package lab.ui.feeds.model;

public class FeedsAskModel {

	private String feedContent;
	private String links;
	private String category;
	private String latitude;
	private String longitude;

	public FeedsAskModel(String feedContent, String links, String category, String latitude, String longitude) {

		this.feedContent = feedContent;
		this.links = links;
		this.category = category;
		this.latitude = latitude;
		this.longitude = longitude;
	
	}

	public String getFeedContent() {
		return feedContent;
	}

	public void setFeedContent(String feedContent) {
		this.feedContent = feedContent;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
