package lab.spark.kafka.consumer.segmentgroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
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

import lab.common.file.dto.DocumentStatisticDto;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.kafka.consumer.CommonSparkConsumerConfig;
import lab.spark.kafka.consumer.function.SentenceExtractionFunction;
import lab.spark.kafka.consumer.function.TextFileUploadProcessingFunction;
import lab.spark.kafka.consumer.function.WordPerSentenceExtractionArrayFunction;
import lab.spark.kafka.consumer.function.WordPerSentenceExtractionFunction;
import lab.spark.kafka.producer.KafkaProducerForSpark;
import scala.Tuple2;
public class TotalSentenceAndWordPerDocument extends CommonSparkConsumerConfig 
				implements Serializable,SegmentGroup<WordsPerSentenceDTO> {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(TotalSentenceAndWordPerDocument.class);

	public static final String CONSUMER_GROUP_NAME = "SENTENCE-WORD-COUNT-CONSUMER-GROUP";
	
	final StructType schema = DataTypes.createStructType(
			new StructField[] { 
					DataTypes.createStructField("fileName", DataTypes.StringType, true),
					DataTypes.createStructField("totalSentences", DataTypes.IntegerType, true)
//					DataTypes.createStructField("wordarray", DataTypes.createArrayType(DataTypes.StringType), true)});
					});
	
	public JavaDStream<WordsPerSentenceDTO> streamTextContent(
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
		JavaDStream<WordsPerSentenceDTO[]> wordsDStream = sentencesDStream.map(new WordPerSentenceExtractionArrayFunction());
		
		JavaDStream<WordsPerSentenceDTO> allWords = wordsDStream.mapPartitions(new WordPerSentenceExtractionFunction());
		
		allWords.foreachRDD(new VoidFunction<JavaRDD<WordsPerSentenceDTO>>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void call(JavaRDD<WordsPerSentenceDTO> rdd) throws Exception {
				
			
				JavaPairRDD<String,Iterable<WordsPerSentenceDTO>> javaPairRDD1 = rdd.groupBy(w ->w.getFileName());
				
				JavaPairRDD<String, DocumentStatisticDto> javaPairRDD2 = javaPairRDD1.mapPartitionsToPair(
						new PairFlatMapFunction<Iterator<Tuple2<String,Iterable<WordsPerSentenceDTO>>>,String, DocumentStatisticDto>() {

							private static final long serialVersionUID = 1L;

							@Override
							public Iterator<Tuple2<String, DocumentStatisticDto>> call(
									Iterator<Tuple2<String, Iterable<WordsPerSentenceDTO>>> iterator) throws Exception {
								
								List<Tuple2<String, DocumentStatisticDto>> list = new ArrayList<>();
							
								
								while(iterator.hasNext()){
									
									Tuple2<String, Iterable<WordsPerSentenceDTO>> tuple2 = iterator.next();
									String fileName = tuple2._1;
									Iterator<WordsPerSentenceDTO> iterator2 = tuple2._2().iterator();
							
									AtomicInteger wordCounter = new AtomicInteger(0);
									AtomicInteger sentenceCounter = new AtomicInteger(0);
									while(iterator2.hasNext()) {
										WordsPerSentenceDTO wordsPerSentenceDTO = iterator2.next();
										wordCounter.addAndGet(wordsPerSentenceDTO.getTotalStems());
										sentenceCounter.incrementAndGet();
									}
									DocumentStatisticDto documentStatisticDto = new DocumentStatisticDto();
									documentStatisticDto.setFileName(fileName);
									documentStatisticDto.setTotalWords(wordCounter.get());
									documentStatisticDto.setTotalSentences(sentenceCounter.get());
									
									list.add(new Tuple2<>(fileName, documentStatisticDto));
								}
								return list.iterator();
							}
				});
				
				Dataset<Row> dataset = 
						sparkSession.createDataset(
								JavaPairRDD.toRDD(javaPairRDD2),
								Encoders.tuple(Encoders.STRING(),Encoders.bean(DocumentStatisticDto.class))).toDF("fileName","documentStatisticDto");
			
				dataset.createOrReplaceTempView("TotalSentenceAndWordTable");
				Dataset<DocumentStatisticDto> selectedDataset = sparkSession.sql(
						"select documentStatisticDto.fileName as fileName,"
						+ " documentStatisticDto.totalSentences as totalSentences, "
						+ " documentStatisticDto.totalWords as totalWords "
						+ " from TotalSentenceAndWordTable ").as(Encoders.bean(DocumentStatisticDto.class));
				
				Dataset<DocumentStatisticDto> persistedRDD = selectedDataset.persist();
				
				long count = persistedRDD.count();
				
				if(count > 0) {
				
					selectedDataset.selectExpr("CAST(fileName AS STRING) AS key", "to_json(struct(*)) AS value")
					  .write()
					  .format("kafka")
					  .option("kafka.bootstrap.servers", kafkaServerList)
					  .option("topic", sparkStreamingSinkTopicList)
					  .save();
				}
				
				selectedDataset.unpersist();
				
			}
		});

	
		return allWords;
	}

	@Override
	public JavaDStream<WordsPerSentenceDTO> streamTextContent(SparkSession sparkSession,
			JavaStreamingContext javaStreamingContext, String kafkaServerList, String topicName,
			String sparkStreamingSinkTopicList, Broadcast<KafkaProducerForSpark> kafkaProducerBroadcast) {
		return this.streamTextContent(sparkSession, javaStreamingContext, kafkaServerList, topicName, sparkStreamingSinkTopicList);
	}
}
