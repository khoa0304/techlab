package lab.spark.kafka.consumer.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.WordsPerSentenceDTO;

public class WordPerSentenceExtractionFunction implements Serializable,FlatMapFunction<Iterator<WordsPerSentenceDTO[]>, WordsPerSentenceDTO> {

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(WordPerSentenceExtractionFunction.class);
	
	@Override
	public Iterator<WordsPerSentenceDTO> call(Iterator<WordsPerSentenceDTO[]> t)
			throws Exception {
		List<WordsPerSentenceDTO> list = new ArrayList<WordsPerSentenceDTO>();
		while (t.hasNext()) {
			WordsPerSentenceDTO[] wordsPerSentenceDTOs = t.next();
			logger.debug("Number of wordsPerSentenceDTOs ", wordsPerSentenceDTOs.length);
			list.addAll(Arrays.asList(wordsPerSentenceDTOs));
		}
		return list.iterator();
	}
}
