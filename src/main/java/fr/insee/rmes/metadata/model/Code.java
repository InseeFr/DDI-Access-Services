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
	private CategoryReference categorieReference;
	
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
	public CategoryReference getCategorieReference() {
		return categorieReference;
	}
	public void setCategorieReference(CategoryReference categorieReference) {
		this.categorieReference = categorieReference;
	}
	public boolean isDiscrete() {
		return isDiscrete;
	}
	public void setDiscrete(boolean isDiscrete) {
		this.isDiscrete = isDiscrete;
	}
	@Override
	public String toString() {
		return "Code [levelNumber=" + levelNumber + ", value=" + value + ", categorieReference=" + categorieReference
				+ ", isDiscrete=" + isDiscrete + ", itemType=" + itemType + ", agencyId=" + agencyId + ", version="
				+ version + ", identifier=" + identifier + ", item=" + item + ", versionDate=" + versionDate
				+ ", versionResponsibility=" + versionResponsibility + ", isPublished=" + isPublished
				+ ", isDeprecated=" + isDeprecated + ", isProvisional=" + isProvisional + ", itemFormat=" + itemFormat
				+ ", versionRationale=" + versionRationale + ", notes=" + notes + "]";
	}
	
	
}
