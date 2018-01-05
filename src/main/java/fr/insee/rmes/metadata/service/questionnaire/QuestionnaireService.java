package fr.insee.rmes.metadata.service.questionnaire;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

public interface QuestionnaireService {

	/**
	 * Get a questionnaire with its id, agency and version
	 * 
	 * @param id
	 *            : questionnaireId
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestionnaire(String id, String ressourcePackageId) throws Exception;
	
	/**
	 * add the Study Unit Node to the map used as a parameter in mapStudyUnit.
	 * @param map
	 * @param node
	 * @param mapStudyUnit
	 * @throws Exception
	 */
	void addStudyUnit(Map<Node, String> map, Node node, TreeMap<Integer, Map<Node, String>> mapStudyUnit)
			throws Exception;
	/**
	 * add the DataCollection Node to the map used as a parameter in mapStudyUnit.
	 * @param map
	 * @param node
	 * @param mapStudyUnit
	 * @throws Exception
	 */
	void addDataCollection(Map<Node, String> map, Node node, TreeMap<Integer, Map<Node, String>> mapStudyUnit)
			throws Exception;
	/**
	 * Get the studyUnit Node thanks to the id of this StudyUnit.
	 * @param id : id of the study unit expected
	 * @return Node resultStudyUnitNode
	 * @throws Exception
	 */
	Node getStudyUnitNode(String id) throws Exception;
}
