package fr.insee.rmes.metadata.service;

import java.util.List;

import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.model.ResourcePackage;

public interface MetadataService {

	String getDerefDDIDocumentWithExternalRP(String itemId, String ressourcePackageId) throws Exception;

	String getDerefDDIDocument(String itemId) throws Exception;

	String getDDIDocument(String itemId) throws Exception;

	String getDDIInstance(String id) throws Exception;

	String getSequence(String id) throws Exception;

	String getQuestion(String id) throws Exception;

	List<Unit> getUnits() throws Exception;

	List<String> getGroupIds() throws Exception;

	List<String> getRessourcePackageIds() throws Exception;

	Relationship[] getRelationship(ObjectColecticaPost relationshipPost) throws Exception;
	
	Relationship[] getRelationshipChildren(ObjectColecticaPost relationshipPost) throws Exception;
	
	ResourcePackage getResourcePackage(String id) throws Exception;

}
