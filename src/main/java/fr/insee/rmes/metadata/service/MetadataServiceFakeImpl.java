package fr.insee.rmes.metadata.service;

import fr.insee.rmes.metadata.model.*;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.ResourcePackage;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.webservice.rest.RMeSException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetadataServiceFakeImpl implements MetadataService {

	private final static Logger logger = LogManager.getLogger(MetadataServiceFakeImpl.class);

	@Override
	public List<String> getGroupIds() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ColecticaItem getItem(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ColecticaItemRefList getChildrenRef(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
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
	public String getSerie(String id, String packageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOperation(String id, String packageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataCollection(String id, String packageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestionnaire(String id, String packageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSequence(String id, String packageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestion(String id, String packageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
  }

	@Override
	public Map<ColecticaItemPostRef, String> postNewItems(ColecticaItemPostRefList refs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<ColecticaItemPostRef, String> postUpdateItems(ColecticaItemPostRefList refs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
