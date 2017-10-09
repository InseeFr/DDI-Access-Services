package fr.insee.rmes.search.model;

import java.util.List;

public class DDIQuery {

	private List<String> types;
	private String filter;

	public List<String> getTypes() {
		return types;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
