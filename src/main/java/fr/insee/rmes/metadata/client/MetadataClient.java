package fr.insee.rmes.metadata.client;

import java.util.List;
import java.util.Map;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;

public interface MetadataClient {

	ColecticaItem getItem(String id) throws Exception;

	List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception;

	ColecticaItemRefList getChildrenRef(String id) throws Exception;

	Integer getLastestVersionItem(String id) throws Exception;

	String postItems(ColecticaItemPostRefList refs) throws Exception;

	List<Unit> getUnits() throws Exception;

	String postItem(ColecticaItemPostRef ref);
}
