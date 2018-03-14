package fr.insee.rmes.metadata.service.variableBook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.UtilXML;

@Service
public class VariableBookServiceImpl implements VariableBookService {
	// private final static Logger logger =
	// LogManager.getLogger(DDIInstanceServiceImpl.class);

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
		Node studyUnitNode = docBuilder.getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(studyUnit.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

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

		for (int i = 0; i < representedVariableGroupNodes.getLength(); i++) {
			Node rVarGroupNode = representedVariableGroupNodes.item(i);
			Node varGroup = getCompleteNode(docBuilder, rVarGroupNode);
			studyUnitNode.appendChild(varGroup);
		}
		docBuilder.appendChild(studyUnitNode);
		return docBuilder.toString();

	}

	private Node getCompleteNode(DDIDocumentBuilder docBuilder, Node rVarGroupNode) throws Exception {
		String idVarGroup = rVarGroupNode.getTextContent();
		String idString = xpathProcessor.queryString(metadataService.getDerefDDIDocument(idVarGroup),
				"/DDIInstance[1]/*");
		Node varGroup = docBuilder.getNode(idString, docBuilder.getDocument());
		return varGroup;
	}

}
