package fr.insee.rmes.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Questionnaire extends ColecticaItem{
	
	@JsonProperty("Label")
	private String label;
	
	@JsonProperty("TypeOfInstrument")
	private String typeOfInstrument;
	
	@JsonProperty("ControlConstructReference")
	private ControlConstructReference controlConstructReference;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTypeOfInstrument() {
		return typeOfInstrument;
	}

	public void setTypeOfInstrument(String typeOfInstrument) {
		this.typeOfInstrument = typeOfInstrument;
	}

	public ControlConstructReference getControlConstructReference() {
		return controlConstructReference;
	}

	public void setControlConstructReference(ControlConstructReference controlConstructReference) {
		this.controlConstructReference = controlConstructReference;
	}

	@Override
	public String toString() {
		return "Questionnaire [label=" + label + ", typeOfInstrument=" + typeOfInstrument
				+ ", controlConstructReference=" + controlConstructReference + ", agencyId=" + agencyId + ", version="
				+ version + ", identifier=" + identifier + "]";
	}

	
	
}
