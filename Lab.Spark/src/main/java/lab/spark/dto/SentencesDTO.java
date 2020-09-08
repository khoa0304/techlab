package lab.spark.dto;

import java.util.Arrays;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SentencesDTO implements KryoSerializable {
	
	private String fileName;
	private String[] sentences;
	
	public SentencesDTO(String fileName, String[] sentences) {
		
		this.fileName = fileName;
		this.sentences = sentences;
	}
	
	public String getFileName() {
		return fileName;
	}
	public String[] getSentences() {
		return sentences;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setSentences(String[] sentences) {
		this.sentences = sentences;
	}
	
	
	@Override
	public String toString() {
		return "FileNameAndSentencesDto [fileName=" + fileName + ", sentences=" + Arrays.toString(sentences) + "]";
	}

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(fileName);
		kryo.writeClassAndObject(output,sentences);
		
	}

	@Override
	public void read(Kryo kryo, Input input) {
		fileName = input.readString();
		sentences = (String[]) kryo.readClassAndObject(input);
	}
	
	
}
