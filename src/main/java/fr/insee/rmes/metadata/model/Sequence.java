package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sequence extends ColecticaItem{
	
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
		return "Sequence [label=" + label + ", agencyId=" + agencyId + ", version=" + version + ", identifier="
				+ identifier + "]";
	}
	
	

}
