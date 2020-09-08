package lab.solr.rest.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author kntran
 *
 */
public class SentenceAndWordStem {

	private String fileName;
	private String sentence;
	private List<String> wordStem = new ArrayList<>();
	
	
	public List<String> getWordStem() {
		return wordStem;
	}
	public String getFileName() {
		return fileName;
	}
	public String getSentence() {
		return sentence;
	}
	public void setWordStem(List<String> wordStem) {
		this.wordStem = wordStem;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	
}
