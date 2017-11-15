package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Options {

	@JsonProperty("VersionRationale")
	private Object versionRationale = new Object();

	@JsonProperty("SetName")
	private String SetName = "";

	public Object getVersionRationale() {
		return versionRationale;
	}

	public void setVersionRationale(Object versionReversionRationale) {
		this.versionRationale = versionReversionRationale;
	}

	public String getSetName() {
		return SetName;
	}

	public void setSetName(String setName) {
		SetName = setName;
	}

	@Override
	public String toString() {
		return "Options [versionReversionRationale=" + versionRationale + ", SetName=" + SetName + "]";
	}

}
