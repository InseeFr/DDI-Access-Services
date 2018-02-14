package fr.insee.rmes.metadata.service;

import java.util.List;

import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.search.model.DDIItemType;

public interface MetadataService {

	String getDerefDDIDocumentWithExternalRP(String itemId, String groupId) throws Exception;
	
	String getDerefDDIDocument(String itemId) throws Exception;
	
	String getDDIDocument(String itemId) throws Exception;
	
	String getItemByType(String id, DDIItemType type) throws Exception;

	String getDDIInstance(String id) throws Exception;
	
	String getSequence(String id) throws Exception;

	String getQuestion(String id) throws Exception;

	List<Unit> getUnits() throws Exception;
	
	List<String> getGroupIds() throws Exception;

	List<String> getRessourcePackageIds() throws Exception;

}
