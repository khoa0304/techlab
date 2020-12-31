package lab.spark.kafka.consumer.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.WordsPerSentenceDTO;

public class WordExtractionFunction implements Serializable,FlatMapFunction<Iterator<WordsPerSentenceDTO>, String>{

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(WordExtractionFunction.class);
	
	@Override
	public Iterator<String> call(Iterator<WordsPerSentenceDTO> t) throws Exception {
		
		List<String> list = new ArrayList<String>();
		while(t.hasNext()) {
			WordsPerSentenceDTO wordsPerSentenceDTO = t.next();
			Set<String> words = wordsPerSentenceDTO.getWords();
			list.addAll(words);
		}
			
		return list.iterator();
	}
	
}
