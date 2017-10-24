package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pierre
 *
 */
public class CategoryReference extends ColecticaItem {
	
	@JsonProperty("TypeOfObject")
	private String typeOfObject = null;

	public String getTypeOfObject() {
		return typeOfObject;
	}

	public void setTypeOfObject(String typeOfObject) {
		this.typeOfObject = typeOfObject;
	}

	@Override
	public String toString() {
		return "CategoryReference [typeOfObject=" + typeOfObject + ", agencyId=" + agencyId + ", version=" + version
				+ ", identifier=" + identifier + "]";
	}
	
	

}
