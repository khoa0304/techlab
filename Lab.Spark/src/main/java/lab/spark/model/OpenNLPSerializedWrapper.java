package lab.spark.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.SystemUtils;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPSerializedWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SentenceModel sentenceModel;
	private TokenizerModel tokenizerModel;

	public OpenNLPSerializedWrapper() throws IOException {
		if(!SystemUtils.IS_OS_WINDOWS) {
			sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("/opt/spark-data/opennlp/models/en-token.bin"));
		}
		else {
			sentenceModel = new SentenceModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-token.bin"));
		}
	}
	
	public String[] detectSentence(String content) {
		
		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(sentenceModel);
		return sentenceDetectorME.sentDetect(content);
	}
}
