package lab.spark.config;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Service;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

@Service
public class OpenNLPConfigService {

	private SentenceModel sentenceModel;
	private TokenizerModel tokenizerModel;
	
	private  POSModel posModel;
	private  DictionaryLemmatizer lemmatizer;
	 
	
	@PostConstruct
	public void initialize() throws IOException {
		
		if(!SystemUtils.IS_OS_WINDOWS) {
			sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("/opt/spark-data/opennlp/models/en-token.bin"));
			//posModel = new POSModel(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
			//lemmatizer = new DictionaryLemmatizer(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
		}
		else {
			sentenceModel = new SentenceModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-token.bin"));
			//posModel = new POSModel(new File("C:\\git\\techlab\\Lab.Spark\\opennlp\\models\\en-pos-maxent.bin"));
			//lemmatizer = new DictionaryLemmatizer(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
		}
	}
	
	
	public SentenceModel getSentenceModel() {
		return this.sentenceModel;
	}
	
	public TokenizerModel getTokenizerModel() {
		return this.tokenizerModel;
	}
	
}
