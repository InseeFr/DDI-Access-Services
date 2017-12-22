package fr.insee.rmes.metadata.service.questionnaire;

public interface QuestionnaireService {
	
	/**
	 * Get a questionnaire with its id, agency and version
	 * 
	 * @param id
	 *            : questionnaireId
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestionnaire(String id) throws Exception;

}
