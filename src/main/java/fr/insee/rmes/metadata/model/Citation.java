package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Citation {

	@JsonProperty("Title")
	private String titile = "";

	public String getTitle() {
		return titile;
	}

	public void setTitle(String title) {
		this.titile = title;
	}

	@Override
	public String toString() {
		return "Citation [titile=" + titile + "]";
	}
	
	
	
}
