package lab.spark.config;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lab.spark.nlp.util.NlpUtil;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

@Service
public class OpenNLPConfigService {

	private Logger logger = LoggerFactory.getLogger(OpenNLPConfigService.class);
	
	private SentenceModel sentenceModel;
	private TokenizerModel tokenizerModel;
	
	private POSModel posModel;
	
	private static Set<String> ENGLISH_STOP_WORDS = new HashSet<>();
	private NlpUtil nlpUtil = NlpUtil.getInstance();
	
	@PostConstruct
	public void initialize() throws IOException {
		
		if(!SystemUtils.IS_OS_WINDOWS) {
			sentenceModel = new SentenceModel(new File("/opt/spark-data/opennlp/models/en-sent.bin"));
			tokenizerModel = new TokenizerModel(new File("/opt/spark-data/opennlp/models/en-token.bin"));
			posModel = new POSModel(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
			ENGLISH_STOP_WORDS  = nlpUtil.getStopWordsSet();
			logger.info("Total Number of Stop Words {}",ENGLISH_STOP_WORDS.size());
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
	
	public POSModel getPOSModel() {
		return this.posModel;
	}
	
	public Set<String> getEnglishStopWord(){
		return ENGLISH_STOP_WORDS;
	}
	
}
