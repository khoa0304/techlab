package lab.ui.model;

public class LabelAndCount {

	private String[] sLabel;
	private long[] sData;
	
	public LabelAndCount(String[] sLabel,long[] sData) {
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
