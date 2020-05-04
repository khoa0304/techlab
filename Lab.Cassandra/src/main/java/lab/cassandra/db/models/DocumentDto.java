package lab.cassandra.db.models;

public class DocumentDto {

	private String abosolutePath;
	private String fileName;
	
	public String getAbosolutePath() {
		return abosolutePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setAbosolutePath(String abosolutePath) {
		this.abosolutePath = abosolutePath;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
