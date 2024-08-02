package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColecticaSearchItemResult {
	
	@JsonProperty("Summary")
	private JSONObject summary;

	@JsonProperty("ItemName")
	private Map<String, String> itemName;
	@JsonProperty("Label")
	private JSONObject label;
	@JsonProperty("Description")
	private JSONObject description;
	@JsonProperty("VersionRationale")
	private JSONObject versionRationale;
	@JsonProperty("MetadataRank")
	private Integer metadataRank;
	@JsonProperty("RepositoryName")
	private JSONObject repositoryName = null;
	@JsonProperty("IsAuthoritative")
	private Boolean isAuthoritative;
	@JsonProperty("Tags")
	private List<String> tags = new ArrayList<>();
	@JsonProperty("ItemType")
	private String itemType;
	@JsonProperty("AgencyId")
	private String agencyId;
	@JsonProperty("Version")
	private Integer version;
	@JsonProperty("Identifier")
	private String identifier;
	@JsonProperty("Item")
	private String item = null;
	@JsonProperty("Notes")
	private String notes = null;
	@JsonProperty("VersionDate")
	private String versionDate;
	@JsonProperty("VersionResponsibility")
	private JSONObject versionResponsibility;
	@JsonProperty("IsPublished")
	private Boolean isPublished = false;
	@JsonProperty("IsDeprecated")
	private Boolean isDeprecated = false;
	@JsonProperty("IsProvisional")
	private Boolean isProvisional = false;
	@JsonProperty("ItemFormat")
	private String itemFormat;
	public JSONObject getSummary() {
		return summary;
	}
	public void setSummary(JSONObject summary) {
		this.summary = summary;
	}
	public Map<String, String> getItemName() {
		return itemName;
	}
	public void setItemName(Map<String, String> itemName) {
		this.itemName = itemName;
	}
	public JSONObject getLabel() {
		return label;
	}
	public void setLabel(JSONObject label) {
		this.label = label;
	}
	public JSONObject getDescription() {
		return description;
	}
	public void setDescription(JSONObject description) {
		this.description = description;
	}
	public JSONObject getVersionRationale() {
		return versionRationale;
	}
	public void setVersionRationale(JSONObject versionRationale) {
		this.versionRationale = versionRationale;
	}
	public Integer getMetadataRank() {
		return metadataRank;
	}
	public void setMetadataRank(Integer metadataRank) {
		this.metadataRank = metadataRank;
	}
	public JSONObject getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(JSONObject repositoryName) {
		this.repositoryName = repositoryName;
	}
	public Boolean getIsAuthoritative() {
		return isAuthoritative;
	}
	public void setIsAuthoritative(Boolean isAuthoritative) {
		this.isAuthoritative = isAuthoritative;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getVersionDate() {
		return versionDate;
	}
	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}
	public JSONObject getVersionResponsibility() {
		return versionResponsibility;
	}
	public void setVersionResponsibility(JSONObject versionResponsibility) {
		this.versionResponsibility = versionResponsibility;
	}
	public Boolean getIsPublished() {
		return isPublished;
	}
	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	public Boolean getIsDeprecated() {
		return isDeprecated;
	}
	public void setIsDeprecated(Boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}
	public Boolean getIsProvisional() {
		return isProvisional;
	}
	public void setIsProvisional(Boolean isProvisional) {
		this.isProvisional = isProvisional;
	}
	public String getItemFormat() {
		return itemFormat;
	}
	public void setItemFormat(String itemFormat) {
		this.itemFormat = itemFormat;
	}
	@Override
	public String toString() {
		return "ColecticaSearchResult [summary=" + summary + ", itemName=" + itemName + ", label=" + label
				+ ", description=" + description + ", versionRationale=" + versionRationale + ", metadataRank="
				+ metadataRank + ", repositoryName=" + repositoryName + ", isAuthoritative=" + isAuthoritative
				+ ", tags=" + tags + ", itemType=" + itemType + ", agencyId=" + agencyId + ", version=" + version
				+ ", identifier=" + identifier + ", item=" + item + ", notes=" + notes + ", VersionDate=" + versionDate
				+ ", versionResponsibility=" + versionResponsibility + ", IsPublished=" + isPublished
				+ ", IsDeprecated=" + isDeprecated + ", IsProvisional=" + isProvisional + ", ItemFormat=" + itemFormat
				+ "]";
	}

}
