package lab.spark.dto;

import java.io.Serializable;
import java.util.Arrays;

public class FileNameAndSentencesDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String fileName;
	private String[] sentences;
	
	public FileNameAndSentencesDTO(String fileName, String[] sentences) {
		
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
	
	
}
