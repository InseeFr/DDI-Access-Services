package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Citation {

	@JsonProperty("Title")
	private String title = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Citation [title=" + title + "]";
	}

}
