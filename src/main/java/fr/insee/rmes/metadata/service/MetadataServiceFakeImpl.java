package fr.insee.rmes.metadata.service;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.search.model.ResourcePackage;
import fr.insee.rmes.search.model.ResponseItem;

@Service
public class MetadataServiceFakeImpl implements MetadataService {

	private final static Logger logger = LogManager.getLogger(MetadataServiceFakeImpl.class);

	@Override
	public List<String> getGroupIds() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<Unit> getUnits() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseItem getDDIRoot(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResourcePackageId(Node rootNode) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ResponseItem> getGroups(Node node, ResponseItem ddiRoot) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ResponseItem> getSubGroups(Node node, ResponseItem group) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ResponseItem> getStudyUnits(Node node, ResponseItem subGroup) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ResponseItem> getDataCollections(Node node, ResponseItem studyUnit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ResponseItem> getInstrumentSchemes(Node node, ResponseItem dataCollection) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ResponseItem> getInstruments(Node node, ResponseItem instrumentScheme) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDDIDocument(String itemId, String resourcePackageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourcePackage getResourcePackage(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCodeList(String itemId, String ressourcePackageId) throws Exception {

		// TODO Auto-generated method stub
		return null;

	}

	

	@Override
	public List<String> getRessourcePackageIds() {
		// TODO Auto-generated method stub
		return null;
	}


	

	@Override
	public String getDDIDocumentWithoutEnvelope(String itemId, String resourcePackageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDDIDocumentWithoutEnvelope(String itemId, String resourcePackageId, String envelopeName)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestionnaire(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSequence(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestion(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDDIItemWithEnvelope(String itemId, String resourcePackageId, String nameEnvelope)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
