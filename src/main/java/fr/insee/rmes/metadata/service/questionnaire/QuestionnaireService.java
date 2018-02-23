package fr.insee.rmes.metadata.service.questionnaire;

public interface QuestionnaireService {

	/**
	 * Get a questionnaire using the DDI format
	 * 
	 * @param idDdiInstance
	 * @param idDdiInstrument
	 * @return String DDI document containing a Questionnaire
	 * @throws Exception
	 */
	String getQuestionnaire(String idDdiInstance, String idDdiInstrument) throws Exception;

}
