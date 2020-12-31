package lab.spark.task;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.SentencesDTO;
import lab.spark.model.OpenNLPSerializedWrapper;
import lab.spark.nlp.util.NlpUtil;
import opennlp.tools.tokenize.TokenizerModel;

public class TokenizeSentenceTask implements Serializable {

	private Logger logger = LoggerFactory.getLogger(TokenizeSentenceTask.class);

	private static final long serialVersionUID = 1L;

	final OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
	final Set<String> englishStopWords = NlpUtil.getInstance().getStopWordsSet();
	
	public Map<String,String[]> tokenizeSentence(
			TokenizerModel tokenizerModel,
			SentencesDTO sentencesDTO) {

		Map<String,String[]> wordsGroupedBySentence = 
				openNLPSerializedWrapper.tokenizeSentence(tokenizerModel, englishStopWords, sentencesDTO.getSentences());
	
//		IntSummaryStatistics totalWords = wordsGroupedBySentence.values().stream().map(new Function<String[], Integer>() {
//
//			@Override
//			public Integer apply(String[] t) {
//				return t.length;
//			}
//		}).collect(Collectors.summarizingInt(Integer:: intValue));
//		
//		logger.info("Total Sentences {} - Total Words {}", wordsGroupedBySentence.size(),totalWords.getSum());
		return wordsGroupedBySentence;
	}

}
