package lab.spark.kafka.consumer.segmentgroup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
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
import lab.spark.kafka.consumer.CommonSparkConsumerConfig;
import lab.spark.kafka.consumer.function.SentenceExtractionFunction;
import lab.spark.kafka.consumer.function.TextFileUploadProcessingFunction;
import lab.spark.kafka.producer.KafkaProducerForSpark;

public class SentenceCountSegmentGroup extends CommonSparkConsumerConfig 
				implements Serializable,SegmentGroup<SentencesDTO> {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(SentenceCountSegmentGroup.class);

	public static final String CONSUMER_GROUP_NAME = "SENTENCE-COUNT-CONSUMER-GROUP";
	
	final StructType schema = DataTypes.createStructType(
			new StructField[] { 
					DataTypes.createStructField("fileName", DataTypes.StringType, true),
					DataTypes.createStructField("totalSentences", DataTypes.IntegerType, true)
//					DataTypes.createStructField("wordarray", DataTypes.createArrayType(DataTypes.StringType), true)});
					});
	
	public JavaDStream<SentencesDTO> streamTextContent(
			SparkSession sparkSession , 
			JavaStreamingContext jssc,
			String kafkaServerList,
			String topicName,
			String sparkStreamingSinkTopicList){
		
		Map<String, Object> configMap = 
				configConsumerGroupName(kafkaServerList, CONSUMER_GROUP_NAME);
		
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
            Dataset<Row> dataset = sparkSession.createDataFrame(totalSentencesPerFile, schema);
            
            dataset.createOrReplaceTempView("table");
			Dataset<Row> topTotalSentencesPerFile = sparkSession.sql("select fileName, totalSentences from table order by totalSentences desc limit 20");
		
			topTotalSentencesPerFile.selectExpr("CAST(fileName AS STRING) AS key", "CAST(totalSentences AS STRING) AS value")
			  .write()
			  .format("kafka")
			  .option("kafka.bootstrap.servers", kafkaServerList)
			  .option("topic", sparkStreamingSinkTopicList)
			  .save();
		
		});
	
		return sentencesDStream;
	}

	@Override
	public JavaDStream<SentencesDTO> streamTextContent(SparkSession sparkSession,
			JavaStreamingContext javaStreamingContext, String kafkaServerList, String topicName,
			String sparkStreamingSinkTopicList, Broadcast<KafkaProducerForSpark> kafkaProducerBroadcast) {
		return this.streamTextContent(sparkSession, javaStreamingContext, kafkaServerList, topicName, sparkStreamingSinkTopicList);
	}
}
