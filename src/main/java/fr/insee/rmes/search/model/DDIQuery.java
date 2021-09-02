package fr.insee.rmes.search.model;

import java.util.List;

public class DDIQuery {

	private List<String> types;
	private String label;
	
	public DDIQuery() {
		
	}
	
	public DDIQuery(String label) {
		super();
		this.label = label;
	}

	public List<String> getTypes() {
		return types;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


}
