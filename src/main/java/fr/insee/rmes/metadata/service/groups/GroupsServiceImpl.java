package fr.insee.rmes.metadata.service.groups;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.Envelope;

@Service
public class GroupsServiceImpl implements GroupsService {

	@Autowired
	private MetadataServiceItem metadataServiceItem;

	@Override
	public String getGroup(String idTopLevel) throws Exception {

		// Step 1 : get the group
		ColecticaItem group = metadataServiceItem.getItemByType(idTopLevel, DDIItemType.GROUP);

		// Step 2 : create a DDIDocumentBuilder
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder(true, Envelope.FRAGMENT_INSTANCE);

		// Step 3 : replace attributes for the TopLevelReference
		replaceValueEnvelope(docBuilder, "r:Agency", group.agencyId);
		replaceValueEnvelope(docBuilder, "r:ID", group.identifier);
		replaceValueEnvelope(docBuilder, "r:Version", group.version);

		// Step 4 : add the group and all of its children
		ColecticaItemRefList refs = metadataServiceItem.getChildrenRef(idTopLevel);

		List<ColecticaItem> items = metadataServiceItem.getItems(refs);
		for (ColecticaItem item : items) {
			Node itemNode = getNode(item.item, docBuilder.getDocument());
			docBuilder.appendChild(itemNode);
		}
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

	/**
	 * Replace a specific value of a node thanks to its tagName
	 * 
	 * @param docBuilder
	 *            : Document to edit
	 * @param targetTagName
	 *            : tagName
	 * @param newValue
	 *            : new value applied to the current node.
	 */
	private void replaceValueEnvelope(DDIDocumentBuilder docBuilder, String targetTagName, String newValue) {
		NodeList nodes = docBuilder.getDocument().getElementsByTagName(targetTagName);
		Node node = nodes.item(0);
		node.setTextContent(newValue);
	}

}
