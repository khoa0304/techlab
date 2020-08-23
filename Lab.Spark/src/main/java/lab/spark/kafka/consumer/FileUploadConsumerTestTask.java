package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
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
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lab.spark.dto.FileNameAndSentencesDTO;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.model.SparkOpenNlpProcessor;
import opennlp.tools.sentdetect.SentenceModel;

public class FileUploadConsumerTestTask implements Serializable {

	private Logger logger = LoggerFactory.getLogger(FileUploadConsumerTask.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SparkOpenNlpProcessor sparkOpenNLP;
	private SentenceModel sentenceModel;

	public FileUploadConsumerTestTask(SparkConf sparkConfig, Map<String, Object> configMap, String topicName,
			SparkOpenNlpProcessor sparkOpenNLP, SentenceModel openNLPConfig) throws InterruptedException {

		this.sparkOpenNLP = sparkOpenNLP;
		this.sentenceModel = openNLPConfig;

		try {
			processFileUpload(sparkConfig, configMap, topicName);
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	private Map<String, String[]> sentencesPerFileName = new ConcurrentHashMap<>();

	private void processFileUpload(SparkConf sparkConfig, Map<String, Object> configMap, String topicName)
			throws InterruptedException {

		final StructType schema = DataTypes.createStructType(
				new StructField[] { DataTypes.createStructField("FileName", DataTypes.StringType, true),
						DataTypes.createStructField("FileContent", DataTypes.StringType, true) });

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConfig, Durations.seconds(15));
		jssc.checkpoint("./checkpoint/");

		// Start reading messages from Kafka and get DStream
		final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(Arrays.asList(topicName), configMap));

		// Read value of each message from Kafka and return it
		JavaDStream<FileUploadContentDTO> lines = stream
				.map(new Function<ConsumerRecord<String, String>, FileUploadContentDTO>() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public FileUploadContentDTO call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
						ObjectMapper objectMapper = new ObjectMapper();
						FileUploadContentDTO fileUploadContent = objectMapper.readValue(kafkaRecord.value(),
								FileUploadContentDTO.class);
						return fileUploadContent;
					}
				});

		final SparkSession sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();

		lines.foreachRDD(new VoidFunction<JavaRDD<FileUploadContentDTO>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<FileUploadContentDTO> rdd) throws Exception {

				JavaRDD<Row> rowRDD = rdd.map(new Function<FileUploadContentDTO, Row>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Row call(FileUploadContentDTO v1) throws Exception {
						Row row = RowFactory.create(v1.getFileName(), v1.getFileContent());
						return row;
					}
				});

				Dataset<Row> msgDataFrame = sparkSession.createDataFrame(rowRDD, schema);

				Dataset<FileNameAndSentencesDTO> sentencesDataset = sparkOpenNLP
						.processContentUsingOpenkNLP(sparkSession, sentenceModel, msgDataFrame);
				
	//			long count = sentencesDataset.count();

//				logger.info("Count ", count);
				List<FileNameAndSentencesDTO> list = sentencesDataset.collectAsList();

				if (list.size() > 0) {

					for (FileNameAndSentencesDTO fileNameAndSentencesDTO : list) {

						logger.info("\n\n=============================================");

						logger.info("Number of sentences: " + fileNameAndSentencesDTO.getSentences().length);
						Arrays.asList(fileNameAndSentencesDTO.getSentences()).forEach(s -> System.out.println(s));
						// sentencesPerFileName.put(row, value)
						logger.info("=============================================");
					}
				}
			}
		});

		try {
			jssc.start();
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			logger.error("", e);
		}

	}
}
