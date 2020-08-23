package lab.spark.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import lab.spark.nlp.util.NlpUtil;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPSerializedWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Set<String> ENGLISH_STOP_WORDS = new HashSet<>();
	
	public OpenNLPSerializedWrapper() {
//		/NlpUtil nlpUtil = new NlpUtil();
		//ENGLISH_STOP_WORDS  = nlpUtil.getStopWordsSet();
	}
	
	public String[] detectSentence(SentenceModel sentenceModel,String content) {
		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(sentenceModel);
		return sentenceDetectorME.sentDetect(content);
	}
	
	public List<String> tokenizeSentence(TokenizerModel tokenizerModel,String[] sentences) {
		
		TokenizerME tokenizer = new TokenizerME (tokenizerModel);  
		
		final List<String> words = new ArrayList<>();
		for(String sentence:sentences) {
		
			if(StringUtils.isEmpty(sentence)) continue;
			String[] tokens = tokenizer.tokenize(sentence);
			words.addAll(Arrays.asList(tokens));
		}
		 
		return removeStopWords(words);
	}
	
	
	public void lemmetatizer(POSModel posModel, String[] words) {
	
		  // initializing the parts-of-speech tagger with model
        POSTaggerME posTagger = new POSTaggerME(posModel);
	}
	
	
	private List<String> removeStopWords(List<String> words ) {
		
		//words.removeAll(ENGLISH_STOP_WORDS);
		return words;
	}
}
