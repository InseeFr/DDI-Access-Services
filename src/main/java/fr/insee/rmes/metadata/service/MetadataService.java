package fr.insee.rmes.metadata.service;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.Code;
import fr.insee.rmes.metadata.model.CodeList;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.search.model.ResponseItem;

public interface MetadataService {

	ColecticaItem getItem(String id) throws Exception;

	ColecticaItemRefList getChildrenRef(String id) throws Exception;

	List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception;

	List<String> getGroupIds() throws Exception;

	String getDDIDocument(String itemId, String groupId) throws Exception;

	ResponseItem getDDIRoot(String id) throws Exception;

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
	 * Get a Serie with its id, agency and version
	 * 
	 * @param id
	 *            : id CodeListReference
	 * @param String
	 *            packageId : id of sourcePackage
	 * @return Fragment
	 * @throws Exception
	 */
	String getSerie(String id, String packageId) throws Exception;

	/**
	 * Get an Operation with its id, agency and version
	 * 
	 * @param id
	 *            : id SubGroupReference
	 * @param String
	 *            packageId : id of sourcePackage
	 * @return Fragment
	 * @throws Exception
	 */
	String getOperation(String id, String packageId) throws Exception;

	/**
	 * Get a dataCollection with its id, agency and version
	 * 
	 * @param id
	 *            : dataCollectionId
	 * @param packageId
	 *            : id of the Colectica Package
	 * @return Fragment
	 * @throws Exception
	 */
	String getDataCollection(String id, String packageId) throws Exception;

	/**
	 * Get a questionnaire with its id, agency and version
	 * 
	 * @param id
	 *            : questionnaireId
	 * @param packageId
	 *            : id of the Colectica Package
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestionnaire(String id, String packageId) throws Exception;

	/**
	 * Get a sequence with its id, agency and version
	 * 
	 * @param id
	 *            : sequenceId
	 * @param packageId
	 *            : id of the Colectica Package
	 * @return Fragment
	 * @throws Exception
	 */
	String getSequence(String id, String packageId) throws Exception;

	/**
	 * Get a question with its id, agency and version
	 * 
	 * @param id
	 *            : id of the Instrument
	 * @param packageId
	 *            : id of the Colectica Package
	 * @return Fragment
	 * @throws Exception
	 */
	String getQuestion(String id, String packageId) throws Exception;

	Map<ColecticaItemPostRef, String> postNewItems(ColecticaItemPostRefList refs) throws Exception;

	Map<ColecticaItemPostRef, String> postUpdateItems(ColecticaItemPostRefList refs) throws Exception;

	/**
	 * Return a List of Units
	 * 
	 * @return € /k€ / %
	 * @throws Exception
	 */
	List<Unit> getUnits() throws Exception;

	List<String> getRessourcePackageIds();

	List<ResponseItem> getDDICodeListSchemeFromResourcePackage(String id) throws Exception;

	List<ResponseItem> getDDICodeListSchemeFromGroupRoot(String id) throws Exception;
	
	/**
	 * Get a DDI document for a specific item without the default DDI envelope.
	 * @param itemId : identifier of the item
	 * @param resourcePackageId : identifier of the DDI package (useless currently but required)
	 * @return String DDI item
	 * @throws Exception
	 */
	String getDDIDocumentWithoutEnvelope(String itemId, String resourcePackageId) throws Exception;
	
	/**
	 *  Get a DDI document for a specific item without a specific DDI envelope (envelopeName).
	 * @param itemId : identifier of the item
	 * @param resourcePackageId : identifier of the DDI package (useless currently but required)
	 * @param envelopeName : name of the target envelope
	 * @return String DDI item
	 * @throws Exception
	 */
	String getDDIDocumentWithoutEnvelope(String itemId, String resourcePackageId, String envelopeName) throws Exception;
}
