package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColecticaPostRefDisplayed {

	

	@JsonProperty("Item")
	protected String item;


	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

}
