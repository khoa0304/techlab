package lab.datacapture.nlp.service;

import java.io.IOException;
import java.net.URL;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
public class NLPProcessor {

	private SentenceModel sentenceModel;
	private TokenizerModel tokenizerModel;
	
	private final URL OPENNLP_SENTENCE_BIN = getClass().getResource("/opennlp/models/en-sent.bin");
	private final URL OPENNLP_TOKEN_BIN = getClass().getResource("/opennlp/models/en-token.bin");
	
	public NLPProcessor() throws IOException {
		sentenceModel = new SentenceModel(OPENNLP_SENTENCE_BIN);
		tokenizerModel = new TokenizerModel(OPENNLP_TOKEN_BIN);
	}
	
	public String[] extractSentences(String content) {

		SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);  
		//Detecting the sentence
		String[] sentences = detector.sentDetect(content); 
		return sentences;
	}
	
	public String[] extractTokens(String sentence) {

		TokenizerME tokenizer = new TokenizerME (tokenizerModel);  
		//Detecting the sentence
		String[] tokens = tokenizer.tokenize(sentence); 
		return tokens;
	}
}
