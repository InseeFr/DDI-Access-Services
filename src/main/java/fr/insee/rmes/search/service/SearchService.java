package fr.insee.rmes.search.service;

import java.util.List;

import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseSearchItem;

public interface SearchService {

	List<DDIItem> searchByLabel(String label, String... types) throws Exception;

	List<ResponseSearchItem> searchByLabel(String subgroupId, String operationId, String dataCollectionId,
			DDIQuery criteria) throws Exception;

	List<DDIItem> searchByLabelInSubgroup(String label, String subgroupId, String... types) throws Exception;

	List<DDIItem> getSubGroups() throws Exception;

	List<DDIItem> getStudyUnits(String seriesId) throws Exception;

	List<DDIItem> getDataCollections(String operationId) throws Exception;

	DDIItem getDDIItemById(String id) throws Exception;

	DataCollectionContext getDataCollectionContext(String id) throws Exception;

	void deleteAll() throws Exception;

	List<DDIItem> getGroups() throws Exception;

}
