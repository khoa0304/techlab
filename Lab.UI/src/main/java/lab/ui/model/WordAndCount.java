package lab.ui.model;

public class WordAndCount {

	private String[] sLabel;
	private long[] sData;
	
	public WordAndCount(String[] sLabel,long[] sData) {
		this.sLabel = sLabel;
		this.sData = sData;
	}

	public String[] getsLabel() {
		return sLabel;
	}

	public long[] getsData() {
		return sData;
	}

	public void setsLabel(String[] sLabel) {
		this.sLabel = sLabel;
	}

	public void setsData(long[] sData) {
		this.sData = sData;
	}
	
	
	
}
