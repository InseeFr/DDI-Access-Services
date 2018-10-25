package fr.insee.rmes.metadata.service.ddiinstance;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;

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
	
	public void addDDIInstanceInformationToDocBuilder(ColecticaItem ddiInstance, DDIDocumentBuilder docBuilder)  throws Exception;
}
