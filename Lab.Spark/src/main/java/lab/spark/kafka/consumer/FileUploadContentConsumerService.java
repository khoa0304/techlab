package lab.spark.kafka.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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

import lab.spark.config.KafkaConfigService;
import lab.spark.config.OpenNLPConfigService;
import lab.spark.config.SparkConfigService;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.model.SparkOpenNlpProcessor;

@Service
public class FileUploadContentConsumerService {

	private Logger logger = LoggerFactory.getLogger(FileUploadContentConsumerService.class);

	@Autowired
	private SparkConfigService sparkConfigService;

	@Autowired
	private KafkaConfigService kafkaConfig;

	@Autowired
	private OpenNLPConfigService openNLPConfig;

	private StructType fileUploadContentSchema;

	@PostConstruct
	private void initialize() {
		fileUploadContentSchema =

				DataTypes.createStructType(
						new StructField[] { DataTypes.createStructField("fileName", DataTypes.StringType, false),
								DataTypes.createStructField("fileContent", DataTypes.StringType, false) });

		startSparkKafkaStreaming();

	}

	private void processFileUpload(SparkSession spark) throws StreamingQueryException {

		SparkOpenNlpProcessor sparkOpenNlpService = new SparkOpenNlpProcessor();

		Dataset<FileUploadContentDTO> dataset = spark.readStream().format("kafka")
				.option("kafka.bootstrap.servers", kafkaConfig.getKafkaServerList())
				.option("subscribe", kafkaConfig.getKafkaTextFileUploadTopic())
				// .option("kafka.max.partition.fetch.bytes",
				// prop.getProperty("kafka.max.partition.fetch.bytes"))
				// .option("kafka.max.poll.records", prop.getProperty("kafka.max.poll.records"))
				.load().selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), fileUploadContentSchema).as("json"))
				.select("json.*").as(Encoders.bean(FileUploadContentDTO.class));

		Dataset<String[]> sentencesDataset = sparkOpenNlpService.extractStringContentSentence(spark,
				openNLPConfig.getSentenceModel(), dataset);

		StreamingQuery query = sentencesDataset.writeStream().outputMode("update").format("console").start();

		// await
		query.awaitTermination();
	}

	public void startSparkKafkaStreaming() {

		final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {

				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				t.setName("Spark-Kafka-Consumer-");
				return t;

				
			}
		});
				
		String topicName = kafkaConfig.getKafkaTextFileUploadTopic();
		
		KafkaConsumerTask kafkaConsumerTask = new KafkaConsumerTask(sparkConfigService,getKafkMapProperties(topicName), topicName,openNLPConfig);
		
		scheduledExecutorService.schedule(kafkaConsumerTask,5,TimeUnit.SECONDS);
		
		logger.info("Finished scheduling Spark-Kafka Consumer for streaming from topic {} ",topicName);

	}

	private Map<String, Object> getKafkMapProperties(String topicName) {

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaServerList());
		kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, topicName +"- SparkGroup");
		// kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

		return kafkaParams;

	}
}
