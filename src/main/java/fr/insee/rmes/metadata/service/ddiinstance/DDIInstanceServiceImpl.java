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
		
		
		// Step 1 : Get the ColecticaItem and Check if it's a DDI instance (an Exception throws if not)
		ColecticaItem ddiInstance = metadataServiceItem.getDDIInstance(id); 
		// Step 2 : Get the ColecticaItem group 
		String idGroup = xpathProcessor.queryString(ddiInstance.getItem(), "/Fragment[1]/DDIInstance[1]/GroupReference[1]/ID[1]/text()");
		ColecticaItem groupItem = metadataServiceItem.getItem(idGroup); 
		// Step 3 : Get the ColecticaItem sub-group
		String idSubGroup = xpathProcessor.queryString(groupItem.getItem(), "/Fragment[1]/Group[1]/SubGroupReference[1]/ID[1]/text()");
		ColecticaItem subGroupItem = metadataServiceItem.getItem(idSubGroup); 
		// Step 4 : Get the ColecticaItem Study-unit
		String idStudyUnit = xpathProcessor.queryString(subGroupItem.getItem(), "/Fragment[1]/SubGroup[1]/StudyUnitReference[1]/ID[1]/text()");
		ColecticaItem studyUnitItem = metadataServiceItem.getItem(idStudyUnit); 
		// Step : Build the group, from the studyUnit to the group
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder();
		
		Node subGroupNode = getNode(UtilXML.nodeToString(xpathProcessor.queryList(subGroupItem.getItem(),"/Fragment[1]/*").item(0)),docBuilder.getDocument());
		NodeList subGroupChildrenNodes = subGroupNode.getChildNodes();
		for (int i = 0; i < subGroupChildrenNodes.getLength(); i++) {
			Node node = subGroupChildrenNodes.item(i);
			if (node.getNodeName().contains("StudyUnitReference")) {
				Node studyUnitNode = getNode(UtilXML.nodeToString(xpathProcessor.queryList(studyUnitItem.getItem(),"/Fragment[1]/*").item(0)),docBuilder.getDocument());
				subGroupNode.removeChild(node);
				subGroupNode.appendChild(studyUnitNode);
			}
		}
		subGroupNode = getNode(UtilXML.nodeToString(subGroupNode),docBuilder.getDocument());
		Node groupNode = getNode(UtilXML.nodeToString(xpathProcessor.queryList(groupItem.getItem(),"/Fragment[1]/*").item(0)),docBuilder.getDocument());
		NodeList groupChildrenNodes = groupNode.getChildNodes();
		for (int i = 0; i < groupChildrenNodes.getLength(); i++) {
			Node node = groupChildrenNodes.item(i);
			if (node.getNodeName().contains("SubGroupReference")) {
				groupNode.appendChild(subGroupNode);
				groupNode.removeChild(node);
			}
		}
		
		// Step  : Get the first Resource package
		String idRP = xpathProcessor.queryString(ddiInstance.getItem(), "/Fragment[1]/DDIInstance[1]/ResourcePackageReference[1]/ID[1]/text()");
		String rpString = xpathProcessor.queryString(metadataService.getDerefDDIDocument(idRP),"/DDIInstance[1]/*");
		Node RP1 = getNode(rpString,docBuilder.getDocument());
		// Step  : Get the second Resource package
		String idRP2 = xpathProcessor.queryString(ddiInstance.getItem(), "/Fragment[1]/DDIInstance[1]/ResourcePackageReference[2]/ID[1]/text()");
		String rpString2 = xpathProcessor.queryString(metadataService.getDerefDDIDocument(idRP2),"/DDIInstance[1]/*");
		Node RP2 = getNode(rpString2,docBuilder.getDocument());
		
		docBuilder.appendChild(groupNode);
		docBuilder.appendChild(RP1);
		docBuilder.appendChild(RP2);
		
		return docBuilder.toString();
		
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
