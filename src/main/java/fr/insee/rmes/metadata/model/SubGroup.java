package fr.insee.rmes.metadata.model;

import java.util.List;

public class SubGroup extends ColecticaItem{
	
	List<StudyUnit> studyUnits;
	Citation citation;
	public List<StudyUnit> getStudyUnits() {
		return studyUnits;
	}
	public void setStudyUnits(List<StudyUnit> studyUnits) {
		this.studyUnits = studyUnits;
	}
	public Citation getCitation() {
		return citation;
	}
	public void setCitation(Citation citation) {
		this.citation = citation;
	}
	@Override
	public String toString() {
		return "SubGroup [studyUnits=" + studyUnits + ", citation=" + citation + ", agencyId=" + agencyId + ", version="
				+ version + ", identifier=" + identifier + "]";
	}
	
	
	
	

}
