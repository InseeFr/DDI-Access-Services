package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author UP3U20
 *
 */
public class Code extends ColecticaItem {
	
	@JsonProperty("levelNumber")
	private int levelNumber;
	
	@JsonProperty("Value")
	private String value = null;
	
	@JsonProperty("CategoryReference")
	private CategoryReference categoryReference;
	
	@JsonProperty("Category")
	private Category category;
	
	@JsonProperty("isDiscrete")
	private boolean isDiscrete;
	
	public int getLevelNumber() {
		return levelNumber;
	}
	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isDiscrete() {
		return isDiscrete;
	}
	public void setDiscrete(boolean isDiscrete) {
		this.isDiscrete = isDiscrete;
	}
	public CategoryReference getCategoryReference() {
		return categoryReference;
	}
	public void setCategoryReference(CategoryReference categoryReference) {
		this.categoryReference = categoryReference;
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	@Override
	public String toString() {
		return "Code [levelNumber=" + levelNumber + ", value=" + value + ", categoryReference=" + categoryReference
				+ ", category=" + category + ", isDiscrete=" + isDiscrete + ", agencyId=" + agencyId + ", version="
				+ version + ", identifier=" + identifier + "]";
	}
	
	
	
}
