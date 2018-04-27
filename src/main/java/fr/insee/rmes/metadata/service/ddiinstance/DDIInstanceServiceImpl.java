package fr.insee.rmes.metadata.service.ddiinstance;

import java.io.StringReader;
import java.util.ArrayList;
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
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.XpathProcessor;
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

		// Step 1 : Get the ColecticaItem and Check if it's a DDI instance (an
		// Exception throws if not)
		ColecticaItem ddiInstance = metadataServiceItem.getDDIInstance(id);
		// Step 2 : Get the ColecticaItem group
		String idGroup = xpathProcessor.queryString(ddiInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/GroupReference[1]/ID[1]/text()");
		ColecticaItem groupItem = metadataServiceItem.getItem(idGroup);
		// Step 3 : Get the ColecticaItem sub-group
		String idSubGroup = xpathProcessor.queryString(groupItem.getItem(),
				"/Fragment[1]/Group[1]/SubGroupReference[1]/ID[1]/text()");
		ColecticaItem subGroupItem = metadataServiceItem.getItem(idSubGroup);
		// Step 4 : Get the ColecticaItem Study-unit
		String idStudyUnit = xpathProcessor.queryString(subGroupItem.getItem(),
				"/Fragment[1]/SubGroup[1]/StudyUnitReference[1]/ID[1]/text()");
		ColecticaItem studyUnitItem = metadataServiceItem.getItem(idStudyUnit);
		// Step 5 : Get the ColecticaItem PhysicalDataProduct
		String idPhysicalDataProduct = xpathProcessor.queryString(subGroupItem.getItem(),
				"/Fragment[1]/SubGroup[1]/PhysicalDataProductReference[1]/ID[1]/text()");
		ColecticaItem physicalDataProductItem = metadataServiceItem.getItem(idPhysicalDataProduct);
		// Step : Build the group, from the studyUnit to the group
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder();

		Node subGroupNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(subGroupItem.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());
		NodeList subGroupChildrenNodes = subGroupNode.getChildNodes();
		for (int i = 0; i < subGroupChildrenNodes.getLength(); i++) {
			Node node = subGroupChildrenNodes.item(i);
			if (node.getNodeName().contains("StudyUnitReference")) {
				Node studyUnitNode = getNode(
						UtilXML.nodeToString(
								xpathProcessor.queryList(studyUnitItem.getItem(), "/Fragment[1]/*").item(0)),
						docBuilder.getDocument());
				subGroupNode.removeChild(node);
				subGroupNode.appendChild(studyUnitNode);
			}
			if (node.getNodeName().contains("PhysicalDataProductReference")) {
				Node physicalDataProductNode = getNode(
						UtilXML.nodeToString(
								xpathProcessor.queryList(physicalDataProductItem.getItem(), "/Fragment[1]/*").item(0)),
						docBuilder.getDocument());
				subGroupNode.removeChild(node);
				subGroupNode.appendChild(physicalDataProductNode);
			}
		}
		subGroupNode = getNode(UtilXML.nodeToString(subGroupNode), docBuilder.getDocument());
		Node groupNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(groupItem.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());
		NodeList groupChildrenNodes = groupNode.getChildNodes();
		for (int i = 0; i < groupChildrenNodes.getLength(); i++) {
			Node node = groupChildrenNodes.item(i);
			if (node.getNodeName().contains("SubGroupReference")) {
				groupNode.appendChild(subGroupNode);
				groupNode.removeChild(node);
			}
		}

		// Step : Get the first Resource package
		String idRP = xpathProcessor.queryString(ddiInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/ResourcePackageReference[1]/ID[1]/text()");
		String rpString = xpathProcessor.queryString(metadataServiceItem.getItem(idRP).getItem(), "/*");
		List<Node> rp1Schemes = patchReferencesItems(rpString, docBuilder);
		rpString = xpathProcessor.queryString(metadataServiceItem.getItem(idRP).getItem(), "/Fragment[1]/*");
		Node RP1 = getNode(rpString, docBuilder.getDocument());
		removeReferences(RP1);
		// Step : Get the second Resource package (if available)
		List<Node> rp2Schemes = null;
		Node RP2 = null;
		try {
			String idRP2 = xpathProcessor.queryString(ddiInstance.getItem(),
					"/Fragment[1]/DDIInstance[1]/ResourcePackageReference[2]/ID[1]/text()");
			String rpString2 = xpathProcessor.queryString(metadataServiceItem.getItem(idRP2).getItem(), "/*");
			rp2Schemes = patchReferencesItems(rpString2, docBuilder);
			rpString2 = xpathProcessor.queryString(metadataServiceItem.getItem(idRP2).getItem(), "/Fragment[1]/*");
			RP2 = getNode(rpString2, docBuilder.getDocument());
			removeReferences(RP2);

		} catch (Exception e) {
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
		docBuilder.appendChild(groupNode);
		for (Node nodeRP1 : rp1Schemes) {
			RP1.appendChild(nodeRP1);
		}
		docBuilder.appendChild(RP1);
		if (rp2Schemes != null) {
			for (Node nodeRP2 : rp2Schemes) {
				RP2.appendChild(nodeRP2);
			}
		}
		if (RP2 != null) {
			docBuilder.appendChild(RP2);
		}

		return docBuilder.toString();

	}

	/**
	 * Get the refernces and return them as a List<Node> for the final process
	 * @param rpNodeStr : String of the rootNode
	 * @param doc : DDIDocumentBuilder
	 * @return List<Node> nodes of the schemes
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
	 * @param rpNode : node related to <ResourcePackage></ResourcePackage>
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

}
