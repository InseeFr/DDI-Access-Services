package fr.insee.rmes.metadata.service;

import java.util.List;

import fr.insee.rmes.metadata.model.ColecticaItem;
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

	List<Unit> getUnits() throws Exception;
}
