package lab.spark.dto;

import java.io.Serializable;

public class FileUploadContentDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private String fileContent;
	
	public FileUploadContentDTO() {};
	
//	public FileUploadContent(String fileName, String fileContent) {
//		this.fileName = fileName;
//		this.fileContent = fileContent;
//	};
	
	
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
	@Override
	public String toString() {
		return "FileUploadContent [fileName=" + fileName + ", fileContent=" + fileContent + "]";
	}
}
