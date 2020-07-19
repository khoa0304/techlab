package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lab.spark.dto.FileUploadContent;
import lab.spark.model.SparkOpenNlpProcessor;
import opennlp.tools.sentdetect.SentenceModel;

public class FileUploadConsumerTestTask implements Serializable {

	private Logger logger = LoggerFactory.getLogger(FileUploadConsumerTask.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SparkOpenNlpProcessor sparkOpenNLP;
	private SentenceModel sentenceModel;

	public FileUploadConsumerTestTask(
			SparkConf sparkConfig, 
			Map<String, Object> configMap, 
			String topicName,
			SparkOpenNlpProcessor sparkOpenNLP,
			SentenceModel openNLPConfig)
			throws InterruptedException {
		
		this.sparkOpenNLP = sparkOpenNLP;
		this.sentenceModel = openNLPConfig;
		
		try {
			processFileUpload(sparkConfig, configMap, topicName);	
		}catch(Exception e) {
			logger.error("", e);
		}
		
	}

	
	private void processFileUpload(SparkConf sparkConfig, Map<String, Object> configMap, String topicName)
			throws InterruptedException {

		final StructType schema = DataTypes.createStructType(
				new StructField[] { 
						DataTypes.createStructField("FileName", DataTypes.StringType, true),
						DataTypes.createStructField("FileContent", DataTypes.StringType, true)});

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConfig, Durations.seconds(15));

		jssc.checkpoint("./");
		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), configMap));

		// Read value of each message from Kafka and return it
		JavaDStream<FileUploadContent> lines = stream.map(new Function<ConsumerRecord<String, String>, FileUploadContent>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public FileUploadContent call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
				ObjectMapper objectMapper = new ObjectMapper();
				FileUploadContent fileUploadContent = objectMapper.readValue(kafkaRecord.value(), FileUploadContent.class);
				//logger.info(fileUploadContent.toString());
				return fileUploadContent;
			}
		});
		
		
	
		lines.foreachRDD(new VoidFunction<JavaRDD<FileUploadContent>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<FileUploadContent> rdd) throws Exception {

				
				JavaRDD<Row> rowRDD = rdd.map(new Function<FileUploadContent, Row>() {
				
					private static final long serialVersionUID = 1L;

					@Override
					public Row call(FileUploadContent v1) throws Exception {
						Row row = RowFactory.create(v1.getFileName(),v1.getFileContent());
						return row;
					}
				});

				SparkSession sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();

				Dataset<Row> msgDataFrame = sparkSession.createDataFrame(rowRDD, schema);
				long count = msgDataFrame.count();
				
				if(count > 0) {
					logger.info("\n\n\n\n===================================");
					logger.info("======> Dataset Size: "+ count);
					logger.info("\n\n\n\n===================================");
				
					Dataset<String[]> sentencesDataset = 
							sparkOpenNLP.processContentUsingOpenkNLP(
									sparkSession,sentenceModel, msgDataFrame);
					
					List<String[]> list = sentencesDataset.collectAsList();
					
					for(String[] sentences : list) {
						logger.info("\n\n=============================================");
						
						logger.info("Number of sentences: "+ sentences.length);
						Arrays.stream(sentences).forEach(num -> logger.info(num));
						logger.info("=============================================");
					}
				}
			}
		});

		

		jssc.start();
		jssc.awaitTermination();
	}
}
