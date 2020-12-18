package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
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

import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.kafka.consumer.function.SentenceExtractionFunction;
import lab.spark.kafka.consumer.function.TextFileUploadProcessingFunction;

public class SentenceCountKafkaStreamConsumer extends CommonSparkConsumerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(SentenceCountKafkaStreamConsumer.class);

	public static final String CONSUMER_GROUP_NAME = "SENTENCE-COUNT-CONSUMER-GROUP";
	
	final StructType schema = DataTypes.createStructType(
			new StructField[] { 
					DataTypes.createStructField("fileName", DataTypes.StringType, true),
					DataTypes.createStructField("totalSentences", DataTypes.LongType, true)
//					DataTypes.createStructField("wordarray", DataTypes.createArrayType(DataTypes.StringType), true)});
					});
	
	public void processFileUpload(
			SparkConf sparkConfig, 
			Map<String, Object> configMap, 
			String topicName,
			String sparkStreamingSinkTopicList)throws InterruptedException {
		
		configConsumerGroupName(configMap, CONSUMER_GROUP_NAME);
		
		final String kafkaServerList = (String) configMap.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
		
		SparkSession sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();
		
		JavaStreamingContext jssc = 
				new JavaStreamingContext(JavaSparkContext.fromSparkContext(sparkSession.sparkContext()), Durations.seconds(30));
		jssc.checkpoint("./checkpoint/stream/sentencecount");
		
		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), configMap));

		// Read value of each message from Kafka and return it
		JavaDStream<FileUploadContentDTO> fileUploadContentDTODStream = stream.map(new TextFileUploadProcessingFunction());

		JavaDStream<SentencesDTO> sentencesDStream = fileUploadContentDTODStream.map(new SentenceExtractionFunction());
		

		sentencesDStream.foreachRDD((JavaRDD<SentencesDTO> sentenceTextRDD) -> {
			
			JavaRDD<Row> totalSentencesPerFile = sentenceTextRDD.map(new Function<SentencesDTO, Row>() {

				private static final long serialVersionUID = 1L;

				@Override
				public Row call(SentencesDTO sentencesDTO) throws Exception {
					Row row = RowFactory.create(sentencesDTO.getFileName(),sentencesDTO.getSentences().length);
					return row;
				}
			});
			
			// Create Spark Session
            SparkSession session = SparkSession.builder().config(sparkConfig).getOrCreate();
            Dataset<Row> dataset = session.createDataFrame(totalSentencesPerFile, schema);
            
            dataset.createOrReplaceTempView("table");
			Dataset<Row> topTotalSentencesPerFile = sparkSession.sql("select fileName, totalSentences from table order by totalSentences desc limit 20");
		
			topTotalSentencesPerFile.selectExpr("CAST(fileName AS STRING) AS key", "CAST(totalSentences AS STRING) AS value")
			  .writeStream()
			  .format("kafka")
			  .option("kafka.bootstrap.servers", kafkaServerList)
			  .option("topic", sparkStreamingSinkTopicList)
			  .start();
		
		});
	}
}
