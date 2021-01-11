package lab.spark.kafka.consumer.segmentgroup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import lab.common.file.dto.SentenceWordDto;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.kafka.consumer.CommonSparkConsumerConfig;
import lab.spark.kafka.consumer.function.SentenceExtractionFunction;
import lab.spark.kafka.consumer.function.StemFunction;
import lab.spark.kafka.consumer.function.TextFileUploadProcessingFunction;
import lab.spark.kafka.consumer.function.WordPerSentenceExtractionArrayFunction;
import lab.spark.kafka.consumer.function.WordPerSentenceExtractionFunction;
import lab.spark.kafka.producer.KafkaProducerForSpark;

public class SentenceSegmentGroup extends CommonSparkConsumerConfig 
						implements Serializable,SegmentGroup<WordsPerSentenceDTO> {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(StemWordSegmentGroup.class);

	public static final String CONSUMER_GROUP_NAME = "SENTENCE-COUNT-CONSUMER-GROUP";
	
	final StructType schema = DataTypes.createStructType(
			new StructField[] { 
					DataTypes.createStructField("fileName", DataTypes.StringType, true),
					DataTypes.createStructField("sentence", DataTypes.StringType, true),
					DataTypes.createStructField("words", DataTypes.createArrayType(DataTypes.StringType,true), true)
					});

	public JavaDStream<WordsPerSentenceDTO> streamTextContent(
			SparkSession sparkSession , 
			JavaStreamingContext jssc,
			String kafkaServerList,
			String topicName,
			String sparkStreamingSinkTopicList,
			Broadcast<KafkaProducerForSpark> kafkaProducerBroadcast){
		
		return null;
	}

	@Override
	public JavaDStream<WordsPerSentenceDTO> streamTextContent(
			SparkSession sparkSession,
			JavaStreamingContext javaStreamingContext, 
			String kafkaServerList, String topicName,
			String sparkStreamingSinkTopicList) {
		
		Map<String, Object> configMap = 
				configConsumerGroupName(kafkaServerList, CONSUMER_GROUP_NAME);
		
		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = 
				KafkaUtils.createDirectStream(
						javaStreamingContext,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), 
				configMap));
		
		// Read value of each message from Kafka and return it
		JavaDStream<FileUploadContentDTO> fileUploadContentDTODStream = stream.map(new TextFileUploadProcessingFunction());

		JavaDStream<SentencesDTO> sentencesDStream = fileUploadContentDTODStream.map(new SentenceExtractionFunction());

		JavaDStream<WordsPerSentenceDTO[]> wordsDStream = sentencesDStream.map(new WordPerSentenceExtractionArrayFunction());

		JavaDStream<WordsPerSentenceDTO[]> stemsDStream = wordsDStream.map(new StemFunction());
		
		JavaDStream<WordsPerSentenceDTO> wordCountsJavaPairDStream = 
				stemsDStream.mapPartitions(new WordPerSentenceExtractionFunction());
		
		wordCountsJavaPairDStream.foreachRDD(new VoidFunction<JavaRDD<WordsPerSentenceDTO>>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void call(JavaRDD<WordsPerSentenceDTO> rdd) throws Exception {
				
				//rdd.persist(StorageLevel.MEMORY_ONLY_2());
						
				JavaRDD<Row> totalSentencesPerFile = rdd.map(new Function<WordsPerSentenceDTO, Row>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Row call(WordsPerSentenceDTO wordsPerSentenceDTO) throws Exception {
						Row row = RowFactory.create(
								wordsPerSentenceDTO.getFileName(),
								wordsPerSentenceDTO.getSentence(),
								wordsPerSentenceDTO.getWords().toArray(new String[0]));
						return row;
					}
				});
	
				Dataset<Row> dataset = 
						sparkSession.createDataFrame(
								totalSentencesPerFile.rdd(),
								schema);
			
				dataset.createOrReplaceTempView("WordCountPerSentenceTable");
				Dataset<SentenceWordDto> selectedDataset = sparkSession.sql("select fileName,sentence, words from WordCountPerSentenceTable ").as(Encoders.bean(SentenceWordDto.class));
				
//				selectedDataset.foreach( new ForeachFunction<SentenceWordDto>() {
//				
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					public void call(SentenceWordDto row) throws Exception {
//						
//						String producerPayload = new ObjectMapper().writeValueAsString(row);
//						
//						KafkaProducerForSpark kafkaProducerForSpark = kafkaProducerBroadcast.getValue();
//						Producer<String, String> kafkaProducer = kafkaProducerForSpark.initKafkaProducer();
//						kafkaProducerForSpark.pushToKafka(kafkaProducer, sparkStreamingSinkTopicList, producerPayload); 
//					}
//				});
				
				selectedDataset.selectExpr("CAST(fileName AS STRING) AS key", "to_json(struct(*)) AS value")
				  .write()
				  .format("kafka")
				  .option("kafka.bootstrap.servers", kafkaServerList)
				  .option("topic", sparkStreamingSinkTopicList)
				  .save();
			}
		});
		
		return wordCountsJavaPairDStream;
	}

}
