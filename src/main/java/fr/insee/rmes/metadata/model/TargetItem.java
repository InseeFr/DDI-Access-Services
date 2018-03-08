package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetItem {

	/*
	 * { "ItemTypes": [ "StringUUID" ], "TargetItem": { "AgencyId": "String",
	 * "Identifier": "StringUUID", "Version": Integer },
	 * "UseDistinctResultItem": true, "UseDistinctTargetItem": true }
	 */

	@JsonProperty("AgencyId")
	private String AgencyId;

	@JsonProperty("Identifier")
	private String Identifier;

	@JsonProperty("Version")
	private Integer Version;

	public String getAgencyId() {
		return AgencyId;
	}

	public void setAgencyId(String agencyId) {
		AgencyId = agencyId;
	}

	public String getIdentifier() {
		return Identifier;
	}

	public void setIdentifier(String identifier) {
		Identifier = identifier;
	}

	public Integer getVersion() {
		return Version;
	}

	public void setVersion(Integer version) {
		Version = version;
	}

	@Override
	public String toString() {
		return "TargetItem [AgencyId=" + AgencyId + ", Identifier=" + Identifier + ", Version=" + Version + "]";
	}

}
