package fr.insee.rmes.metadata.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodeList extends ColecticaItem{
	@JsonProperty("Code")
	private List<Code> codeList = new ArrayList<Code>();
	@JsonProperty("isUniversallyUnique")
	private boolean isUniversallyUnique = true;

	public List<Code> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<Code> codeList) {
		this.codeList = codeList;
	}

	public boolean isUniversallyUnique() {
		return isUniversallyUnique;
	}

	public void setUniversallyUnique(Boolean isUniversallyUnique) {
		this.isUniversallyUnique = isUniversallyUnique;
	}

	@Override
	public String toString() {
		return "CodeList [codeList=" + codeList + ", isUniversallyUnique=" + isUniversallyUnique + ", itemType="
				+ itemType + ", agencyId=" + agencyId + ", version=" + version + ", identifier=" + identifier
				+ ", item=" + item + ", versionDate=" + versionDate + ", versionResponsibility=" + versionResponsibility
				+ ", isPublished=" + isPublished + ", isDeprecated=" + isDeprecated + ", isProvisional=" + isProvisional
				+ ", itemFormat=" + itemFormat + ", versionRationale=" + versionRationale + ", notes=" + notes + "]";
	}
	
	
	
	
	
}
