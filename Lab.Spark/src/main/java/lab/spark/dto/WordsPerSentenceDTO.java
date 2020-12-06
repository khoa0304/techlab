package lab.spark.dto;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class WordsPerSentenceDTO implements KryoSerializable {
	
	private String fileName;
	private String sentence;
	private List<String> words;
	private int totalStems;
	private int sentenceLength;
	
	public WordsPerSentenceDTO(String fileName,String sentence,List<String> words) {
		this.fileName = fileName;
		this.sentence = sentence;
		this.words = words;
		this.totalStems = words.size();
		this.sentenceLength = sentence.length();
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public List<String> getWords() {
		return words;
	}
	public void setWords(List<String> words) {
		this.words = words;
	}
	

	public int getTotalStems() {
		return totalStems;
	}

	public void setTotalStems(int totalStems) {
		this.totalStems = totalStems;
	}

	public int getSentenceLength() {
		return sentenceLength;
	}

	public void setSentenceLength(int sentenceLength) {
		this.sentenceLength = sentenceLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordsPerSentenceDTO other = (WordsPerSentenceDTO) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileNameAndWordsDTO [fileName=" + fileName + ", sentence=" + sentence + "]";
	}

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(fileName);
		output.writeString(sentence);
		kryo.writeClassAndObject(output, words);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		fileName = input.readString();
		sentence = input.readString();
		words = (ArrayList<String>) kryo.readClassAndObject(input);
	}
	
}
