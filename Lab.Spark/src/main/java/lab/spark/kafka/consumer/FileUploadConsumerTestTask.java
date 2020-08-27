package lab.spark.kafka.consumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

import lab.spark.config.OpenNLPConfigService;
import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.model.SparkOpenNlpProcessor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

public class FileUploadConsumerTestTask implements Serializable {

	private Logger logger = LoggerFactory.getLogger(FileUploadConsumerTask.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SparkOpenNlpProcessor sparkOpenNLP;
	private SentenceModel sentenceModel;
	private TokenizerModel tokenizerModel;
	private POSModel posModel;
	
	public FileUploadConsumerTestTask(
			SparkConf sparkConfig, 
			Map<String, Object> configMap, 
			String topicName,
			SparkOpenNlpProcessor sparkOpenNLP, 
			OpenNLPConfigService openNLPConfig) throws InterruptedException {

		this.sparkOpenNLP = sparkOpenNLP;
		this.sentenceModel = openNLPConfig.getSentenceModel();
		this.tokenizerModel = openNLPConfig.getTokenizerModel();
		this.posModel = openNLPConfig.getPOSModel();
	
		
		try {
			processFileUpload(sparkConfig, configMap, topicName);
		} catch (Exception e) {
			logger.error("", e);
		}
	}


	private void processFileUpload(
			SparkConf sparkConfig, 
			Map<String, Object> configMap, String topicName)
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

		

		lines.foreachRDD(new VoidFunction<JavaRDD<FileUploadContentDTO>>() {
			
			final SparkSession sparkSession = SparkSession.builder().config(sparkConfig).getOrCreate();
			
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

				Dataset<SentencesDTO> sentencesDataset = sparkOpenNLP
						.processContentUsingOpenkNLP(sparkSession, sentenceModel, msgDataFrame);
				
				Dataset<WordsPerSentenceDTO[]> wordsPerSentenceDataset = 
						sparkOpenNLP.extractWordsFromSentence(sparkSession, tokenizerModel, sentencesDataset);
				
				Dataset<WordsPerSentenceDTO[]> stemPerSentenceDataset = 
						sparkOpenNLP.stemWords(sparkSession, posModel, wordsPerSentenceDataset);
				

				List<WordsPerSentenceDTO[]> list = stemPerSentenceDataset.collectAsList();

				if (list.size() > 0) {
				
					WordsPerSentenceDTO[] fileNameAndWordsDTO = list.get(0);
					logger.info("\n\n=============================================");
					
					logger.info("FileName {} - Number of sentences {} ",fileNameAndWordsDTO[0].getFileName(), list.size());
					
					for (WordsPerSentenceDTO[] fileNameAndSentencesDTOArray : list) {
					
						for(WordsPerSentenceDTO fileNameAndSentencesDTO:fileNameAndSentencesDTOArray) {
							
							logger.info("Sentence: {} ",fileNameAndSentencesDTO.getSentence());
							for(String stem : fileNameAndSentencesDTO.getWords()) {
								logger.info(stem);
							}
						}
					}
					
					// sentencesPerFileName.put(row, value)
					logger.info("=============================================");
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
