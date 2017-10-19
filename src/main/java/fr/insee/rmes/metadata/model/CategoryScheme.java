package fr.insee.rmes.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryScheme extends ColecticaItem{
	
	@JsonProperty("Label")
	private String label;
	
	@JsonProperty("Category")
	private List<Category> categories;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "CategoryScheme [label=" + label + ", categories=" + categories + ", agencyId=" + agencyId + ", version="
				+ version + ", identifier=" + identifier + "]";
	}
	
	

}
