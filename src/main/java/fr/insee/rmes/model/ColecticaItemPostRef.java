package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.rmes.model.ColecticaItemRef.Unformatted;
import fr.insee.rmes.utils.ddi.ItemFormat;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class ColecticaItemPostRef extends ColecticaPostRefDisplayed {

	@JsonProperty("Identifier")
	public String identifier;

	@JsonProperty("Version")
	public String version;

	@JsonProperty("AgencyId")
	@Value("${fr.insee.rmes.api.remote.metadata.agency}")
	public String agencyId;

	public ColecticaItemPostRef() {
	}

	public ColecticaItemPostRef(String identifier, String version) {
		this.identifier = identifier;
		this.version = version;
	}

	@JsonProperty("ItemType")
	private String itemType;

	@JsonProperty("Notes")
	private List<Unformatted> notes = new ArrayList<Unformatted>();

	@JsonProperty("VersionDate")
	private String versionDate;

	@JsonProperty("VersionResponsibility")
	private String versionResponsibility;

	@JsonProperty("IsPublished")
	private boolean isPublished;

	@JsonProperty("IsDeprecated")
	private boolean isDeprecated;

	@JsonProperty("ItemFormat")
	private String itemFormat;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public List<Unformatted> getNotes() {
		return notes;
	}

	public void setNotes(List<Unformatted> notes) {
		this.notes = notes;
	}

	public String getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}

	public String getVersionResponsibility() {
		return versionResponsibility;
	}

	public void setVersionResponsibility(String versionResponsibility) {
		this.versionResponsibility = versionResponsibility;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	public boolean isDeprecated() {
		return isDeprecated;
	}

	public void setDeprecated(boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}

	public String getItemFormat() {
		return itemFormat;
	}

	public void setItemFormat(Enum<ItemFormat> itemFormat) {
		this.itemFormat = itemFormat.toString();
	}

	@Override
	public String toString() {
		return "ColecticaItemPostRef [identifier=" + identifier + ", version=" + version + ", agencyId=" + agencyId
				+ ", itemType=" + itemType + ", notes=" + notes + ", versionDate=" + versionDate
				+ ", versionResponsibility=" + versionResponsibility + ", isPublished=" + isPublished
				+ ", isDeprecated=" + isDeprecated + ", itemFormat=" + itemFormat + ", item=" + item + "]";
	}
	
	

}
