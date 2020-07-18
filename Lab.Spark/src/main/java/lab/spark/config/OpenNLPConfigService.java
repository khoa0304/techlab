package lab.spark.config;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Service;

import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

@Service
public class OpenNLPConfigService {

	private SentenceModel sentenceModel;
	private TokenizerModel tokenizerModel;
	
	
	@PostConstruct
	public void initialize() throws IOException {
		
		if(!SystemUtils.IS_OS_WINDOWS) {
			sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("/opt/spark-data/opennlp/models/en-token.bin"));
		}
		else {
			sentenceModel = new SentenceModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-token.bin"));
		}
	}
	
	
	public SentenceModel getSentenceModel() {
		return this.sentenceModel;
	}
	
	public TokenizerModel getTokenizerModel() {
		return this.tokenizerModel;
	}
	
}
