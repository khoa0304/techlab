package lab.spark.kafka.consumer.segmentgroup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.cassandra.dao.SentenceWordsWriter.SentenceWordsWriterFactory;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.kafka.consumer.CommonSparkConsumerConfig;
import lab.spark.kafka.consumer.function.PairWordFunction;
import lab.spark.kafka.consumer.function.SentenceExtractionFunction;
import lab.spark.kafka.consumer.function.StemFunction;
import lab.spark.kafka.consumer.function.TextFileUploadProcessingFunction;
import lab.spark.kafka.consumer.function.WordExtractionFunction;
import lab.spark.kafka.consumer.function.WordPerSentenceExtractionArrayFunction;
import lab.spark.kafka.consumer.function.WordPerSentenceExtractionFunction;

public class StemWordSegmentGroup extends CommonSparkConsumerConfig 
						implements Serializable,SegmentGroup<WordsPerSentenceDTO[]> {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(StemWordSegmentGroup.class);

	private SentenceWordsWriterFactory sentenceWordsWriterFactory = new SentenceWordsWriterFactory();

	public static final String CONSUMER_GROUP_NAME = "WORD-COUNT-CONSUMER-GROUP";
	

	public JavaDStream<WordsPerSentenceDTO[]> streamTextContent(
			SparkSession sparkSession , 
			JavaStreamingContext jssc,
			String kafkaServerList,
			String topicName,
			String sparkStreamingSinkWordTopicList){
		
		Map<String, Object> configMap = 
				configConsumerGroupName(kafkaServerList, CONSUMER_GROUP_NAME);
		
		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = 
				KafkaUtils.createDirectStream(
				jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), 
				configMap));
		
		// Read value of each message from Kafka and return it
		JavaDStream<FileUploadContentDTO> fileUploadContentDTODStream = stream.map(new TextFileUploadProcessingFunction());

		JavaDStream<SentencesDTO> sentencesDStream = fileUploadContentDTODStream.map(new SentenceExtractionFunction());

		JavaDStream<WordsPerSentenceDTO[]> wordsDStream = sentencesDStream.map(new WordPerSentenceExtractionArrayFunction());

		JavaDStream<WordsPerSentenceDTO[]> stemsDStream = wordsDStream.map(new StemFunction());

		stemsDStream.foreachRDD(new VoidFunction<JavaRDD<WordsPerSentenceDTO[]>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<WordsPerSentenceDTO[]> rdd) throws Exception {

				JavaRDD<WordsPerSentenceDTO> wordsPerJavaRDD = rdd.mapPartitions(new WordPerSentenceExtractionFunction());

//				CassandraJavaUtil.javaFunctions(wordsPerJavaRDD)
//						.writerBuilder("lab", "sentencewords", sentenceWordsWriterFactory).saveToCassandra();

				logger.info("Finished persisting RDD to cassandra");
				
				JavaRDD<String> wordsRdd = wordsPerJavaRDD.mapPartitions(new WordExtractionFunction());
				
				JavaPairRDD<String, Integer> wordCountsPairRdd = 
						wordsRdd.mapToPair(new PairWordFunction()).reduceByKey(new Function2<Integer, Integer, Integer>() {
				
					private static final long serialVersionUID = 1L;

					@Override
					public Integer call(Integer i1, Integer i2) {
						return i1 + i2;
					}
				});
		
				Dataset<Row> dataset = 
						sparkSession.createDataset(
								JavaPairRDD.toRDD(wordCountsPairRdd),
								Encoders.tuple(Encoders.STRING(),Encoders.INT())).toDF("word","count");
						
				dataset.createOrReplaceTempView("table");
				Dataset<Row> topWordsCount = sparkSession.sql("select word, count from table order by count desc limit 20");
				
				dataset.write().format("console").save();
				
				topWordsCount.selectExpr("CAST(word AS STRING) AS key", "CAST(count AS STRING) AS value")
				  .write()
				  .format("kafka")
				  .option("kafka.bootstrap.servers", kafkaServerList)
				  .option("topic", sparkStreamingSinkWordTopicList)
				  .save();
			}
		});
		
		return stemsDStream;
	}

}
