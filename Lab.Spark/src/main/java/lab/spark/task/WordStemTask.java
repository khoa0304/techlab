package lab.spark.task;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.spark.model.OpenNLPSerializedWrapper;
import lab.spark.nlp.util.NlpUtil;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;

public class WordStemTask implements Serializable {

	private Logger logger = LoggerFactory.getLogger(WordStemTask.class);

	private static final long serialVersionUID = 1L;

	final OpenNLPSerializedWrapper openNLPSerializedWrapper = OpenNLPSerializedWrapper.getInstance();
	final DictionaryLemmatizer dictionaryLemmatizer = NlpUtil.getInstance().getDictionaryLemmatizer();
	
	public String[] lemmatatizer(
			POSModel posModel,
			String[] words) {

		String[] wordStems = 
				openNLPSerializedWrapper.lemmatatizer(dictionaryLemmatizer, posModel, words);
	
		logger.info("Total Stem Words {}",wordStems.length);
		return wordStems;
	}

}
