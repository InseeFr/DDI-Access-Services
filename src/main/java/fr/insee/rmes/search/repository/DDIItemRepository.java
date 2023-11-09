package fr.insee.rmes.search.repository;

import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseSearchItem;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface DDIItemRepository {

    default List<DDIItem> findByLabel(String label, String... types) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default List<DDIItem> findByLabelInSubGroup(String label, String subgroupId, String... types) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default List<DDIItem> getSubGroups() throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default List<DDIItem> getStudyUnits(String seriesId) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default List<DDIItem> getDataCollections(String operationId) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default List<ResponseSearchItem> getItemsByCriteria(String subgroupId, String operationId, String dataCollectionId, DDIQuery criteria) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default void deleteAll() throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default DDIItem getItemById(String id) throws Exception {
        throw new RuntimeException("Not Implemented");
    }

    default List<DDIItem> getGroups() throws Exception {
        throw new RuntimeException("Not Implemented");
    }

}
