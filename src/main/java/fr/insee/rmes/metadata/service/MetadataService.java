package fr.insee.rmes.metadata.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.utils.ddi.Envelope;

public interface MetadataService {

	String getDDIDocument(String itemId, String groupId) throws Exception;
	
	String getItemByType(String id, DDIItemType type) throws Exception;

	

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
