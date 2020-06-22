package lab.spark.dto;

import java.io.Serializable;

public class FileAndContent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String file_name;
	private String content;
	
	public String getFile_name() {
		return file_name;
	}
	public String getContent() {
		return content;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
