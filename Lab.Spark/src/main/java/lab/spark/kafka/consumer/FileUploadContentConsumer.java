package lab.spark.kafka.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lab.spark.config.KafkaConfig;
import lab.spark.config.OpenNLPConfig;
import lab.spark.config.SparkConfigService;
import lab.spark.dto.FileUploadContent;
import lab.spark.model.SparkOpenNlpService;

@Service
public class FileUploadContentConsumer {

	private Logger logger = LoggerFactory.getLogger(FileUploadContentConsumer.class);
	
	@Autowired
	private SparkConfigService sparkConfigService;
	
	@Autowired
	private KafkaConfig kafkaConfig;
	
	@Autowired
	private OpenNLPConfig openNLPConfig;
	
	private StructType fileUploadContentSchema;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	@Autowired
	private void initialize() {
		fileUploadContentSchema =
				
				DataTypes.createStructType(new StructField[] 
						{ 
						DataTypes.createStructField("fileName", DataTypes.StringType, false),
						DataTypes.createStructField("fileContent", DataTypes.StringType, false)
						});
				
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					processFileUpload(sparkConfigService.getSparkSession(FileUploadContentConsumer.class.getName()));
				} catch (StreamingQueryException e) {
					logger.warn("",e);
				}
			}
		});
		
	}
	
	private void processFileUpload(SparkSession spark) throws StreamingQueryException {
		
		SparkOpenNlpService sparkOpenNlpService = new SparkOpenNlpService();
		
		Dataset<FileUploadContent> dataset = spark
			      .readStream()
			      .format("kafka")
			      .option("kafka.bootstrap.servers", kafkaConfig.getKafkaServerList())
			      .option("subscribe", kafkaConfig.getKafkaTextFileUploadTopic())
			     // .option("kafka.max.partition.fetch.bytes", prop.getProperty("kafka.max.partition.fetch.bytes"))
			      //.option("kafka.max.poll.records", prop.getProperty("kafka.max.poll.records"))
			      .load()
			      .selectExpr("CAST(value AS STRING) as message")
			      .select(functions.from_json(functions.col("message"),fileUploadContentSchema).as("json"))
			      .select("json.*")
			      .as(Encoders.bean(FileUploadContent.class)); 
		
		Dataset<String[]> sentencesDataset = sparkOpenNlpService.extractStringContentSentence(spark,openNLPConfig.getSentenceModel(),dataset);
		
		 StreamingQuery query = sentencesDataset.writeStream()
			      .outputMode("update")
			      .format("console")
			      .start();
		 
		 
		 //await
	     query.awaitTermination();
	}
}
