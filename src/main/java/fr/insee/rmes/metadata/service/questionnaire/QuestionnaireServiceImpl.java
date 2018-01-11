package fr.insee.rmes.metadata.service.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.codeList.CodeListServiceImpl;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.service.SearchService;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
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
	public String getQuestionnaire(String id, String ressourcePackageId, String dataCollectionId, String subGroupId,
			String groupId) throws Exception {
		String fragmentStudyUnit = "", docWithRessourcePackage = "";
		StringBuilder resRootFragment = new StringBuilder();
		Map<String, String> fragments = new TreeMap<String, String>();
		TreeMap<Integer, Map<Node, String>> nodesWithParentNames = null;
		try {
			ColecticaItemRefList idChildren = metadataRepository.getChildrenRef(id);
			List<ColecticaItem> idItems = metadataServiceItem.getItems(idChildren);
			List<ColecticaItem> allItems = new ArrayList<ColecticaItem>();
			allItems.addAll(idItems);
			for (ColecticaItem colecticaItem : allItems) {
				fragments.put(colecticaItem.identifier, "");
			}
			TreeMap<Integer, Map<Node, String>> mapInstrument = new TreeMap<Integer, Map<Node, String>>();
			Map<Node, String> map = new HashMap<Node, String>();
			Node node = getFragmentNode(metadataRepository.findById(id).item);
			map.put(node, "d:InstrumentScheme");
			mapInstrument.put(1, map);
			TreeMap<Integer, Map<Node, String>> mapInterviewerInstructionScheme = ProcessingInterviewerInstructionScheme(
					allItems, fragments, nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> controlConstructScheme = ProcessingControlConstructScheme(allItems,
					fragments, nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> questionScheme = ProcessingQuestionScheme(allItems, fragments,
					nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> categoryScheme = ProcessingCategoryScheme(allItems, fragments, 2,
					nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> codeListScheme = ProcessingCodeListScheme(allItems, fragments,
					nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> variableScheme = ProcessingVariableScheme(allItems, fragments,
					nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> instructionScheme = ProcessingInstructionScheme(allItems, fragments,
					nodesWithParentNames);
			TreeMap<Integer, Map<Node, String>> managedRepresentationScheme = ProcessingManagedRepresentationScheme(
					allItems, fragments, nodesWithParentNames);
			docWithRessourcePackage = new DDIDocumentBuilder(true, Envelope.INSTRUMENTSCHEME).build()
					.buildWithCustomNodes(mapInstrument).buildWithCustomNodes(mapInterviewerInstructionScheme)
					.buildWithCustomNodes(controlConstructScheme).buildWithCustomNodes(questionScheme)
					.buildWithCustomNodes(instructionScheme).buildWithCustomNodes(categoryScheme)
					.buildWithCustomNodes(codeListScheme).buildWithCustomNodes(variableScheme)
					.buildWithCustomNodes(managedRepresentationScheme).toString();
			resRootFragment.append(docWithRessourcePackage);
		} catch (Exception e) {
			throw new RMeSException(404, "This item is not in the Colectica database.", fragmentStudyUnit);
		}
		return resRootFragment.toString();
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingInterviewerInstructionScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("Instruction");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "d:InterviewerInstructionScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingControlConstructScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("Sequence");
		list.add("IfThenElse");
		list.add("QuestionContruct");
		list.add("ComputationItem");
		list.add("StatementItem");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "d:ControlConstructScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingQuestionScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("QuestionItem");
		list.add("QuestionGrid");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "d:QuestionScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingCategoryScheme(List<ColecticaItem> items,
			Map<String, String> fragments, int numberOfScheme, TreeMap<Integer, Map<Node, String>> nodesWithParentNames)
			throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("Category");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "l:CategoryScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingCodeListScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("CodeList");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "l:CodeListScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingVariableScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("Variable");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "l:VariableScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingInstructionScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("GenerationInstruction");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "d:ProcessingInstructionScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingManagedRepresentationScheme(List<ColecticaItem> items,
			Map<String, String> fragments, TreeMap<Integer, Map<Node, String>> nodesWithParentNames) throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("ManagedDateTimeRepresentation");
		return ProcessingNodes(items, fragments, nodesWithParentNames, "r:ManagedRepresentationScheme", list);
	}

	public TreeMap<Integer, Map<Node, String>> ProcessingNodes(List<ColecticaItem> items, Map<String, String> fragments,
			TreeMap<Integer, Map<Node, String>> nodesWithParentNames, String parentName, List<String> itemNames)
			throws Exception {
		nodesWithParentNames = new TreeMap<Integer, Map<Node, String>>();
		Node node;
		Map<Node, String> map = new HashMap<Node, String>();
		String res = "";
		int index = 1;
		List<String> idItemsAlreadyGot = new ArrayList<String>();

		for (ColecticaItem colecticaItem : items) {
			node = getFragmentNode(colecticaItem.item);
			for (String name : itemNames) {
				res = xpathProcessor.queryText(node, "//*[local-name()='" + name + "']/*[local-name()='ID']/text()");
				if (!(res.equals(""))) {
					if (!(idItemsAlreadyGot.contains(colecticaItem.identifier))) {
						map = new HashMap<Node, String>();
						fragments.put(colecticaItem.identifier, colecticaItem.item);
						map.put(node, parentName);
						nodesWithParentNames.put(index, map);
						index++;
						idItemsAlreadyGot.add(colecticaItem.identifier);
					}
				}
			}
		}
		return nodesWithParentNames;
	}

	public Node getInstrumentNode(String id) throws Exception {
		String fragmentStudyUnit = metadataServiceItem.getItem(id).item;
		NodeList nodelistStudyUnit = xpathProcessor.queryList(fragmentStudyUnit,
				"//*[local-name()='Fragment']/*[local-name()='Instrument']");
		Node node = nodelistStudyUnit.item(0);
		return node;
	}

	public Node getFragmentNodeById(String id) throws Exception {
		String fragment = metadataServiceItem.getItem(id).item;
		NodeList nodelist = xpathProcessor.queryList(fragment, "//*[local-name()='Fragment']//*");
		Node node = nodelist.item(0);
		return node;
	}

	public Node getFragmentNode(String fragment) throws Exception {
		NodeList nodelist = xpathProcessor.queryList(fragment, "//*[local-name()='Fragment']//*");
		Node node = nodelist.item(0);
		return node;
	}

	public void addInstrument(Map<Node, String> map, Node node, TreeMap<Integer, Map<Node, String>> mapStudyUnit) {
		map.put(node, "DDIInstance");
		mapStudyUnit.put(1, map);
	}

}
