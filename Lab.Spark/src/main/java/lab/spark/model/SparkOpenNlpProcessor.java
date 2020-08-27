package lab.spark.model;

import java.io.Serializable;
import java.util.Map;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.FileUploadContentDTO;
import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

public class SparkOpenNlpProcessor implements Serializable {

	private static final long serialVersionUID = -8488774602800941495L;
	
	private Logger logger = LoggerFactory.getLogger(SparkOpenNlpProcessor.class);
	
	private static OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
	
	public Dataset<String[]> extractStringContentSentence(
			SparkSession sparkSession,
			SentenceModel sentenceModel,
			Dataset<FileUploadContentDTO> dataset){

		

//		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
//				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<String[]> sentencesDataset = dataset.map(
				
				(MapFunction<FileUploadContentDTO, String[]>) mapFunc ->
				
				{
					//OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
					
					return openNLPSerializedWrapper.detectSentence(sentenceModel,mapFunc.getFileContent());
				}
				, Encoders.kryo(String[].class)
				);
		
		
		return sentencesDataset;
	}
	
	public Dataset<WordsPerSentenceDTO[]> extractWordsFromSentence(
			SparkSession sparkSession,
			TokenizerModel tokenizerModel,
			Dataset<SentencesDTO> dataset){

		Dataset<WordsPerSentenceDTO[]> sentencesDataset = dataset.map(
				
				(MapFunction<SentencesDTO,WordsPerSentenceDTO[]>) mapFunc ->
				
				{
					//OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
					Map<String,String[]> wordsGroupedBySentence = openNLPSerializedWrapper.tokenizeSentence(tokenizerModel,mapFunc.getSentences());
					
					WordsPerSentenceDTO[] fileNameAndWordsDTOs = new WordsPerSentenceDTO[wordsGroupedBySentence.size()];
					int index = 0;
					for(Map.Entry<String,String[]> entry : wordsGroupedBySentence.entrySet()) {
						
						WordsPerSentenceDTO fileNameAndSentencesDto = 
								new WordsPerSentenceDTO(mapFunc.getFileName(), entry.getKey(),entry.getValue());
						fileNameAndWordsDTOs[index++]= fileNameAndSentencesDto;
						
					}
				
					return fileNameAndWordsDTOs;
				}
				, Encoders.kryo(WordsPerSentenceDTO[].class)
				);
		
		return sentencesDataset;
	}
	
	
	public Dataset<WordsPerSentenceDTO[]> stemWords(
			SparkSession sparkSession,
			POSModel posModel,
			Dataset<WordsPerSentenceDTO[]> wordsDataset){

		Dataset<WordsPerSentenceDTO[]> sentencesDataset = wordsDataset.map(
				
				(MapFunction<WordsPerSentenceDTO[],WordsPerSentenceDTO[]>) wordsDatasetArray ->
				
				{
					//OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
					
					for(WordsPerSentenceDTO wordsPerSentenceDTO : wordsDatasetArray) {
					
						String[] words = wordsPerSentenceDTO.getWords();
						String[] stems = openNLPSerializedWrapper.lemmatatizer(posModel,words );						
						wordsPerSentenceDTO.setWords(stems);
					}
					return wordsDatasetArray;
				}
				, Encoders.kryo(WordsPerSentenceDTO[].class)
				);
		
		return sentencesDataset;
	}

	
	public Dataset<SentencesDTO> processContentUsingOpenkNLP(
			SparkSession sparkSession,
			SentenceModel sentenceModel,
			Dataset<Row> dataset){

//		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();
//
//		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
//				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<SentencesDTO> sentencesDataset = dataset.map(
				
				(MapFunction<Row, SentencesDTO>) mapFunc ->
				
				{
					//OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
					String[] sentences = openNLPSerializedWrapper.detectSentence(sentenceModel,mapFunc.getString(1));
					SentencesDTO fileNameAndSentencesDto = new SentencesDTO(mapFunc.getString(0), sentences);
					return fileNameAndSentencesDto;
				}
				, Encoders.kryo(SentencesDTO.class)
				);
		
		return sentencesDataset;
	}

}
