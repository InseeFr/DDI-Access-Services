package fr.insee.rmes.search.model;

import java.util.List;

public class DDIQuery {

	private String type;
	private String label;
	
	public DDIQuery() {
		
	}
	
	public DDIQuery(String label) {
		super();
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


}
