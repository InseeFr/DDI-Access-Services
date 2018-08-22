package fr.insee.rmes.metadata.service.fragmentInstance;

import fr.insee.rmes.search.model.DDIItemType;

public interface FragmentInstanceService {
	
	/**
	 * get all fragments thanks to the UUID of the Top level item.
	 * @param idTopLevel : UUID of the top level item
	 * @throws Exception 
	 * @return String : fragmentInstance with its children represented as fragments.
	 */
	String getFragmentInstance(String idTopLevel, DDIItemType itemType) throws Exception;

}
