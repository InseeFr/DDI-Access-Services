package fr.insee.rmes.metadata.service;

import java.util.List;
import java.util.Map;

import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.utils.ddi.DDIFragmentDocumentBuilder;
import fr.insee.rmes.utils.ddi.Envelope;

public interface MetadataService {

	String getDDIDocument(String itemId, String groupId) throws Exception;

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
	 *            : identifier of the DDI package (useless currently but required)
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
	 *            : identifier of the DDI package (useless currently but required)
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

	String getDDIItemsWithEnvelope(String resourcePackageId,
			DDIFragmentDocumentBuilder ddiFragmentBuilder, Enum<Envelope> rootEnvelope) throws Exception;



	

}
