package fr.insee.rmes.metadata.service.ddiinstance;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.TargetItem;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.DocumentBuilderUtils;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.UtilXML;

@Service
public class DDIInstanceServiceImpl implements DDIInstanceService {
	private final static Logger logger = LogManager.getLogger(DDIInstanceServiceImpl.class);

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	MetadataService metadataService;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	XpathProcessor xpathProcessor;

	@Override
	public String getDDIInstance(String id) throws Exception {
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder();
		// Step 1 : Get the ColecticaItem and Check if it's a DDI instance (an
		// Exception throws if not)
		ColecticaItem ddiInstance = metadataServiceItem.getDDIInstance(id);

		// Step 2 : Get the ColecticaItem groups /subGroups / StuDyUnits
		List<ColecticaItem> groups = searchItemsChildrenByType(DDIItemType.GROUP, ddiInstance);
		List<Node> groupNodes = new ArrayList<Node>();
		for (ColecticaItem group : groups) {
			Node groupNode = DocumentBuilderUtils.getNode(itemToString(group),docBuilder.getDocument());
			List<ColecticaItem> subgroups = searchItemsChildrenByType(DDIItemType.SUB_GROUP, group);
			NodeList groupChildrenNodes = groupNode.getChildNodes();

			for (ColecticaItem subgroup : subgroups) {
				Node subGroupNode = subGroupItemToNode(docBuilder, subgroup);
				for (int i = 0; i < groupChildrenNodes.getLength(); i++) {
					Node node = groupChildrenNodes.item(i);
					if (node.getNodeName().contains("SubGroupReference")) {
						groupNode.appendChild(subGroupNode);
						groupNode.removeChild(node);
					}
				}
				groupNodes.add(groupNode);
			}
		}

		// Get ResourcesPackages
		List<ColecticaItem> RPitems = searchItemsChildrenByType(DDIItemType.RESSOURCEPACKAGE, ddiInstance);
		List<Node> rpNodes = new ArrayList<Node>();
		for (ColecticaItem rpItem : RPitems) {
			String rpString = xpathProcessor.queryString(rpItem.getItem(), "/*");
			List<Node> rpSchemes = getReferencesInListOfNodes(rpString, docBuilder);
			Node rpItemNode = getNodeByXpath(docBuilder, rpItem, "/Fragment[1]/*");
			removeReferences(rpItemNode);
			for (Node node : rpSchemes) {
				rpItemNode.appendChild(node);
			}
			rpNodes.add(rpItemNode);
		}

		// Step : Get DDI Instance informations on root : r:URN, r:Agency, r:ID,
		// r:Version, r:UserID, r:Citation
		addDDIInstanceInformationToDocBuilder(ddiInstance,docBuilder);
		for (Node node : groupNodes) {
			docBuilder.appendChild(node);
		}

		for (Node node : rpNodes) {
			docBuilder.appendChild(node);
		}

		return docBuilder.toString();

	}

	private Node subGroupItemToNode(DDIDocumentBuilder docBuilder, ColecticaItem subgroup) throws Exception {
		Node subGroupNode = DocumentBuilderUtils.getNode(itemToString(subgroup),	docBuilder.getDocument());
		NodeList subGroupChildrenNodes = subGroupNode.getChildNodes();
		for (int i = 0; i < subGroupChildrenNodes.getLength(); i++) {
			Node node = subGroupChildrenNodes.item(i);
			if (node.getNodeName().contains("StudyUnitReference")) {
				List<ColecticaItem> studyUnits = searchItemsChildrenByType(DDIItemType.STUDY_UNIT, subgroup);
				for (ColecticaItem studyUnitItem : studyUnits) {
					Node studyUnitNode = studyUnitItemToNode(docBuilder, studyUnitItem);
					subGroupNode.removeChild(node);
					subGroupNode.appendChild(studyUnitNode);
				}
			}
			if (node.getNodeName().contains("PhysicalDataProductReference")) {
				List<ColecticaItem> physicalDataProducts = searchItemsChildrenByType(
						DDIItemType.PHYSICAL_DATA_PRODUCT, subgroup);
				for (ColecticaItem physicalDataProductItem : physicalDataProducts) {
					Node physicalDataProductNode = DocumentBuilderUtils.getNode(itemToString(physicalDataProductItem),docBuilder);
					subGroupNode.removeChild(node);
					subGroupNode.appendChild(physicalDataProductNode);
				}
			}
		}
		return subGroupNode;
	}

	private Node studyUnitItemToNode(DDIDocumentBuilder docBuilder, ColecticaItem studyUnitItem) throws Exception {
		Node studyUnitNode = DocumentBuilderUtils.getNode(itemToString(studyUnitItem),docBuilder);
		List<ColecticaItem> dataCollections = searchItemsChildrenByType(DDIItemType.DATA_COLLECTION,
				studyUnitItem);
		for (ColecticaItem dcItem : dataCollections) {
			Node dataCollectionNode = DocumentBuilderUtils.getNode(itemToString(dcItem),docBuilder);
			removeReferences(studyUnitNode);
			studyUnitNode.appendChild(dataCollectionNode);
		}
		return studyUnitNode;
	}

	private String itemToString(ColecticaItem colecticaItem) throws Exception {
		return UtilXML.nodeToString(xpathProcessor.queryList(colecticaItem.getItem(), "/Fragment[1]/*").item(0));
	}

	private Node getNodeByXpath(DDIDocumentBuilder docBuilder, ColecticaItem ddiInstance, String xpathExpression)
			throws Exception {
		String fragment = xpathProcessor.queryString(ddiInstance.getItem(), xpathExpression);
		Node node = DocumentBuilderUtils.getNode(fragment.trim(), docBuilder);
		return node;
	}

	/**
	 * Get the references in a fragment and return them as a List of Node 
	 * 
	 * @param fragment
	 *            : String of the rootNode
	 * @param doc
	 *            : DDIDocumentBuilder
	 * @return List<Node> : nodes of the schemes
	 * @throws Exception
	 */
	private List<Node> getReferencesInListOfNodes(String fragment, DDIDocumentBuilder doc) throws Exception {
		List<Node> referencesNodes = new ArrayList<Node>();
		List<NodeList> fragmentsSchemes = new ArrayList<NodeList>();
		NodeList fragmentNodes = xpathProcessor.queryList(fragment, "/Fragment[1]/*");
		Node fragmentNode = null;
		for (int n = 0; n < fragmentNodes.getLength(); n++) {
			fragmentNode = fragmentNodes.item(n);
			if (fragmentNode.getNodeName().contains("ResourcePackage")) {
				NodeList resourcePackageChildren = fragmentNode.getChildNodes();
				for (int i = 0; i < resourcePackageChildren.getLength(); i++) {
					Node childNode = resourcePackageChildren.item(i);
					if (childNode.getNodeName().endsWith("Reference")) {
						NodeList childRefNodes = childNode.getChildNodes();
						for (int j = 0; j < childRefNodes.getLength(); j++) {
							Node childRefNode = childRefNodes.item(j);
							if (childRefNode.getNodeName().contains("r:ID")) {
								ColecticaItem item = metadataServiceItem
										.getItem(childRefNode.getChildNodes().item(0).getNodeValue());
								fragmentsSchemes.add(xpathProcessor.queryList(item.getItem(), "/Fragment[1]/*"));

							}
						}
					}
				}
			}

		}

		for (NodeList nodeListScheme : fragmentsSchemes) {
			for (int j = 0; j < nodeListScheme.getLength(); j++) {
				Node newNode = nodeListScheme.item(j).cloneNode(true);
				doc.getDocument().adoptNode(newNode);
				referencesNodes.add(newNode);
			}
		}
		return referencesNodes;
	}

	/**
	 * Remove the references of the Resource Package Node
	 * 
	 * @param rpNode
	 *            : node related to <ResourcePackage></ResourcePackage>
	 */
	private void removeReferences(Node rpNode) {
		NodeList listRP = rpNode.getChildNodes();
		for (int j = 0; j < listRP.getLength(); j++) {
			Node nodeREF = listRP.item(j);
			if (nodeREF.getNodeName().endsWith("Reference")) {
				rpNode.removeChild(nodeREF);
			}
		}
	}


	/**
	 * Search the children of a specific DDI TYpe for a DDI object
	 * 
	 * @param ddiItemType
	 *            : <tt>DDIItemType<tt>
	 * @param itemChild
	 *            : <tt>ColecticaItem<tt>
	 * @return
	 * @throws Exception
	 */
	private List<ColecticaItem> searchItemsChildrenByType(DDIItemType ddiItemType, ColecticaItem itemChild)
			throws Exception {
		ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
		List<String> itemTypes = new ArrayList<String>();
		List<ColecticaItem> items = new ArrayList<ColecticaItem>();
		itemTypes.add(ddiItemType.getUUID());
		objectColecticaPost.setItemTypes(itemTypes);
		TargetItem targetItem = new TargetItem();
		targetItem.setAgencyId(itemChild.agencyId);
		targetItem.setIdentifier(itemChild.identifier);
		targetItem.setVersion(Integer.valueOf(itemChild.version));
		objectColecticaPost.setTargetItem(targetItem);
		objectColecticaPost.setUseDistinctResultItem(true);
		objectColecticaPost.setUseDistinctTargetItem(true);
		Relationship[] relationships = metadataService.getRelationshipChildren(objectColecticaPost);
		for (Relationship relationship : relationships) {
			String DDIidentifier = relationship.getIdentifierTriple().getIdentifier();
			ColecticaItem item = metadataServiceItem.getItem(DDIidentifier);
			items.add(item);
		}
		return items;
	}

	/**
	 * Get DDI Instance Informations on root and append to docBuilder
	 *  r:URN, r:Agency, r:ID, r:Version, r:UserID, r:Citation
	 * @param docBuilder
	 * @param ddiInstance
	 * @throws Exception
	 */
	@Override
	public void addDDIInstanceInformationToDocBuilder(ColecticaItem ddiInstance, DDIDocumentBuilder docBuilder) throws Exception {
		Node urnNode = getNodeByXpath(docBuilder, ddiInstance, "/Fragment[1]/DDIInstance[1]/URN[1]");
		Node agencyNode = getNodeByXpath(docBuilder, ddiInstance, "/Fragment[1]/DDIInstance[1]/Agency[1]");
		Node idNode = getNodeByXpath(docBuilder, ddiInstance, "/Fragment[1]/DDIInstance[1]/ID[1]");
		Node versionNode = getNodeByXpath(docBuilder,ddiInstance,"/Fragment[1]/DDIInstance[1]/Version[1]");
		Node userIDNode = getNodeByXpath(docBuilder, ddiInstance,"/Fragment[1]/DDIInstance[1]/UserID[1]");
		Node citationNode = getNodeByXpath(docBuilder, ddiInstance, "/Fragment[1]/DDIInstance[1]/Citation[1]");

		// add Child on root
		docBuilder.appendChild(urnNode);
		docBuilder.appendChild(agencyNode);
		docBuilder.appendChild(idNode);
		docBuilder.appendChild(versionNode);
		docBuilder.appendChild(userIDNode);
		docBuilder.appendChild(citationNode);
		
	}

}
