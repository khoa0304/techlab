package lab.kafka.dto;

public class FileContentDTO {

	private String fileName;
	private String fileContent;
	
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
}
