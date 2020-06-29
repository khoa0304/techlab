package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
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
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import scala.Tuple2;

public class FileUploadConsumerTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileUploadConsumerTask(SparkConf sparkConfig, Map<String, Object> configMap, String topicName)
			throws InterruptedException {
		processFileUpload(sparkConfig, configMap, topicName);
	}

	public void processFileUpload(SparkConf sparkConfig, Map<String, Object> configMap, String topicName)
			throws InterruptedException {

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConfig, Durations.seconds(15));

		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), configMap));

		// Read value of each message from Kafka and return it
		JavaDStream<String> lines = stream.map(new Function<ConsumerRecord<String, String>, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
				return kafkaRecord.value();
			}
		});

		// Break every message into words and return list of words
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Iterator<String> call(String line) throws Exception {
				System.out.println("\n\n\n\n===================================");
				System.out.println("======> Line : "+line);
				System.out.println("\n\n\n\n===================================");
				return Arrays.asList(line.split(" ")).iterator();
			}
		});

//		// Take every word and return Tuple with (word,1)
		JavaPairDStream<String, Integer> wordMap = words.mapToPair(new PairFunction<String, String, Integer>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, Integer> call(String word) throws Exception {
				return new Tuple2<>(word, 1);
			}
		});

		// Count occurance of each word
		JavaPairDStream<String, Integer> wordCount = wordMap.reduceByKey(new Function2<Integer, Integer, Integer>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Integer call(Integer first, Integer second) throws Exception {
				return first + second;
			}
		});

		wordCount.foreachRDD(new VoidFunction<JavaPairRDD<String, Integer>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaPairRDD<String, Integer> rdd) throws Exception {

				StructType schema = DataTypes.createStructType(
						new StructField[] { 
								DataTypes.createStructField("Word", DataTypes.StringType, true),
								DataTypes.createStructField("Count", DataTypes.IntegerType, true) });

				JavaRDD<Row> rowRDD = rdd.map(new Function<Tuple2<String, Integer>, Row>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Row call(Tuple2<String, Integer> v1) throws Exception {
						Row row = RowFactory.create(v1._1, v1._2);
						return row;
					}
				});

				SparkSession sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();

				Dataset<Row> msgDataFrame = sparkSession.createDataFrame(rowRDD,schema);
				long count = msgDataFrame.count();
				if(count > 0) {
					System.out.println("\n\n\n\n===================================");
					System.out.println("======> Dataset Size: "+msgDataFrame.count());
					System.out.println("\n\n\n\n===================================");
					msgDataFrame.show();	
				}
				

			}
		});

		jssc.start();
		jssc.awaitTermination();
	}
}
