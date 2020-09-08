package lab.spark.dto;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class WordsPerSentenceDTO implements KryoSerializable {
	
	private String fileName;
	private String sentence;
	private String[] words;
	
	public WordsPerSentenceDTO(String fileName,String sentence,String[] words) {
		this.fileName = fileName;
		this.sentence = sentence;
		this.words = words;
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
	public String[] getWords() {
		return words;
	}
	public void setWords(String[] words) {
		this.words = words;
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
		words = (String[]) kryo.readClassAndObject(input);
	}
	
}
