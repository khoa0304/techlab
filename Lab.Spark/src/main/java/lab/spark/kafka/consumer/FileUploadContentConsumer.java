package lab.spark.kafka.consumer;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
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

				DataTypes.createStructType(
						new StructField[] { DataTypes.createStructField("fileName", DataTypes.StringType, false),
								DataTypes.createStructField("fileContent", DataTypes.StringType, false) });

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					//processFileUpload(sparkConfigService.getSparkSession(FileUploadContentConsumer.class.getName()));
					processFileUpload(sparkConfigService.getSparkConfig(FileUploadContentConsumer.class.getName()));
				} catch (UnknownHostException | InterruptedException e) {
					logger.warn("", e);
				}
			}
		});

	}

	private void processFileUpload(SparkSession spark) throws StreamingQueryException {

		SparkOpenNlpService sparkOpenNlpService = new SparkOpenNlpService();

		Dataset<FileUploadContent> dataset = spark.readStream().format("kafka")
				.option("kafka.bootstrap.servers", kafkaConfig.getKafkaServerList())
				.option("subscribe", kafkaConfig.getKafkaTextFileUploadTopic())
				// .option("kafka.max.partition.fetch.bytes",
				// prop.getProperty("kafka.max.partition.fetch.bytes"))
				// .option("kafka.max.poll.records", prop.getProperty("kafka.max.poll.records"))
				.load().selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), fileUploadContentSchema).as("json"))
				.select("json.*").as(Encoders.bean(FileUploadContent.class));

		Dataset<String[]> sentencesDataset = sparkOpenNlpService.extractStringContentSentence(spark,
				openNLPConfig.getSentenceModel(), dataset);

		StreamingQuery query = sentencesDataset.writeStream().outputMode("update").format("console").start();

		// await
		query.awaitTermination();
	}

	private void processFileUpload(SparkConf sparkConfig) throws InterruptedException {

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConfig, Durations.seconds(10));

		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream =
				KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(kafkaConfig.getKafkaTextFileUploadTopic()), getKafkMapProperties()));

		// Read value of each message from Kafka and return it
		JavaDStream<String> lines = stream.map(new Function<ConsumerRecord<String, String>, String>() {
			@Override
			public String call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
				return kafkaRecord.value();
			}
		});

		// Break every message into words and return list of words
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterator<String> call(String line) throws Exception {
				return Arrays.asList(line.split(" ")).iterator();
			}
		});

		words.print();
		
//		// Take every word and return Tuple with (word,1)
//		JavaPairDStream<String, Integer> wordMap = words.mapToPair(new PairFunction<String, String, Integer>() {
//			@Override
//			public Tuple2<String, Integer> call(String word) throws Exception {
//				return new Tuple2<>(word, 1);
//			}
//		});
//
//		// Count occurance of each word
//		JavaPairDStream<String, Integer> wordCount = wordMap.reduceByKey(new Function2<Integer, Integer, Integer>() {
//			@Override
//			public Integer call(Integer first, Integer second) throws Exception {
//				return first + second;
//			}
//		});
//
//		// Print the word count
//		wordCount.print();

		jssc.start();
		jssc.awaitTermination();
	}

	private Map<String, Object> getKafkMapProperties() {

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaServerList());
		kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		//kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		
		return kafkaParams;
		
	}
}
