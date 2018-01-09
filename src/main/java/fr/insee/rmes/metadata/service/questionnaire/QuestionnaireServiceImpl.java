package fr.insee.rmes.metadata.service.questionnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.codeList.CodeListServiceImpl;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.service.SearchService;
import fr.insee.rmes.utils.ddi.Envelope;
import fr.insee.rmes.webservice.rest.RMeSException;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

	private final static Logger logger = LogManager.getLogger(QuestionnaireServiceImpl.class);

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	MetadataService metadataService;

	@Autowired
	SearchService searchService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	XpathProcessor xpathProcessor;

	@Override
	public String getQuestionnaire(String id, String ressourcePackageId) throws Exception {
		String fragmentStudyUnit = "", docWithRessourcePackage = "";
		StringBuilder resRootFragment = new StringBuilder();
		TreeMap<Integer, Map<Node, String>> mapStudyUnit = new TreeMap<Integer, Map<Node, String>>();
		Map<Node, String> map = new HashMap<Node, String>();
		try {
			Node node = getStudyUnitNode(id);
			addStudyUnit(map, node, mapStudyUnit);
			addDataCollection(map, node, mapStudyUnit);
			docWithRessourcePackage = metadataService.getRessourcePackageWithEnvelopeAndCustomItems(ressourcePackageId,
					ressourcePackageId, Envelope.DEFAULT, mapStudyUnit);
			resRootFragment.append(docWithRessourcePackage);
		} catch (Exception e) {
			throw new RMeSException(404, "This item is not in the Colectica database.", fragmentStudyUnit);
		}
		return resRootFragment.toString();
	}
	
	public Node getStudyUnitNode(String id) throws Exception
	{
		String fragmentStudyUnit = metadataServiceItem.getItem(id).item;
		NodeList nodelistStudyUnit = xpathProcessor.queryList(fragmentStudyUnit,
				"//*[local-name()='Fragment']/*[local-name()='StudyUnit']");
		Node node = nodelistStudyUnit.item(0);
		return node;
	}
	
	public void addStudyUnit(Map<Node, String> map, Node node, TreeMap<Integer, Map<Node, String>> mapStudyUnit)
	{
		map.put(node, "DDIInstance");
		mapStudyUnit.put(1, map);
	}
	
	public void addDataCollection(Map<Node, String> map, Node node, TreeMap<Integer, Map<Node, String>> mapStudyUnit) throws Exception
	{
		String idDataCollection = xpathProcessor.queryString(node,
				"//*[local-name()='Fragment']/*[local-name()='StudyUnit']/*[local-name()='DataCollectionReference']/*[local-name()='ID']/text()");
		
		String fragmentDataCollection = metadataServiceItem.getItem(idDataCollection).item;
		NodeList nodelistDataCollection = xpathProcessor.queryList(fragmentDataCollection,
				"//*[local-name()='Fragment']/*[local-name()='DataCollection']");
		node = nodelistDataCollection.item(0);
		map = new HashMap<>();
		map.put(node, "StudyUnit");
		mapStudyUnit.put(2, map);
	}

}
