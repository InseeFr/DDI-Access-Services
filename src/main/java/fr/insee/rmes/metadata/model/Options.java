package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Options {

	@JsonProperty("VersionRationale")
	private Object versionRationale = new Object();

	@JsonProperty("SetName")
	private String setName = "";

	public Object getVersionRationale() {
		return versionRationale;
	}

	public void setVersionRationale(Object versionReversionRationale) {
		this.versionRationale = versionReversionRationale;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	@Override
	public String toString() {
		return "Options [versionReversionRationale=" + versionRationale + ", SetName=" + setName + "]";
	}

}
