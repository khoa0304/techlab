package lab.spark.kafka.consumer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
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

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lab.spark.cassandra.dao.SentenceWordsWriter.SentenceWordsWriterFactory;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.nlp.util.NlpUtil;
import lab.spark.task.SentenceDetectTask;
import lab.spark.task.TokenizeSentenceTask;
import lab.spark.task.WordStemTask;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;
import scala.Tuple2;

public class FileUploadConsumerTestTask implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(FileUploadConsumerTestTask.class);

	private SentenceWordsWriterFactory sentenceWordsWriterFactory = new SentenceWordsWriterFactory();

	final StructType schema = DataTypes.createStructType(
			new StructField[] { 
					DataTypes.createStructField("word", DataTypes.StringType, true),
					DataTypes.createStructField("count", DataTypes.LongType, true)
//					DataTypes.createStructField("wordarray", DataTypes.createArrayType(DataTypes.StringType), true)});
					});
	
	public void processFileUpload(
			SparkConf sparkConfig, 
			Map<String, Object> configMap, 
			String topicName,
			String sparkStreamingSinkTopicList)throws InterruptedException {
		
		final String kafkaServerList = (String) configMap.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
		
		SparkSession sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();
		
		JavaStreamingContext jssc = 
				new JavaStreamingContext(JavaSparkContext.fromSparkContext(sparkSession.sparkContext()), Durations.seconds(30));
		jssc.checkpoint("./checkpoint/");
		
		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), configMap));

		// Read value of each message from Kafka and return it
		JavaDStream<FileUploadContentDTO> fileUploadContentDTODStream = stream
				.map(new Function<ConsumerRecord<String, String>, FileUploadContentDTO>() {
					private static final long serialVersionUID = 1L;

					@Override
					public FileUploadContentDTO call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
						ObjectMapper objectMapper = new ObjectMapper();
						FileUploadContentDTO fileUploadContent = objectMapper.readValue(kafkaRecord.value(),
								FileUploadContentDTO.class);
						return fileUploadContent;
					}
				});

		JavaDStream<SentencesDTO> sentencesDStream = fileUploadContentDTODStream
				.map(new Function<FileUploadContentDTO, SentencesDTO>() {

					private static final long serialVersionUID = 1L;
					private SentenceModel sentenceModel = null;

					@Override
					public SentencesDTO call(FileUploadContentDTO fileUploadContentDTO) throws Exception {

						if (sentenceModel == null) {
							sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));
							logger.info("Initialize Sentence Model");
						}

						SentenceDetectTask sentenceDetectTask = new SentenceDetectTask();
						SentencesDTO sentencesDTO = sentenceDetectTask.extractSentencesFromContent(sentenceModel,
								fileUploadContentDTO);

						return sentencesDTO;
					}

				});

		JavaDStream<WordsPerSentenceDTO[]> wordsDStream = sentencesDStream
				.map(new Function<SentencesDTO, WordsPerSentenceDTO[]>() {

					private static final long serialVersionUID = 1L;
					private TokenizerModel tokenizerModel = null;

					@Override
					public WordsPerSentenceDTO[] call(SentencesDTO sentencesDTO) throws Exception {

						if (tokenizerModel == null) {
							tokenizerModel = new TokenizerModel(
									new File("/opt/spark-data/opennlp/models/en-token.bin"));
							logger.info("Initialize TokenizerModel");
						}

						TokenizeSentenceTask tokenizeSentenceTask = new TokenizeSentenceTask();

						Map<String, String[]> wordsGroupBySentence = tokenizeSentenceTask
								.tokenizeSentence(tokenizerModel, sentencesDTO);

						WordsPerSentenceDTO[] wordsPerSentenceDTOs = new WordsPerSentenceDTO[wordsGroupBySentence
								.size()];

						int index = 0;
						for (Map.Entry<String, String[]> entry : wordsGroupBySentence.entrySet()) {

							WordsPerSentenceDTO wordsPerSentenceDTO = new WordsPerSentenceDTO(
									sentencesDTO.getFileName(), entry.getKey(), Arrays.asList(entry.getValue()));

							wordsPerSentenceDTOs[index++] = wordsPerSentenceDTO;
						}

						return wordsPerSentenceDTOs;
					}

				});

		final Set<String> PUNCTUATION_SET = NlpUtil.getPunctuationSet();

		JavaDStream<WordsPerSentenceDTO[]> stemsDStream = wordsDStream
				.map(new Function<WordsPerSentenceDTO[], WordsPerSentenceDTO[]>() {

					private static final long serialVersionUID = 1L;
					private POSModel posModel = null;

					@Override
					public WordsPerSentenceDTO[] call(WordsPerSentenceDTO[] wordsGroupBySentenceList) throws Exception {

						if (posModel == null) {
							posModel = new POSModel(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
							logger.info("Initialize PostModel");
						}

						for (WordsPerSentenceDTO entry : wordsGroupBySentenceList) {

							WordStemTask wordStemTask = new WordStemTask();
							List<String> words = entry.getWords();
							String[] stems = wordStemTask.lemmatatizer(posModel,
									words.toArray(new String[words.size()]));
							int index = 0;
							for (String stem : stems) {

								if (stem.equalsIgnoreCase("O")) {
									String originalWord = words.get(index);
									logger.debug("Replace Stem 0 with {} ",
											PUNCTUATION_SET.contains(originalWord) ? "" : originalWord);
									stems[index] = words.get(index);
								}
								index++;
							}
							logger.info("Total Words {} - Total Stems {} ", words.size(), stems.length);
							entry.setWords(Arrays.asList(stems));
						}

						return wordsGroupBySentenceList;
					}
				});


	
		// final SparkSession sparkSession =
		// SparkSession.builder().config(sparkConfig).getOrCreate();

		stemsDStream.foreachRDD(new VoidFunction<JavaRDD<WordsPerSentenceDTO[]>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<WordsPerSentenceDTO[]> rdd) throws Exception {

				JavaRDD<WordsPerSentenceDTO> wordsPerJavaRDD = rdd
						.mapPartitions(new FlatMapFunction<Iterator<WordsPerSentenceDTO[]>, WordsPerSentenceDTO>() {

							private static final long serialVersionUID = 1L;

							@Override
							public Iterator<WordsPerSentenceDTO> call(Iterator<WordsPerSentenceDTO[]> t)
									throws Exception {
								List<WordsPerSentenceDTO> list = new ArrayList<WordsPerSentenceDTO>();
								while (t.hasNext()) {
									WordsPerSentenceDTO[] wordsPerSentenceDTOs = t.next();
									logger.info("Number of wordsPerSentenceDTOs ", wordsPerSentenceDTOs.length);
									list.addAll(Arrays.asList(wordsPerSentenceDTOs));
								}
								return list.iterator();
							}
						});

				CassandraJavaUtil.javaFunctions(wordsPerJavaRDD)
						.writerBuilder("lab", "sentencewords", sentenceWordsWriterFactory).saveToCassandra();

				logger.info("Finished persisting RDD to cassandra");
				
				JavaRDD<String> wordsRdd = wordsPerJavaRDD.mapPartitions(new FlatMapFunction<Iterator<WordsPerSentenceDTO>, String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Iterator<String> call(Iterator<WordsPerSentenceDTO> t) throws Exception {
						
						List<String> list = new ArrayList<String>();
						while(t.hasNext()) {
							WordsPerSentenceDTO wordsPerSentenceDTO = t.next();
							List<String> words = wordsPerSentenceDTO.getWords();
							list.addAll(words);
						}
							
						return list.iterator();
					}
				});
				
				JavaPairRDD<String, Integer> wordCountsPairRdd = wordsRdd.mapToPair(new PairFunction<String, String, Integer>() {
					
					private static final long serialVersionUID = 1L;
					@Override
					public Tuple2<String, Integer> call(String word) {
						return new Tuple2<String,Integer>(word, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>() {
				
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
				
				topWordsCount.show();
				
//				topWordsCount.selectExpr("CAST(current_timestamp() AS STRING) AS key", "CAST(count AS STRING) AS value")
//				  .writeStream()
//				  .format("kafka")
//				  .option("kafka.bootstrap.servers", kafkaServerList)
//				  .option("topic", sparkStreamingSinkTopicList)
//				  .start();
				
				topWordsCount.writeStream().format("console").start();
				
				//sendDataSetToDashboard(topWordsCount);
				
			}

			

		});



		try {
			jssc.start();
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			logger.error("", e);
		}

	}
	
	
	
	
//	private void sendDataSetToDashboard(
//			Dataset<Row> dataset) {
//		
//		String commaSeparatedWords = 
//				dataset.select("word").
//				collectAsList().stream().
//				map(new java.util.function.Function<Row, String>() {
//
//					@Override
//					public String apply(Row row) {
//						
//						return row.getString(0);
//					}
//				}).
//				collect(Collectors.joining(","));
//		
//		
//		String commaSeparatedCounts  = 
//				dataset.select("count").
//				collectAsList().stream().
//				map(new java.util.function.Function<Row, String>() {
//
//					@Override
//					public String apply(Row row) {
//						
//						return String.valueOf(row.getInt(0));
//					}
//				}).
//				collect(Collectors.joining(","));
//		
//		logger.info("Labels: {} - Count {} ", commaSeparatedWords,commaSeparatedCounts);
//					
//	}
}
