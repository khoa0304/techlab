package lab.spark.kafka.consumer;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
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

public class FileUploadConsumerTestTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(FileUploadConsumerTestTask.class);
	
	
	public void processFileUpload(
			SparkConf sparkConfig, 
			Map<String, Object> configMap,
			String topicName)
			throws InterruptedException {

//		final StructType schema = DataTypes.createStructType(
//				new StructField[] { 
//						DataTypes.createStructField("FileName", DataTypes.StringType, true),
//						DataTypes.createStructField("FileContent", DataTypes.StringType, true) });

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConfig, Durations.seconds(15));
		//jssc.checkpoint("./checkpoint/");

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
		
		JavaDStream<SentencesDTO> sentencesDStream = fileUploadContentDTODStream.map(new Function<FileUploadContentDTO, SentencesDTO>() {

			private static final long serialVersionUID = 1L;
            private SentenceModel sentenceModel= null;
			
			@Override
			public SentencesDTO call(FileUploadContentDTO fileUploadContentDTO) throws Exception {
			
				if(sentenceModel == null) {
					sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));	
					logger.info("Initialize Sentence Model");
				}
				
				SentenceDetectTask sentenceDetectTask = new SentenceDetectTask();
				SentencesDTO sentencesDTO =
						sentenceDetectTask.extractSentencesFromContent(sentenceModel, fileUploadContentDTO);
			
				return sentencesDTO;
			}
		
		});
				
		
		
		JavaDStream<WordsPerSentenceDTO[]> wordsDStream = sentencesDStream.map(new Function<SentencesDTO,WordsPerSentenceDTO[]>() {
		
			private static final long serialVersionUID = 1L;
			private TokenizerModel tokenizerModel = null;
			
			@Override
			public WordsPerSentenceDTO[] call(SentencesDTO sentencesDTO) throws Exception {
				
				if(tokenizerModel == null) {
					tokenizerModel = new TokenizerModel(new File("/opt/spark-data/opennlp/models/en-token.bin"));
					logger.info("Initialize TokenizerModel");
				}
				
				TokenizeSentenceTask tokenizeSentenceTask = new TokenizeSentenceTask();
				
				Map<String,String[]> wordsGroupBySentence =
						tokenizeSentenceTask.tokenizeSentence(tokenizerModel, sentencesDTO);
				
				WordsPerSentenceDTO[] wordsPerSentenceDTOs = new WordsPerSentenceDTO[wordsGroupBySentence.size()];
				
				int index = 0;
				for(Map.Entry<String,String[]> entry : wordsGroupBySentence.entrySet()) {
				
					WordsPerSentenceDTO wordsPerSentenceDTO = 
							new WordsPerSentenceDTO(sentencesDTO.getFileName(), entry.getKey(),entry.getValue());
					
					wordsPerSentenceDTOs[index++] = wordsPerSentenceDTO;
				}
			
				return wordsPerSentenceDTOs;
			}
	
		});
		
		final Set<String> PUNCTUATION_SET = NlpUtil.getPunctuationSet();
		
		JavaDStream<WordsPerSentenceDTO[]> stemsDStream = wordsDStream.map(new Function<WordsPerSentenceDTO[], WordsPerSentenceDTO[]>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private POSModel posModel = null;
			
			@Override
			public WordsPerSentenceDTO[] call(WordsPerSentenceDTO[] wordsGroupBySentenceList) throws Exception {
				
				if(posModel == null) {
					posModel = new POSModel(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
					logger.info("Initialize PostModel");
				}
				 
				for(WordsPerSentenceDTO entry : wordsGroupBySentenceList) {
					
					WordStemTask wordStemTask = new WordStemTask();
					String[] words= entry.getWords();
					String[] stems = wordStemTask.lemmatatizer(posModel,words);
					int index = 0;
					for(String stem : stems) {
					
						if(stem.equalsIgnoreCase("O")) {
							String originalWord = words[index];
							logger.debug("Replace Stem 0 with {} ", PUNCTUATION_SET.contains(originalWord) ? "":originalWord);
							stems[index] = words[index];
						}
						index++;
					}
					logger.info("Total Words {} - Total Stems {} ", words.length,stems.length);
					entry.setWords(Arrays.copyOf(stems, stems.length));
				}
				
				return wordsGroupBySentenceList;
			}
		});
		
		
		stemsDStream.foreachRDD(new VoidFunction<JavaRDD<WordsPerSentenceDTO[]>>() {
		
			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<WordsPerSentenceDTO[]> javaRDD) throws Exception {
				
				List<WordsPerSentenceDTO[]> list = javaRDD.collect();
				if (list.size() > 0) {
				
					WordsPerSentenceDTO[] fileNameAndWordsDTO = list.get(0);
					logger.info("\n\n=============================================");
					
					logger.info("FileName {} - Number of RDD {} ",fileNameAndWordsDTO[0].getFileName(), list.size());
					
					for (WordsPerSentenceDTO[] fileNameAndSentencesDTOArray : list) {
					
						for(WordsPerSentenceDTO fileNameAndSentencesDTO:fileNameAndSentencesDTOArray) {
							StringBuilder sb = new StringBuilder();
							for(String stem : fileNameAndSentencesDTO.getWords()) {
								sb.append(stem).append(" ");
							}
							logger.info("Sentence: {} ",fileNameAndSentencesDTO.getSentence());
							logger.info("Stem/Word {})", sb.toString());
						    sb = null;
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



