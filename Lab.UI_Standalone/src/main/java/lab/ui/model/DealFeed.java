package lab.ui.model;


public class DealFeed {
    private String text;
    private String url;

    public DealFeed(String text, String url) {
		this.text = text;
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
}
