package lab.ui.model;

public class Tag {

	public int id;
	public String tagName;

	// getter and setter methods

	public Tag(int id, String tagName) {
		this.id = id;
		this.tagName = tagName;
	}

	public int getId() {
		return id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
