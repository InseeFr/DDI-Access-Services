package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category  extends ColecticaItem{

	@JsonProperty("Label")
	private String label;

	public String getLabel() {
		
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "Category [label=" + label + ", agencyId=" + agencyId + ", version=" + version + ", identifier="
				+ identifier + "]";
	}
	
	
}
