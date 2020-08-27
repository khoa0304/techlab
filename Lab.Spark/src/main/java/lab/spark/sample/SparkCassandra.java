package lab.spark.sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.johnsnowlabs.nlp.DocumentAssembler;
import com.johnsnowlabs.nlp.Finisher;
import com.johnsnowlabs.nlp.annotators.LemmatizerModel;
import com.johnsnowlabs.nlp.annotators.Normalizer;
import com.johnsnowlabs.nlp.annotators.Tokenizer;
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector;

import lab.spark.config.OpenNLPConfigService;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.mllib.JavaTfIdf;
import lab.spark.model.SparkOpenNlpProcessor;


public class SparkCassandra {
	
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	public JavaRDD<String> perform(SparkSession spark, String keyspace, String tableName, String columnName) {

		SparkContext sparkContext = spark.sparkContext();
		JavaRDD<String> stringRDD = CassandraJavaUtil.javaFunctions(sparkContext)
				.cassandraTable(keyspace, tableName, 
						CassandraJavaUtil.mapColumnTo(String.class)).select(columnName)
		 .where("file_name = ? ", " UTResume.pdf.txt");
		// .withAscOrder().limit(5l);
		

		
		return stringRDD;
	}

	public Dataset<Row> performDataFrame(SparkSession spark, String keyspace, String tableName,String clusterName) {

		Map<String,String> configMap = configMap(keyspace, tableName, clusterName);
		Dataset<Row> dataset = spark.read().format("org.apache.spark.sql.cassandra")
				.options(configMap).load()
				.select("file_name", "content").where("file_name = 'UTResume.pdf.txt'");
		
		StructType schema = new StructType(
				new StructField[] { 
						new StructField("label", DataTypes.StringType, false, Metadata.empty()),
						new StructField("sentence", DataTypes.StringType, false, Metadata.empty()) 
						});
		
		JavaTfIdf javaTfIdf = new JavaTfIdf();
		Dataset<Row> tfidfDataset = javaTfIdf.tFIdf(dataset, schema, spark);
		
		tfidfDataset.show(false);
		//
		configMap = configMap(keyspace, "document_tfidf", clusterName);
		configMap.put("confirm.truncate", "true");

		tfidfDataset.write().mode(SaveMode.Append).save("/opt/spark-data/tfidf");
		
		//tfidfDataset.write().format("org.apache.spark.sql.cassandra").options(configMap).mode(SaveMode.Append).saveAsTable("document_tfidf");
		
		
		configMap = configMap(keyspace, "document_content", clusterName);
		configMap.put("confirm.truncate", "true");
		dataset.write().format("org.apache.spark.sql.cassandra").options(configMap).mode(SaveMode.Append).saveAsTable("document_content");
		
		//dataset.createOrReplaceTempView("document_pdf_view");
		//dataset = spark.sql("select file_name from document_pdf_view");
	
		return dataset;
	}
	
	public Dataset<Row> performSparkSQLUsingView1(SparkSession spark, String keyspace, 
			String tableName,String clusterName, String columnName) {
		
		String createTempViewQuery ="CREATE TEMPORARY VIEW temp "+
			     " USING org.apache.spark.sql.cassandra" +
			     " OPTIONS (" +
			     " table \""+ tableName +"\", "+
			     " keyspace \"" + keyspace+"\", " +
			     " cluster \""+ clusterName +"\", " +
			     " pushdown \"true\")";
		spark.sql(createTempViewQuery);
		Dataset<Row> dataset = spark.sql("select "+ columnName + " from temp where file_name = 'UTResume.pdf.txt'").toDF();
		
		dataset.show();
		return dataset;
	}
	

	public Dataset<SentencesDTO> processContent(
			SparkSession spark,
			SparkOpenNlpProcessor sparkOpenNLP,
			OpenNLPConfigService openNLPConfig,
			String keyspace, 
			String tableName,String clusterName,String fileName) throws IOException {

		Map<String,String> configMap = configMap(keyspace, tableName, clusterName);
		
		String fileNameCol = "fileName";
		String fileContentCol = "fileContent";
		
		if(fileName !=null) {
			
			Dataset<FileUploadContentDTO> dataset = 
					spark.read().format("org.apache.spark.sql.cassandra")
					.options(configMap).load()
					.select(fileNameCol,"fileContent")
					.where(fileContentCol + " = '"+fileName+"'")
					.as(Encoders.bean(FileUploadContentDTO.class));
			Dataset<SentencesDTO> sentencesDataset = 
					sparkOpenNLP.processContentUsingOpenkNLP(
							spark,openNLPConfig.getSentenceModel(), dataset.select(fileContentCol));
			return sentencesDataset;
		}
		else {
			
			
			Dataset<FileUploadContentDTO> dataset = 
					spark.read().format("org.apache.spark.sql.cassandra")
					.options(configMap).load()
					.select("file_name","content")
					.as(Encoders.bean(FileUploadContentDTO.class));
			
			Dataset<SentencesDTO> sentencesDataset = 
					sparkOpenNLP.processContentUsingOpenkNLP(
							spark,openNLPConfig.getSentenceModel(), dataset.select(fileContentCol));
			return sentencesDataset;
			
		}
	
	
	}
	
	public Dataset<Row> processContentUsingSparkNLP(
			SparkSession spark, String keyspace, String tableName,String clusterName,
			String fileName) {

		Map<String,String> configMap = configMap(keyspace, tableName, clusterName);
		Dataset<Row> dataset = spark.read().format("org.apache.spark.sql.cassandra")
				.options(configMap).load()
				.select("file_name", "content").where("file_name = '"+fileName+"'");
		
		configMap = configMap(keyspace, "document_content", clusterName);
		configMap.put("confirm.truncate", "true");
		dataset.write().format("org.apache.spark.sql.cassandra").options(configMap).mode(SaveMode.Append).saveAsTable("document_content");
	
		DocumentAssembler document_assembler = 
				(DocumentAssembler) new DocumentAssembler().setInputCol("content").setOutputCol("document");

		SentenceDetector sentence_detector = 
				(SentenceDetector) ((SentenceDetector) new SentenceDetector().setInputCols(new String[] {"document"})).setOutputCol("sentence");
		
		Tokenizer tokenizer = 
				(Tokenizer)((Tokenizer) new Tokenizer().setInputCols(new String[] {"sentence"})).setOutputCol("token");

		Normalizer normalizer = 
				(Normalizer)((Normalizer) new Normalizer().setInputCols(new String[]{"token"})).setOutputCol("normalized");

		LemmatizerModel lemmatizer = (LemmatizerModel)((LemmatizerModel) LemmatizerModel.pretrained("lemma_antbnc", "en", "public/models").setInputCols(new String[]{"normalized"})).setOutputCol("lemma");

		Finisher finisher = 
				new Finisher().setInputCols(new String[]{"document", "lemma"}).setOutputCols(new String[]{"document", "lemma"});

		Pipeline pipeline = 
				new Pipeline().setStages(new PipelineStage[]{document_assembler, sentence_detector, tokenizer, normalizer, lemmatizer, finisher});

		// Fit the pipeline to training documents.
		PipelineModel pipelineFit = pipeline.fit(dataset);
		Dataset<Row> finalDataset = pipelineFit.transform(dataset);
		return finalDataset;
	}

	public Map<String,String> configMap(String keyspace, String tableName,String clusterName){
	
		Map<String, String> configMap = new HashMap<>();
		configMap.put("keyspace",keyspace);
		configMap.put("table",tableName);
		if(clusterName != null)
			configMap.put("cluster",clusterName);
		
		return configMap;
		
	}
	
	
//	public void printDataset(Dataset<Row> dataset) {
//		
//		dataset.foreachPartition(new ForeachPartitionFunction<Row>() {
//           private static final long serialVersionUID = 1L;
//
//			public void call(Iterator<Row> t) throws Exception {
//                while (t.hasNext()){
//                    Row row = t.next();
//                    logger.info(row.getString(0));
//                }
//            }
//        });
//	}
	
	
}
