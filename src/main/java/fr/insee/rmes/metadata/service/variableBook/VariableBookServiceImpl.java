package fr.insee.rmes.metadata.service.variableBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.DocumentBuilderUtils;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.UtilXML;

@Service
public class VariableBookServiceImpl implements VariableBookService {

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	MetadataService metadataService;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	XpathProcessor xpathProcessor;

	@Override
	public String getVariableBook(String idStudyUnit) throws Exception {
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder();

		// Step 1 : Get the ColecticaItem and Check if it's a StudyUnit
		ColecticaItem studyUnit = metadataServiceItem.getStudyUnit(idStudyUnit);
		Node studyUnitNode = DocumentBuilderUtils.getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(studyUnit.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder);

		// Step 2 : Get the LogicalProduct and RepresentedVariableScheme
		String idLogicalProduct = xpathProcessor.queryString(studyUnit.getItem(),
				"/Fragment[1]/StudyUnit[1]/LogicalProductReference[1]/ID[1]/text()");
		ColecticaItem logicalProductItem = metadataServiceItem.getItem(idLogicalProduct);
		String idRepresentedVariableScheme = xpathProcessor.queryString(logicalProductItem.getItem(),
				"/Fragment[1]/LogicalProduct[1]/RepresentedVariableSchemeReference[1]/ID[1]/text()");
		ColecticaItem representedVariableSchemeItem = metadataServiceItem.getItem(idRepresentedVariableScheme);

		// Step 3 : Get the Variables Groups and all their contents
		NodeList representedVariableGroupNodes = xpathProcessor.queryList(representedVariableSchemeItem.getItem(),
				"/Fragment[1]/RepresentedVariableScheme[1]/RepresentedVariableGroupReference/ID[1]");
		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<String> subgroupsId = new ArrayList<String>();

		for (int i = 0; i < representedVariableGroupNodes.getLength(); i++) {
			String idGroup = representedVariableGroupNodes.item(i).getTextContent();
			if (!subgroupsId.contains(idGroup)) {// if not already a subgroup
				Node varGroup = getCompleteNode(docBuilder, idGroup);
				groupMap.put(idGroup, varGroup);
				completeSubGroupIdsList(subgroupsId, varGroup);
			}
		}

		for (Map.Entry<String, Node> entry : groupMap.entrySet()) {
			if (!subgroupsId.contains(entry.getKey())) {
				studyUnitNode.appendChild(entry.getValue());
			}
		}

		// Step 4 : Build the document
		docBuilder.appendChild(studyUnitNode);
		return docBuilder.toString();

	}

	private void completeSubGroupIdsList(List<String> subgroupsId, Node varGroup) throws Exception {
		// check if varGroup has subgroups
		NodeList groupChildren = varGroup.getChildNodes();
		for (int j = 0; j < groupChildren.getLength(); j++) {
			Node node = groupChildren.item(j);
			if (node.getNodeName().equals("RepresentedVariableGroup")) {
				subgroupsId.add(getId(node));
			}
		}
	}

	private Node getCompleteNode(DDIDocumentBuilder docBuilder, String idNodeToExtract) throws Exception {
		String fragment = xpathProcessor.queryString(metadataService.getDerefDDIDocument(idNodeToExtract),
				"/DDIInstance[1]/*");
		return DocumentBuilderUtils.getNode(fragment, docBuilder);
	}

	private static String getId(Node refNode) throws Exception {
		NodeList refChildren = refNode.getChildNodes();
		for (int i = 0; i < refChildren.getLength(); i++) {
			if (refChildren.item(i).getNodeName().equals("r:ID")) {
				return refChildren.item(i).getTextContent();
			}
		}
		throw new Exception("No reference found in node");
	}

}
