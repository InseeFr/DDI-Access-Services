package fr.insee.rmes.metadata.client;

import java.util.List;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;

public interface MetadataClient {

    ColecticaItem getItem(String id) throws Exception;
    List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception;
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
	List<Unit> getUnits() throws Exception;
}
