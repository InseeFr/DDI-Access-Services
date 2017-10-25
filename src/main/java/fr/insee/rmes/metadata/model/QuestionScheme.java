package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionScheme extends ColecticaItem{
	
	@JsonProperty("TypeOfObject")
	private String typeOfObject;

	public String getTypeOfobject() {
		return typeOfObject;
	}

	public void setTypeOfobject(String typeOfobject) {
		this.typeOfObject = typeOfobject;
	}

	@Override
	public String toString() {
		return "QuestionScheme [typeOfobject=" + typeOfObject + ", agencyId=" + agencyId + ", version=" + version
				+ ", identifier=" + identifier + "]";
	}
	
	

}
