package lab.spark.sample;

import java.io.IOException;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import lab.spark.model.FileAndContent;
import lab.spark.model.OpenNLPSerializedWrapper;
import opennlp.tools.sentdetect.SentenceDetectorME;

public class SparkOpenNLP {

	
	public SparkOpenNLP() {}

	
	public Dataset<String[]> processContentUsingOpenkNLP(SparkSession sparkSession, Dataset<FileAndContent> dataset) throws IOException {

		OpenNLPSerializedWrapper openNLPSerializedWrapper = new OpenNLPSerializedWrapper();

		Broadcast<OpenNLPSerializedWrapper> broadcastSentenceDetector = sparkSession.sparkContext()
				.broadcast(openNLPSerializedWrapper, scala.reflect.ClassTag$.MODULE$.apply(OpenNLPSerializedWrapper.class));

		Dataset<String[]> sentencesDataset = dataset.map(
				
				(MapFunction<FileAndContent, String[]>) mapFunc ->
				
				{
					return broadcastSentenceDetector.value().detectSentence(mapFunc.getContent());
				}
				, Encoders.kryo(String[].class)
				);
		
		
		return sentencesDataset;
	}

}
