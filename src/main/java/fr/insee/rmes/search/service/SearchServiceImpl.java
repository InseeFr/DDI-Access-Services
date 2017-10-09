package fr.insee.rmes.search.service;

import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.search.repository.DDIItemRepository;

@Service
public class SearchServiceImpl implements SearchService {

	@Value("${fr.insee.rmes.search.ddiItemRepository.impl}")
	private static final String ddiItemRepositoryImpl = "DDIItemFakeImpl";

	@Autowired
	@Qualifier(ddiItemRepositoryImpl)
	private DDIItemRepository ddiItemRepository;

	@Override
	public IndexResponse save(String type, ResponseItem item) throws Exception {
		return ddiItemRepository.save(type, item);
	}

	public List<DDIItem> searchByLabel(String label, String... types) throws Exception {
		return ddiItemRepository.findByLabel(label, types);
	}

	public List<DDIItem> searchByLabelInSubgroup(String label, String subgroupId, String... types) throws Exception {
		return ddiItemRepository.findByLabelInSubGroup(label, subgroupId, types);
	}

	public DeleteResponse delete(String type, String id) throws Exception {
		return ddiItemRepository.delete(type, id);
	}

	@Override
	public List<DDIItem> getSubGroups() throws Exception {
		return ddiItemRepository.getSubGroups();
	}

	@Override
	public List<DDIItem> getStudyUnits(String subGroupId) throws Exception {
		return ddiItemRepository.getStudyUnits(subGroupId);
	}

	@Override
	public List<DDIItem> getDataCollections(String operationId) throws Exception {
		return ddiItemRepository.getDataCollections(operationId);
	}

	@Override
	public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
		return ddiItemRepository.getDataCollectionContext(dataCollectionId);
	}

}
