package lab.ui.model;

import java.io.File;

public class DocumentDto {

	private String absoluteDirectoryPath;
	private String fileNamePlusExtesion;
	
	
	public String getAbsoluteDirectoryPath() {
		return absoluteDirectoryPath;
	}


	public String getFileNamePlusExtesion() {
		return fileNamePlusExtesion;
	}


	public void setAbsoluteDirectoryPath(String absoluteDirectoryPath) {
		this.absoluteDirectoryPath = absoluteDirectoryPath;
	}


	public void setFileNamePlusExtesion(String fileNamePlusExtesion) {
		this.fileNamePlusExtesion = fileNamePlusExtesion;
	}


	public String getAbsoluteFilePath() {
		return this.absoluteDirectoryPath + File.separatorChar + this.fileNamePlusExtesion;
	}


	@Override
	public String toString() {
		return "DocumentDto [absoluteDirectoryPath=" + absoluteDirectoryPath + ", fileNamePlusExtesion="
				+ fileNamePlusExtesion + "]";
	}
}
