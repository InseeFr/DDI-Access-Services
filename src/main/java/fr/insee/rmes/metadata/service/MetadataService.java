package fr.insee.rmes.metadata.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.utils.ddi.DDIFragmentDocumentBuilder;
import fr.insee.rmes.utils.ddi.Envelope;

public interface MetadataService {

	String getDDIDocument(String itemId, String groupId) throws Exception;
	
	/**
	 * Get the fragment of a Codelist 
	 * @param res : variable to save the CodeList Fragment
	 * @param fragment : xml document of the main fragment
	 * @param itemId : id of the codeList
	 * @return StringBuilder res (modified)
	 * @throws Exception
	 */
	StringBuilder getFragmentCodeList(StringBuilder res, String fragment, String itemId) throws Exception;

	/**
	 * Add a categoryScheme to a new Map to store the customItems
	 * @param categoryIdRes : id of the first Category
	 * @param ressourcePackageId : id of the ressourcePackage
	 * @param indexInMap : position in the main Map (generally 1)
	 * @return TreeMap<Integer, Map<Node, String>> New Map of custom items
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategoryScheme(String categoryIdRes, String ressourcePackageId,
			int indexInMap) throws Exception;
	/**
	 * Add a category to an existing main Map of custom items
	 * @param categoryCustomItems : target main Map
	 * @param categoryIdRes : id of the new Category
	 * @param indexInMap : position in the main Map (generally >1)
	 * @return TreeMap<Integer, Map<Node, String>> Map with the new Category
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategoryById(TreeMap<Integer, Map<Node, String>> categoryCustomItems,
			String categoryIdRes, int indexInMap) throws Exception;
	/**
	 * Add all the categories to a new Main Map (including the CategoryScheme)
	 * @param fragment : fragment without the categories
	 * @param ressourcePackageId
	 * @param categoryCustomItems : target main Map
	 * @return TreeMap<Integer, Map<Node, String>> Map with the Categories.
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategories(String fragment, String ressourcePackageId,
			TreeMap<Integer, Map<Node, String>> categoryCustomItems) throws Exception;

	/**
	 * Get a List of codes with their id, agency and versions
	 * 
	 * @param id
	 *            : id CodeListReference
	 * @param String
	 *            packageId : id of sourcePackage
	 * @return Fragments
	 * @throws Exception
	 */
	String getCodeList(String id, String packageId) throws Exception;

	/**
	 * Get a questionnaire with its id, agency and version
	 * 
	 * @param id
	 *            : questionnaireId
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestionnaire(String id) throws Exception;

	/**
	 * Get a sequence with its id, agency and version
	 * 
	 * @param id
	 *            : sequenceId
	 * @return Fragment
	 * @throws Exception
	 */
	String getSequence(String id) throws Exception;

	/**
	 * Get a question with its id, agency and version
	 * 
	 * @param id
	 *            : questionId
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestion(String id) throws Exception;

	/**
	 * Return a List of Units
	 * 
	 * @return € /k€ / %
	 * @throws Exception
	 */
	List<Unit> getUnits() throws Exception;

	/**
	 * Get a DDI document for a specific item without the default DDI envelope.
	 * 
	 * @param itemId
	 *            : identifier of the item
	 * @param resourcePackageId
	 *            : identifier of the DDI package (useless currently but
	 *            required)
	 * @return String DDI item
	 * @throws Exception
	 */
	String getDDIDocumentWithoutEnvelope(String itemId, String resourcePackageId) throws Exception;

	/**
	 * Get a DDI document for a specific item without a specific DDI envelope
	 * (envelopeName).
	 * 
	 * @param itemId
	 *            : identifier of the item
	 * @param resourcePackageId
	 *            : identifier of the DDI package (useless currently but
	 *            required)
	 * @param envelopeName
	 *            : name of the target envelope
	 * @return String DDI item
	 * @throws Exception
	 */
	String getDDIDocumentWithoutEnvelope(String itemId, String resourcePackageId, Enum<Envelope> envelopeName)
			throws Exception;

	List<String> getGroupIds() throws Exception;

	List<String> getRessourcePackageIds() throws Exception;

	String getDDIItemWithEnvelope(String itemId, String resourcePackageId, Enum<Envelope> nameEnvelope)
			throws Exception;

	/**
	 * 
	 * @param itemId
	 * @param resourcePackageId
	 * @param nameEnvelope
	 * @param nodesWithParentNames
	 *            contain a map with the order (mapKey.keySet()) and the Node in
	 *            value (map.map.KeySet() And the value of the secondMap is
	 *            associated to the parentName.
	 * @return String DDIDocument doc ---> doc.toString();
	 * @throws Exception
	 */
	String getDDIItemWithEnvelopeAndCustomItems(String itemId, String resourcePackageId, Enum<Envelope> nameEnvelope,
			TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception;

}
