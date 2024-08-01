package fr.insee.rmes.model;



public class StudyUnit extends ColecticaItem{
	
	private Citation citation;
	
	private DataCollection datacollection;

	public Citation getCitation() {
		return citation;
	}

	public void setCitation(Citation citation) {
		this.citation = citation;
	}

	public DataCollection getDatacollection() {
		return datacollection;
	}

	public void setDatacollection(DataCollection datacollection) {
		this.datacollection = datacollection;
	}

	@Override
	public String toString() {
		return "StudyUnit [citation=" + citation + ", datacollection=" + datacollection + ", agencyId=" + agencyId
				+ ", version=" + version + ", identifier=" + identifier + "]";
	}
	
	
}
