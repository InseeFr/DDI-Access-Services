package fr.insee.rmes.metadata.service.questionnaire;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

public interface QuestionnaireService {

	/**
	 * Get a questionnaire using the DDI format
	 * 
	 * @param id
	 *            : questionnaireId
	 * @param ressourcePackageId
	 * @param dataCollectionId
	 * @param subGroupId
	 * @param groupId
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestionnaire(String id, String ressourcePackageId, String dataCollectionId, String subGroupId,
			String groupId) throws Exception;



	/**
	 * Get the Instrument Node thanks to the id of this Instrument.
	 * 
	 * @param id
	 *            : id of the Instrument expected
	 * @return Node resultInstrumentNode
	 * @throws Exception
	 */
	Node getInstrumentNode(String id) throws Exception;
}
