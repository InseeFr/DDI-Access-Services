package fr.insee.rmes.metadata.service.groups;

public interface GroupsService {
	
	/**
	 * get all groups thanks to the UUID of the Top level item.
	 * @param idTopLevel : UUID of the top level item
	 * @throws Exception 
	 * @return String : fragmentInstance with all fragment's groups.
	 */
	String getGroup(String idTopLevel) throws Exception;

}
