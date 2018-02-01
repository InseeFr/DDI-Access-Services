package fr.insee.rmes.metadata.service.ddiinstance;

public interface DDIInstanceService {
	/**
	 * Get a DDI Instance using the DDI format
	 * 
	 * @param id
	 *            : DDI Instance Id
	 * @return DDI Document
	 * @throws Exception
	 */
	String getDDIInstance(String id) throws Exception;
}
