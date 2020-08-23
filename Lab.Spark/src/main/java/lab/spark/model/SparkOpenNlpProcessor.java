package lab.spark.model;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import lab.spark.dto.FileNameAndSentencesDTO;
import lab.spark.dto.FileUploadContentDTO;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

public class SparkOpenNlpProcessor implements Serializable {

	private static final long serialVersionUID = -8488774602800941495L;

	public Dataset<FileNameAndSentencesDTO> processContentUsingOpenkNLP(
			SparkSession sparkSession,
			SentenceModel sentenceModel,
			Dataset<Row> dataset){

		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();

		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<FileNameAndSentencesDTO> sentencesDataset = dataset.map(
				
				(MapFunction<Row, FileNameAndSentencesDTO>) mapFunc ->
				
				{
					//OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();
					String[] sentences = broadcastSentenceDetector.value().detectSentence(sentenceModel,mapFunc.getString(1));
					FileNameAndSentencesDTO fileNameAndSentencesDto = new FileNameAndSentencesDTO(mapFunc.getString(0), sentences);
					return fileNameAndSentencesDto;
				}
				, Encoders.kryo(FileNameAndSentencesDTO.class)
				);
		
		return sentencesDataset;
	}
	
	
	public Dataset<FileNameAndSentencesDTO> extractWordFromString(
			SparkSession sparkSession,
			TokenizerModel tokenizerModel,
			Dataset<FileNameAndSentencesDTO> dataset){

		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();

		Broadcast<OpenNLPSerializedWrapper> broadcastWordDetector = sparkSession.sparkContext()
				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));
		

		Dataset<FileNameAndSentencesDTO> sentencesDataset = dataset.map(
				
				(MapFunction<FileNameAndSentencesDTO,FileNameAndSentencesDTO>) mapFunc ->
				
				{
					List<String> words = broadcastWordDetector.value().tokenizeSentence(tokenizerModel,mapFunc.getSentences());
					FileNameAndSentencesDTO fileNameAndSentencesDto = 
							new FileNameAndSentencesDTO(mapFunc.getFileName(), words.toArray(new String[words.size()]));
					return fileNameAndSentencesDto;
				}
				, Encoders.kryo(FileNameAndSentencesDTO.class)
				);
		
		return sentencesDataset;
	}
	
	
	
	public Dataset<String[]> extractStringContentSentence(
			SparkSession sparkSession,
			SentenceModel sentenceModel,
			Dataset<FileUploadContentDTO> dataset){

		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();

		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<String[]> sentencesDataset = dataset.map(
				
				(MapFunction<FileUploadContentDTO, String[]>) mapFunc ->
				
				{
					return broadcastSentenceDetector.value().detectSentence(sentenceModel,mapFunc.getFileContent());
				}
				, Encoders.kryo(String[].class)
				);
		
		
		return sentencesDataset;
	}

}
