package fr.insee.rmes.metadata.service.ddiinstance;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.TargetItem;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
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
			Node groupNode = null;
			List<ColecticaItem> subgroups = searchItemsChildrenByType(DDIItemType.SUB_GROUP, group);
			for (ColecticaItem subgroup : subgroups) {
				Node subGroupNode = getNode(
						UtilXML.nodeToString(xpathProcessor.queryList(subgroup.getItem(), "/Fragment[1]/*").item(0)),
						docBuilder.getDocument());
				NodeList subGroupChildrenNodes = subGroupNode.getChildNodes();
				for (int i = 0; i < subGroupChildrenNodes.getLength(); i++) {
					Node node = subGroupChildrenNodes.item(i);
					if (node.getNodeName().contains("StudyUnitReference")) {
						List<ColecticaItem> studyUnits = searchItemsChildrenByType(DDIItemType.STUDY_UNIT, subgroup);
						for (ColecticaItem studyUnitItem : studyUnits) {
							Node studyUnitNode = getNode(
									UtilXML.nodeToString(xpathProcessor
											.queryList(studyUnitItem.getItem(), "/Fragment[1]/*").item(0)),
									docBuilder.getDocument());
							List<ColecticaItem> dataCollections = searchItemsChildrenByType(DDIItemType.DATA_COLLECTION,
									studyUnitItem);
							for (ColecticaItem dcItem : dataCollections) {
								Node DCNode = getNode(
										UtilXML.nodeToString(
												xpathProcessor.queryList(dcItem.getItem(), "/Fragment[1]/*").item(0)),
										docBuilder.getDocument());

								removeReferences(studyUnitNode);
								studyUnitNode.appendChild(DCNode);

							}

							subGroupNode.removeChild(node);
							subGroupNode.appendChild(studyUnitNode);
						}
					}
					if (node.getNodeName().contains("PhysicalDataProductReference")) {
						List<ColecticaItem> physicalDataProducts = searchItemsChildrenByType(
								DDIItemType.PHYSICAL_DATA_PRODUCT, subgroup);
						for (ColecticaItem physicalDataProductItem : physicalDataProducts) {
							Node physicalDataProductNode = getNode(
									UtilXML.nodeToString(xpathProcessor
											.queryList(physicalDataProductItem.getItem(), "/Fragment[1]/*").item(0)),
									docBuilder.getDocument());
							subGroupNode.removeChild(node);
							subGroupNode.appendChild(physicalDataProductNode);
						}

					}
				}
				subGroupNode = getNode(UtilXML.nodeToString(subGroupNode), docBuilder.getDocument());
				groupNode = getNode(
						UtilXML.nodeToString(xpathProcessor.queryList(group.getItem(), "/Fragment[1]/*").item(0)),
						docBuilder.getDocument());
				NodeList groupChildrenNodes = groupNode.getChildNodes();
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
		List<Node> RPNodes = new ArrayList<Node>();
		for (ColecticaItem rpItem : RPitems) {
			String rpString = xpathProcessor.queryString(rpItem.getItem(), "/*");
			List<Node> rpSchemes = patchReferencesItems(rpString, docBuilder);
			rpString = xpathProcessor.queryString(rpItem.getItem(), "/Fragment[1]/*");
			Node rpItemNode = getNode(rpString, docBuilder.getDocument());
			removeReferences(rpItemNode);
			for (Node node : rpSchemes) {
				rpItemNode.appendChild(node);
			}
			RPNodes.add(rpItemNode);
		}

		// Step : Get DDI Instance informations on root : r:URN, r:Agency, r:ID,
		// r:Version, r:UserID, r:Citation
		String urnString = xpathProcessor.queryString(ddiInstance.getItem(), "/Fragment[1]/DDIInstance[1]/URN[1]");
		Node urnNode = getNode(urnString.trim(), docBuilder.getDocument());
		String agencyString = xpathProcessor.queryString(ddiInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/Agency[1]");
		Node agencyNode = getNode(agencyString, docBuilder.getDocument());
		String idString = xpathProcessor.queryString(ddiInstance.getItem(), "/Fragment[1]/DDIInstance[1]/ID[1]");
		Node idNode = getNode(idString, docBuilder.getDocument());
		String versionString = xpathProcessor.queryString(ddiInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/Version[1]");
		Node versionNode = getNode(versionString, docBuilder.getDocument());
		String userIDString = xpathProcessor.queryString(ddiInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/UserID[1]");
		Node userIDNode = getNode(userIDString, docBuilder.getDocument());
		String citationString = xpathProcessor.queryString(ddiInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/Citation[1]");
		Node citationNode = getNode(citationString, docBuilder.getDocument());

		// Final step add Child on root
		docBuilder.appendChild(urnNode);
		docBuilder.appendChild(agencyNode);
		docBuilder.appendChild(idNode);
		docBuilder.appendChild(versionNode);
		docBuilder.appendChild(userIDNode);
		docBuilder.appendChild(citationNode);
		for (Node node : groupNodes) {
			docBuilder.appendChild(node);
		}

		for (Node node : RPNodes) {
			docBuilder.appendChild(node);
		}

		return docBuilder.toString();

	}

	/**
	 * Get the refernces and return them as a List<Node> for the final process
	 * 
	 * @param rpNodeStr
	 *            : String of the rootNode
	 * @param doc
	 *            : DDIDocumentBuilder
	 * @return List<Node> : nodes of the schemes
	 * @throws Exception
	 */
	private List<Node> patchReferencesItems(String rpNodeStr, DDIDocumentBuilder doc) throws Exception {
		List<Node> nodes = new ArrayList<Node>();
		Node nodeRP = null;
		List<NodeList> fragmentsSchemes = new ArrayList<NodeList>();
		NodeList nodeList = xpathProcessor.queryList(rpNodeStr, "/Fragment[1]/*");
		for (int n = 0; n < nodeList.getLength(); n++) {
			nodeRP = nodeList.item(n);
			if (nodeRP.getNodeName().contains("ResourcePackage")) {
				NodeList listRP = nodeRP.getChildNodes();
				for (int i = 0; i < listRP.getLength(); i++) {
					Node nodeREF = listRP.item(i);
					if (nodeREF.getNodeName().endsWith("Reference")) {
						NodeList childRefNodes = nodeREF.getChildNodes();
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
				nodes.add(newNode);
			}
		}
		return nodes;
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

	private Document getDocument(String fragment) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		if (null == fragment || fragment.isEmpty()) {
			return builder.newDocument();
		}
		InputSource ddiSource = new InputSource(new StringReader(fragment));
		return builder.parse(ddiSource);
	}

	private Node getNode(String fragment, Document doc) throws Exception {
		Element node = getDocument(fragment).getDocumentElement();
		Node newNode = node.cloneNode(true);
		// Transfer ownership of the new node into the destination document
		doc.adoptNode(newNode);
		return newNode;
	}

	/**
	 * Serach the children of a specific DDI TYpe for a DDI object
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

}
