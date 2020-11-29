package lab.spark.nlp.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;

public class NlpUtil {


	private static final Logger logger = LoggerFactory.getLogger(NlpUtil.class);
	
	private static final NlpUtil INSTANCE = new NlpUtil();
	
	private static final Set<String> STOP_WORD_SET =  new HashSet<>();
	
	private static final String FILE_LOCATION_PREFIX = "/opt/spark-data/stopwords/english/stopwords_%d.txt";
	
	private static final String LEMMA_DICTIONARY= "/opt/spark-data/opennlp/models/en-lemmatizer.dict";
	
	private DictionaryLemmatizer dictionaryLemmatizer;
	
	private NlpUtil() {};
	
	public static final NlpUtil getInstance() {
		return INSTANCE;
	}
	
	public Set<String> getStopWordsSet(){

		if(!STOP_WORD_SET.isEmpty()) return STOP_WORD_SET;
		
		
		for(int i = 1 ; i <= 3; i ++) {
			
			String fileLocation = String.format(FILE_LOCATION_PREFIX, i);
			try {
				
				File file = ResourceUtils.getFile(fileLocation);
				List<String> lines = Files.readAllLines(file.toPath());
				Set<String>  set = lines.stream().map(new Function<String, String>() {

					@Override
					public String apply(String t) {

						if(t != null && t.trim().length() > 0) {
							return t.toLowerCase();
						}
						return "";
					}
				}).collect(Collectors.toSet());;

				STOP_WORD_SET.addAll(set);
				logger.info("Finished initializing STOP_WORDS {}", STOP_WORD_SET.size());
			}catch (IOException e) {
				logger.error("Error reading stop words ",e);
			}
		
		}
		
		
		return STOP_WORD_SET;
	}
	
	public DictionaryLemmatizer getDictionaryLemmatizer() {
		
		try {
			
			if(dictionaryLemmatizer != null)return dictionaryLemmatizer;
			
			dictionaryLemmatizer = 
					new DictionaryLemmatizer(new File(LEMMA_DICTIONARY));
			logger.info("Finish loading English Dictionary size {}", dictionaryLemmatizer.getDictMap().size());
			
		} catch (IOException e) {
			logger.error("Error loading dictionaryLemmatizer",e);
		}
		return dictionaryLemmatizer;
	}
	
	public static Set<String> getPunctuationSet(){
		Set<String> set =  new HashSet<String>();
		
		final String[] array = new String[] {
		   "!",".",",","#",".",
		   "(",")","~","*",":",
		   "[","]","{","}"
		};
		
		set.addAll(Arrays.asList(array));
		
		return set;
	}
}
