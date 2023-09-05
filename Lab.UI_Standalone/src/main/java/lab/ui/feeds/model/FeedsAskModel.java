package lab.ui.feeds.model;

public class FeedsAskModel {
	
	private String text;
	private String email;
	
	private String latitude;
	private String longitude;
	
	public FeedsAskModel(String text, String email, String latitude, String longitude) {
		this.text = text;
		this.email = email;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	@Override
	public String toString() {
		return "FeedsAskModel [text=" + text + ", email=" + email + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}
	
	
}
