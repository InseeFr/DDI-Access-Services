package fr.insee.rmes.model;

public class DDIItem extends ResponseItem {

	private String type;

	public DDIItem() {
		super();
	}

	public DDIItem(String id, String label, String parent, String type) {
		super(id, parent, label);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
