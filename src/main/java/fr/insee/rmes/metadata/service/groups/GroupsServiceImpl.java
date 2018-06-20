package fr.insee.rmes.metadata.service.groups;

import java.io.StringReader;
import java.util.ArrayList;
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
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.TargetItem;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.Envelope;

@Service
public class GroupsServiceImpl implements GroupsService {

	@Autowired
	private MetadataServiceItem metadataServiceItem;

	@Autowired
	private MetadataService metadataService;

	private List<Node> groups;

	@Override
	public String getGroups(String idTopLevel) throws Exception {

		// Step 1 : get the DDIInstance that contains all the groups
		ColecticaItem ddiInstance = metadataServiceItem.getItem(idTopLevel);

		// Step 2 : get all the groups
		Relationship[] relationshipsGroups = getRelationchipsChidren(ddiInstance, DDIItemType.GROUP);
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder(true, Envelope.FRAGMENT_INSTANCE);

		
		//Step 3 : replace attributes for the TopLevelReference
		replaceValueEnvelope(docBuilder, "r:Agency", ddiInstance.agencyId);
		replaceValueEnvelope(docBuilder, "r:ID", ddiInstance.identifier);
		replaceValueEnvelope(docBuilder, "r:Version", ddiInstance.version);
		
		// Step 4 : add all the groups found to the fragmentInstance
		for (Relationship relationship : relationshipsGroups) {
			ColecticaItem group = metadataServiceItem.getItem(relationship.getIdentifierTriple().getIdentifier());
			Node groupNode = getNode(group.item, docBuilder.getDocument());
			docBuilder.appendChild(groupNode);
		}

		return docBuilder.toString();
	}

	/**
	 * Get all the relationships of a DDIInstance concerning a specific type of item
	 * @param ddiInstance : DDIInstance
	 * @param ddiItemType : type of the item type
	 * @return tab of relationships
	 * @throws Exception
	 */
	public Relationship[] getRelationchipsChidren(ColecticaItem ddiInstance, DDIItemType ddiItemType) throws Exception {
		ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
		List<String> itemTypes = new ArrayList<String>();
		itemTypes.add(ddiItemType.getUUID());
		objectColecticaPost.setItemTypes(itemTypes);
		TargetItem targetItem = new TargetItem();
		targetItem.setAgencyId(ddiInstance.agencyId);
		targetItem.setIdentifier(ddiInstance.identifier);
		targetItem.setVersion(Integer.valueOf(ddiInstance.version));
		objectColecticaPost.setTargetItem(targetItem);
		objectColecticaPost.setUseDistinctResultItem(true);
		objectColecticaPost.setUseDistinctTargetItem(true);
		Relationship[] relationshipsChildren = metadataService.getRelationshipChildren(objectColecticaPost);
		return relationshipsChildren;
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
	 * @param docBuilder  : Document to edit
	 * @param targetTagName : tagName
	 * @param newValue : new value applied to the current node.
	 */
	private void replaceValueEnvelope(DDIDocumentBuilder docBuilder, String targetTagName, String newValue) {
		NodeList nodes = docBuilder.getDocument().getElementsByTagName(targetTagName);
		Node node = nodes.item(0);
		node.setTextContent(newValue);
	}

}
