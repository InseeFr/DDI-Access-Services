package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetItem {

	/*
	 * { "ItemTypes": [ "StringUUID" ], "TargetItem": { "AgencyId": "String",
	 * "Identifier": "StringUUID", "Version": Integer },
	 * "UseDistinctResultItem": true, "UseDistinctTargetItem": true }
	 */

	@JsonProperty("AgencyId")
	private String agencyId;

	@JsonProperty("Identifier")
	private String identifier;

	@JsonProperty("Version")
	private Integer version;

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "TargetItem [AgencyId=" + agencyId + ", Identifier=" + identifier + ", Version=" + version + "]";
	}

}
