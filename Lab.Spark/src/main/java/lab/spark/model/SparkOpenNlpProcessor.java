package lab.spark.model;

import java.io.Serializable;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import lab.spark.dto.FileUploadContent;
import opennlp.tools.sentdetect.SentenceModel;


public class SparkOpenNlpProcessor implements Serializable {

	private static final long serialVersionUID = -8488774602800941495L;

	public Dataset<String[]> processContentUsingOpenkNLP(
			SparkSession sparkSession,
			SentenceModel sentenceModel,
			Dataset<Row> dataset){

		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();

		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<String[]> sentencesDataset = dataset.map(
				
				(MapFunction<Row, String[]>) mapFunc ->
				
				{
					return broadcastSentenceDetector.value().detectSentence(sentenceModel,mapFunc.getString(1));
				}
				, Encoders.kryo(String[].class)
				);
		
		return sentencesDataset;
	}
	
	public Dataset<String[]> extractStringContentSentence(
			SparkSession sparkSession,
			SentenceModel sentenceModel,
			Dataset<FileUploadContent> dataset){

		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();

		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<String[]> sentencesDataset = dataset.map(
				
				(MapFunction<FileUploadContent, String[]>) mapFunc ->
				
				{
					return broadcastSentenceDetector.value().detectSentence(sentenceModel,mapFunc.getFileContent());
				}
				, Encoders.kryo(String[].class)
				);
		
		
		return sentencesDataset;
	}

}
