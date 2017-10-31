package fr.insee.rmes.metadata.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.insee.rmes.metadata.model.ColecticaItemRef.Unformatted;

public class ColecticaItemPostRef {

	@JsonProperty("Identifier")
	public String identifier;

	@JsonProperty("Version")
	public String version;

	@JsonProperty("AgencyId")
	public String agencyId;

	public ColecticaItemPostRef() {
	}

	public ColecticaItemPostRef(String identifier, String version, String agencyId) {
		this.agencyId = agencyId;
		this.identifier = identifier;
		this.version = version;
	}

	@JsonProperty("ItemType")
	private UUID itemType;

	@JsonProperty("Item")
	private String item;

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
	private UUID itemFormat;

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

	public UUID getItemType() {
		return itemType;
	}

	public void setItemType(UUID itemType) {
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

	public UUID getItemFormat() {
		return itemFormat;
	}

	public void setItemFormat(UUID itemFormat) {
		this.itemFormat = itemFormat;
	}

}
