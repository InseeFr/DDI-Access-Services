package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCollection extends ColecticaItem{

	@JsonProperty("Label")
	private String label;
	
	@JsonProperty("QuestionItem")
	private Question question;
	
	@JsonProperty("Instrument")
	private Questionnaire questionnaire;
	
	@JsonProperty("ControlConstructScheme")
	private ControlConstructScheme controlConstructScheme;
	
	

	public ControlConstructScheme getControlConstructScheme() {
		return controlConstructScheme;
	}

	public void setControlConstructScheme(ControlConstructScheme controlConstructScheme) {
		this.controlConstructScheme = controlConstructScheme;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	@Override
	public String toString() {
		return "DataCollection [label=" + label + ", question=" + question + ", questionnaire=" + questionnaire
				+ ", controlConstructScheme=" + controlConstructScheme + ", agencyId=" + agencyId + ", version="
				+ version + ", identifier=" + identifier + "]";
	}

	
	
	
}
