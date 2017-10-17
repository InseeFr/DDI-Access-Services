package fr.insee.rmes.metadata.service;

import java.util.List;

import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.Code;
import fr.insee.rmes.metadata.model.ColecticaItem;
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
	 * @param id : id CodeListReference
	 * @param String packageId : identifiant du packageSource
	 * @return List<Code> codeList
	 * @throws Exception
	 */
	String getCodeList (String id, String packageId) throws Exception;
	

	List<Unit> getUnits() throws Exception;
}
