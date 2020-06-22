package lab.spark.model;

import java.io.Serializable;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class OpenNLPSerializedWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String[] detectSentence(SentenceModel sentenceModel,String content) {
		
		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(sentenceModel);
		return sentenceDetectorME.sentDetect(content);
	}
}
