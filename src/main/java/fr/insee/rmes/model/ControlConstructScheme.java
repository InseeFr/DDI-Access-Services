package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlConstructScheme extends ColecticaItem
{
	@JsonProperty("Sequence")
	private Sequence sequence;

	public Sequence getSequence() {
		return sequence;
	}

	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		return "ControlConstructScheme [sequence=" + sequence + ", agencyId=" + agencyId + ", version=" + version
				+ ", identifier=" + identifier + "]";
	}
	
	

}
