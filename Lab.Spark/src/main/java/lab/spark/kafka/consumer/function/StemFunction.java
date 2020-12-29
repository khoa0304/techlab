package lab.spark.kafka.consumer.function;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.nlp.util.NlpUtil;
import lab.spark.task.WordStemTask;
import opennlp.tools.postag.POSModel;

public class StemFunction implements Serializable,Function<WordsPerSentenceDTO[], WordsPerSentenceDTO[]> {

	private Logger logger = LoggerFactory.getLogger(StemFunction.class);
	
	private static final long serialVersionUID = 1L;
	private POSModel posModel = null;
	
	private final Set<String> PUNCTUATION_SET = NlpUtil.getPunctuationSet();
	
	private static final String ALPHA_NUMERIC_PATTERN = "^[a-zA-Z0-9]*$";
	@Override
	public WordsPerSentenceDTO[] call(WordsPerSentenceDTO[] wordsGroupBySentenceList) throws Exception {

		if (posModel == null) {
			posModel = new POSModel(new File("/opt/spark-data/opennlp/models/en-pos-maxent.bin"));
			logger.info("Initialize PostModel");
		}

		for (WordsPerSentenceDTO entry : wordsGroupBySentenceList) {

			WordStemTask wordStemTask = new WordStemTask();
			List<String> words = entry.getWords();
			String[] stems = wordStemTask.lemmatatizer(posModel,
					words.toArray(new String[words.size()]));
			int index = 0;
			for (String stem : stems) {

				if (stem.equalsIgnoreCase("O")) {
					String originalWord = words.get(index);
					logger.debug("Replace Stem 0 with {} ",
							PUNCTUATION_SET.contains(originalWord) ? "" : originalWord);
					stems[index] = words.get(index);
				}
				
				if( ! stem.matches(ALPHA_NUMERIC_PATTERN)) {
					stems[index] = "" ;
				}
			
				index++;
			}
			logger.info("Total Words {} - Total Stems {} ", words.size(), stems.length);
			entry.setWords(Arrays.asList(stems));
		}

		return wordsGroupBySentenceList;
	}

}
