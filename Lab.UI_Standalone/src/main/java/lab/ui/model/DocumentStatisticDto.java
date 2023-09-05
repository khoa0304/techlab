package lab.ui.model;

public class DocumentStatisticDto {

	private String fileName;
	private int totalSentences;
	private int totalWords;

	public DocumentStatisticDto() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalSentences() {
		return totalSentences;
	}

	public void setTotalSentences(int totalSentences) {
		this.totalSentences = totalSentences;
	}

	public int getTotalWords() {
		return totalWords;
	}

	public void setTotalWords(int totalWords) {
		this.totalWords = totalWords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + totalSentences;
		result = prime * result + totalWords;
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
		DocumentStatisticDto other = (DocumentStatisticDto) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (totalSentences != other.totalSentences)
			return false;
		if (totalWords != other.totalWords)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DocumentStatisticDto [fileName=" + fileName + ", totalSentences=" + totalSentences + ", totalWords="
				+ totalWords + "]";
	}
}
