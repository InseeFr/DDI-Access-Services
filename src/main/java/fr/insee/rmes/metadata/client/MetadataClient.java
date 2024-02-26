package fr.insee.rmes.metadata.client;

import java.util.List;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.ColecticaSearchItemRequest;
import fr.insee.rmes.metadata.model.ColecticaSearchItemResponse;
import fr.insee.rmes.metadata.model.ColecticaSearchSetRequest;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Unit;

public interface MetadataClient {

	ColecticaItem getItem(String id) throws Exception;

	List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception;
	
	ColecticaSearchItemResponse searchItems(ColecticaSearchItemRequest req) throws Exception;

	ColecticaItemRefList getChildrenRef(String id) throws Exception;

	Integer getLastestVersionItem(String id) throws Exception;

	String postItems(ColecticaItemPostRefList refs) throws Exception;

	List<Unit> getUnits() throws Exception;

	String postItem(ColecticaItemPostRef ref) throws Exception;

	Relationship[] getRelationship(ObjectColecticaPost objectColecticaPost) throws Exception;

	Relationship[] getRelationshipChildren(ObjectColecticaPost relationshipPost) throws Exception;
	
	Relationship[] getItemsReferencingSpecificItem(ObjectColecticaPost objectColecticaPost) throws Exception;
	
	Relationship[] searchSets(ColecticaSearchSetRequest setRequest);

}
