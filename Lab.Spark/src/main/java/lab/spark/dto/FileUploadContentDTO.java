package lab.spark.dto;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class FileUploadContentDTO implements KryoSerializable {
	
	private String fileName;
	private String fileContent;
	private String[] sentences = new String[0];
	
	public FileUploadContentDTO() {};
	
	public String getFileName() {
		return fileName;
	}
	public String getFileContent() {
		return fileContent;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String[] getSentences() {
		return sentences;
	}

	public void setSentences(String[] sentences) {
		this.sentences = sentences;
	}

	@Override
	public String toString() {
		return "FileUploadContent [fileName=" + fileName + ", fileContent=" + fileContent + "]";
	}

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(fileName);
		output.writeString(fileContent);
		
		kryo.writeClassAndObject(output, sentences);	
		
	}

	@Override
	public void read(Kryo kryo, Input input) {
		fileName = input.readString();
	    fileContent = input.readString();
	    sentences = (String[]) kryo.readClassAndObject(input);
		
	}
}
