package lab.spark.kafka.consumer.function;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.dto.SentencesDTO;
import lab.spark.dto.WordsPerSentenceDTO;
import lab.spark.task.TokenizeSentenceTask;
import opennlp.tools.tokenize.TokenizerModel;

public class WordPerSentenceExtractionArrayFunction implements Serializable,Function<SentencesDTO, WordsPerSentenceDTO[]> {

	private Logger logger = LoggerFactory.getLogger(WordPerSentenceExtractionArrayFunction.class);
	
	private static final long serialVersionUID = 1L;
	private TokenizerModel tokenizerModel = null;

	@Override
	public WordsPerSentenceDTO[] call(SentencesDTO sentencesDTO) throws Exception {

		if (tokenizerModel == null) {
			tokenizerModel = new TokenizerModel(
					new File("/opt/spark-data/opennlp/models/en-token.bin"));
			logger.info("Initialize TokenizerModel");
		}

		TokenizeSentenceTask tokenizeSentenceTask = new TokenizeSentenceTask();

		Map<String, String[]> wordsGroupBySentence = tokenizeSentenceTask
				.tokenizeSentence(tokenizerModel, sentencesDTO);

		WordsPerSentenceDTO[] wordsPerSentenceDTOs = new WordsPerSentenceDTO[wordsGroupBySentence
				.size()];

		int index = 0;
		for (Map.Entry<String, String[]> entry : wordsGroupBySentence.entrySet()) {

			WordsPerSentenceDTO wordsPerSentenceDTO = new WordsPerSentenceDTO(
					sentencesDTO.getFileName(), entry.getKey(), new HashSet<>(Arrays.asList(entry.getValue())));

			wordsPerSentenceDTOs[index++] = wordsPerSentenceDTO;
		}

		return wordsPerSentenceDTOs;
	}

}
