package lab.ui.model;

public class MyDataDTO {
    private String latitude;
    private String longitude;

    public MyDataDTO(String latitude, String longitude) {
		this.latitude =latitude;
		this.longitude = longitude;
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
		return "MyDataDTO [latitude=" + latitude + ", longitude=" + longitude + "]";
	}

	
	
}
