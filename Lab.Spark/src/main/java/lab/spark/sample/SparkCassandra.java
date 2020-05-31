package lab.spark.sample;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
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

import lab.spark.mllib.JavaTfIdf;


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
		

		tfidfDataset.write().mode(SaveMode.Append).save("/opt/spark-data/tfidf");
		
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
