package fr.insee.rmes.metadata.service.questionnaire;

public interface QuestionnaireService {

	/**
	 * Get a questionnaire using the DDI format
	 * 
	 * @param idDdiInstrument
	 * @return String DDI document containing a Questionnaire
	 * @throws Exception
	 */
	String getQuestionnaire(String idDdiInstrument) throws Exception;

}
