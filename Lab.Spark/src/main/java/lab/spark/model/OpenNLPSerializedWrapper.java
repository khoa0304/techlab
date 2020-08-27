package lab.spark.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import lab.spark.nlp.util.NlpUtil;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPSerializedWrapper implements Serializable {

	private Logger logger = LoggerFactory.getLogger(OpenNLPSerializedWrapper.class);
	
	private static final long serialVersionUID = 1L;
	private static Set<String> ENGLISH_STOP_WORDS = new HashSet<>();
	
	private DictionaryLemmatizer dictionaryLemmatizer;
	
	private static OpenNLPSerializedWrapper INSTANCE = new OpenNLPSerializedWrapper();
	
	public static OpenNLPSerializedWrapper getInstance() {
		return INSTANCE;
	}
	
	private OpenNLPSerializedWrapper() {
		
		NlpUtil nlpUtil = NlpUtil.getInstance();
		ENGLISH_STOP_WORDS  = nlpUtil.getStopWordsSet();
		logger.info("Total Number of Stop Words {}",ENGLISH_STOP_WORDS.size());
		
		dictionaryLemmatizer = nlpUtil.getDictionaryLemmatizer();
		
	}
	
	public String[] detectSentence(SentenceModel sentenceModel,String content) {
		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(sentenceModel);
		return sentenceDetectorME.sentDetect(content);
	}
	
	public Map<String,String[]> tokenizeSentence(TokenizerModel tokenizerModel,String[] sentences) {
		
		TokenizerME tokenizer = new TokenizerME (tokenizerModel);  
		
		Map<String,String[]> wordsGroupedBySentence = new HashMap<>(sentences.length);
		
		for(String sentence:sentences) {
			
			if(StringUtils.isEmpty(sentence)) continue;
			
			String[] tokens = tokenizer.tokenize(sentence);
			List<String> words = removeStopWords(tokens);
			String[] wordArray = words.toArray(new String[0]);
			logger.info("Total words {}", wordArray.length);
			wordsGroupedBySentence.put(sentence,wordArray );
		}
	
		return wordsGroupedBySentence ;
	}
	
	
	public String[] lemmatatizer(POSModel posModel, String[] words) {
	    POSTaggerME posTagger = new POSTaggerME(posModel);
        String[] tags = posTagger.tag(words);
        String[] lemmas = dictionaryLemmatizer.lemmatize(words, tags);
		logger.info("====== > Stemming {} words - Stems size: {}" , words.length,lemmas.length);
        return lemmas;
    }
	
	
	private List<String> removeStopWords(String[] words ) {
		logger.debug("Before removing stopword {} ",words.length);
		
		List<String> wordList =
				Arrays.asList(words).stream().parallel().map(w ->w.toLowerCase()).collect(Collectors.toList());
		wordList.removeAll(ENGLISH_STOP_WORDS);
		
		logger.debug("After removing stopword {} ",wordList.size());
		
		return wordList;
	}
}
