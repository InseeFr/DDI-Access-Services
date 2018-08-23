package fr.insee.rmes.metadata.service.questionnaire;

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
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.ItemWithParent;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.TargetItem;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import fr.insee.rmes.metadata.utils.DocumentBuilderUtils;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.service.SearchService;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.UtilXML;
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
	DDIInstanceService ddiInstanceService;

	@Autowired
	SearchService searchService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	XpathProcessor xpathProcessor;

	private String idDDIInstrument;

	private ColecticaItem instrument;

	private ColecticaItem DDIInstance;

	private ColecticaItem variableScheme;

	private ColecticaItem subGroupItem;

	private ColecticaItem groupItem;

	private ColecticaItem studyUnitItem;

	private ColecticaItem dataCollection;

	private ColecticaItem instrumentScheme;

	private Node groupNode;

	private Node subGroupNode;

	private Node studyUnitNode;

	private Node dcNode;

	private Node variableSchemeNode;

	private Node instrumentSchemeNode;

	private Node instrumentNode;

	@Override
	public String getQuestionnaire(String idDDIInstrument) throws Exception {
		this.idDDIInstrument = idDDIInstrument;

		// Step 1 : Get the DDIInstance, the DDIInstrument and Check type (an
		// Exception throws if not)

		ColecticaItem DDIInstrument = metadataServiceItem.getItemByType(idDDIInstrument, DDIItemType.QUESTIONNAIRE);
		this.instrument = DDIInstrument;
		ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
		List<String> itemTypes = new ArrayList<String>();
		itemTypes.add(DDIItemType.INSTRUMENT_SCHEME.getUUID());
		objectColecticaPost.setItemTypes(itemTypes);
		TargetItem targetItem = new TargetItem();
		targetItem.setAgencyId(DDIInstrument.agencyId);
		targetItem.setIdentifier(DDIInstrument.identifier);
		targetItem.setVersion(Integer.valueOf(DDIInstrument.version));
		objectColecticaPost.setTargetItem(targetItem);
		objectColecticaPost.setUseDistinctResultItem(true);
		objectColecticaPost.setUseDistinctTargetItem(true);
		Relationship[] relationshipsInstrument = metadataService.getRelationship(objectColecticaPost);
		String DDIidentifier = relationshipsInstrument[0].getIdentifierTriple().getIdentifier();
		instrumentScheme = metadataServiceItem.getItem(DDIidentifier);
		dataCollection = searchItemParent(itemTypes, DDIItemType.DATA_COLLECTION, objectColecticaPost,
				instrumentScheme);
		variableScheme = mockGetVariableScheme(dataCollection);
		studyUnitItem = searchItemParent(itemTypes, DDIItemType.STUDY_UNIT, objectColecticaPost, dataCollection);
		subGroupItem = searchItemParent(itemTypes, DDIItemType.SUB_GROUP, objectColecticaPost, studyUnitItem);
		groupItem = searchItemParent(itemTypes, DDIItemType.GROUP, objectColecticaPost, subGroupItem);
		DDIInstance = searchItemParent(itemTypes, DDIItemType.DDI_INSTANCE, objectColecticaPost, groupItem);
		return buildQuestionnaire();

	}

	private ColecticaItem mockGetVariableScheme(ColecticaItem dataCollection) throws Exception {
		// TODO: remove mock when context will be fixed
		return metadataServiceItem.getItem("10489bc2-11bb-4688-b56f-3886c3f81c58");
	}

	private ColecticaItem searchItemParent(List<String> itemTypes, DDIItemType ddiItemType,
			ObjectColecticaPost objectColecticaPost, ColecticaItem itemChild) throws Exception {
		itemTypes.clear();
		itemTypes.add(ddiItemType.getUUID());
		objectColecticaPost.setItemTypes(itemTypes);
		TargetItem targetItem = new TargetItem();
		targetItem.setAgencyId(itemChild.agencyId);
		targetItem.setIdentifier(itemChild.identifier);
		targetItem.setVersion(Integer.valueOf(itemChild.version));
		objectColecticaPost.setTargetItem(targetItem);
		objectColecticaPost.setUseDistinctResultItem(true);
		objectColecticaPost.setUseDistinctTargetItem(true);
		Relationship[] relationshipsInstrument = metadataService.getRelationship(objectColecticaPost);
		String DDIidentifier = relationshipsInstrument[0].getIdentifierTriple().getIdentifier();
		ColecticaItem item = metadataServiceItem.getItem(DDIidentifier);
		return item;
	}

	/**
	 * This method build the DDI Questionnaire
	 * 
	 * @return DDIQuestionnaire.toString()
	 * @throws Exception
	 */
	private String buildQuestionnaire() throws Exception {
		// Step 1 : get all the children of the instrument (include the
		// instrument by default)
		ColecticaItemRefList listChildrenWithoutInstrument = metadataServiceItem
				.getChildrenRef(instrument.getIdentifier());
		ColecticaItemRef instrumentTemp = null;
		// Step 2 : Among all of the itemsReferences, the instrument will be get
		// and removed from this list
		for (ColecticaItemRef childInstrument : listChildrenWithoutInstrument.identifiers) {
			if (childInstrument.identifier.equals(idDDIInstrument)) {
				instrumentTemp = childInstrument;
			}
		}
		if (instrumentTemp != null) {
			listChildrenWithoutInstrument.identifiers.remove(instrumentTemp);
		}
		// Step 3 : Build the group, from the
		// studyUnit to the group
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder();
		ddiInstanceService.addDDIInstanceInformationToDocBuilder(DDIInstance, docBuilder);
		convertAsNodesWithXPath(docBuilder);
		appendChildsByParent(docBuilder);
		// Step 4 : return the filled out enveloppe
		// as result
		processItemsRessourcePackage(docBuilder, listChildrenWithoutInstrument);
		return docBuilder.toString();

	}

	private void appendChildsByParent(DDIDocumentBuilder docBuilder) {
		removeReferences(groupNode);
		docBuilder.appendChild(groupNode);
		removeReferences(subGroupNode);
		docBuilder.appendChildByParent("Group", subGroupNode);
		removeReferences(studyUnitNode);
		docBuilder.appendChildByParent("SubGroup", studyUnitNode);
		// Step 1 : Insert the content of the
		// DataCollection got to the enveloppe as
		// a child of the StudyUnit.
		removeReferences(dcNode);
		docBuilder.appendChildByParent("StudyUnit", dcNode);
		removeReferences(instrumentSchemeNode);
		docBuilder.appendChildByParent("DataCollection", instrumentSchemeNode);
		docBuilder.appendChildByParent("InstrumentScheme", instrumentNode);

	}

	private void convertAsNodesWithXPath(DDIDocumentBuilder docBuilder) throws Exception {
		this.subGroupNode = getNodeColecticaItemfromDocument(subGroupItem, docBuilder);
		this.groupNode = getNodeColecticaItemfromDocument(groupItem, docBuilder);
		this.studyUnitNode = getNodeColecticaItemfromDocument(studyUnitItem, docBuilder);
		this.dcNode = getNodeColecticaItemfromDocument(dataCollection, docBuilder);
		this.variableSchemeNode = getNodeColecticaItemfromDocument(variableScheme, docBuilder);
		this.instrumentSchemeNode = getNodeColecticaItemfromDocument(instrumentScheme, docBuilder);
		this.instrumentNode = getNodeColecticaItemfromDocument(instrument, docBuilder);
	}

	
	/**
	 * get the first Node of the item in the Fragment
	 * @param ColecticaItem item : target item
	 * @param DDIDocumentBuilder docBuilder : Document
	 * @return Node itemNode
	 * @throws Exception
	 */
	public Node getNodeColecticaItemfromDocument(ColecticaItem item, DDIDocumentBuilder docBuilder) throws Exception {
		return DocumentBuilderUtils.getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(item.getItem(), "/Fragment[1]/*").item(0)), docBuilder);
	}

	private void processItemsRessourcePackage(DDIDocumentBuilder docBuilder,
			ColecticaItemRefList listItemsChildrenInstrument) throws Exception {

		List<ItemWithParent> parentsWithCildren = new ArrayList<ItemWithParent>();
		List<ColecticaItem> items = metadataServiceItem.getItems(listItemsChildrenInstrument);

		// Step 1 : Insert the other references of
		// the studyUnit to the
		// enveloppe as children of
		// the first RessourcePackage
		processingSchemes(items, docBuilder, parentsWithCildren);

		// Step 2 : get the Ressource Packages (parents of the schemes) with
		// each scheme in the right package.
		List<ItemWithParent> rpItemsNodeString = getRessourcePackagesWithSchemes(docBuilder, parentsWithCildren);

		// Step 3 : Insert the Ressource Packages in the Root Document.
		for (ItemWithParent rpItemNodeString : rpItemsNodeString) {
			removeReferences(rpItemNodeString.getRessourcePackageNode());
			docBuilder.appendChild(rpItemNodeString.getRessourcePackageNode());
		}

	}

	private List<ItemWithParent> getRessourcePackagesWithSchemes(DDIDocumentBuilder docBuilder,
			List<ItemWithParent> parentsWithCildren) throws Exception {
		List<String> identifiersRP = new ArrayList<String>();
		List<ItemWithParent> rpItemsNodeString = new ArrayList<ItemWithParent>();
		for (ItemWithParent itemParentWithChildren : parentsWithCildren) {
			ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
			List<String> itemTypes = new ArrayList<String>();
			itemTypes.add(DDIItemType.RESSOURCEPACKAGE.getUUID());
			objectColecticaPost.setItemTypes(itemTypes);
			TargetItem targetItem = new TargetItem();
			targetItem.setAgencyId(itemParentWithChildren.getParent().agencyId);
			targetItem.setIdentifier(itemParentWithChildren.getParent().identifier);
			targetItem.setVersion(Integer.valueOf(itemParentWithChildren.getParent().version));
			objectColecticaPost.setTargetItem(targetItem);
			objectColecticaPost.setUseDistinctResultItem(true);
			objectColecticaPost.setUseDistinctTargetItem(true);
			Relationship[] relationshipsRP = metadataService.getRelationship(objectColecticaPost);
			String identifierRP = relationshipsRP[0].getIdentifierTriple().getIdentifier();

			if (identifiersRP.contains(identifierRP)) {
				for (ItemWithParent rpItemNodeString : rpItemsNodeString) {
					if (rpItemNodeString.getItem().getIdentifier().equals(identifierRP)) {
						removeReferences(itemParentWithChildren.getParentNode());
						rpItemNodeString.getRessourcePackageNode().appendChild(itemParentWithChildren.getParentNode());
					}
				}
			} else {
				ColecticaItem rpItem = metadataServiceItem.getItem(identifierRP);
				ItemWithParent rpItemNodeString = new ItemWithParent();
				rpItemNodeString.setItem(rpItem);
				rpItemNodeString.setRessourcePackageNode(DocumentBuilderUtils.getNode(
						UtilXML.nodeToString(xpathProcessor.queryList(rpItem.getItem(), "/Fragment[1]/*").item(0)),
						docBuilder));
				removeReferences(itemParentWithChildren.getParentNode());
				rpItemNodeString.getRessourcePackageNode().appendChild(itemParentWithChildren.getParentNode());
				rpItemNodeString.setRessourcePackage(rpItem);
				rpItemsNodeString.add(rpItemNodeString);
				identifiersRP.add(identifierRP);
			}
		}
		return rpItemsNodeString;
	}

	private void processingSchemes(List<ColecticaItem> items, DDIDocumentBuilder docBuilder,
			List<ItemWithParent> parentsWithCildren) throws Exception {
		List<String> identifierParentsWithCildren = new ArrayList<String>();
		List<Node> itemSchemeNodes = new ArrayList<Node>();
		List<ColecticaItem> itemSchemes = new ArrayList<ColecticaItem>();
		List<String> itemTypes = new ArrayList<String>();

		// Adding variableScheme : independent scheme
		ColecticaItemRefList refsVariables = metadataServiceItem.getChildrenRef(this.variableScheme.getIdentifier());
		List<ColecticaItem> variables = metadataServiceItem.getItems(refsVariables);
		for (ColecticaItem variable : variables) {
			Node variableNode = DocumentBuilderUtils.getNode(
					UtilXML.nodeToString(xpathProcessor.queryList(variable.item, "/Fragment[1]/*").item(0)),
					docBuilder);
			this.variableSchemeNode.appendChild(variableNode);
		}
		ItemWithParent variableScheme = new ItemWithParent();
		variableScheme.setParent(this.variableScheme);
		ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
		variableScheme.setRessourcePackage(
				searchItemParent(itemTypes, DDIItemType.RESSOURCEPACKAGE, objectColecticaPost, this.variableScheme));
		variableScheme.setRessourcePackageNode(DocumentBuilderUtils.getNode(
				UtilXML.nodeToString(
						xpathProcessor.queryList(variableScheme.getRessourcePackage().item, "/Fragment[1]/*").item(0)),
				docBuilder));
		removeReferences(variableScheme.getRessourcePackageNode());
		variableScheme.setParentNode(this.variableSchemeNode);
		parentsWithCildren.add(variableScheme);

		for (DDIItemType type : DDIItemType.values()) {
			if (type.getName().endsWith("Scheme")) {
				itemTypes.add(type.getUUID());
			}
		}
		for (ColecticaItem item : items) {
			objectColecticaPost = new ObjectColecticaPost();
			Node node = DocumentBuilderUtils.getNode(
					UtilXML.nodeToString(xpathProcessor.queryList(item.getItem(), "/Fragment[1]/*[1]").item(0)),
					docBuilder);
			removeReferences(node);
			TargetItem targetItem = new TargetItem();
			targetItem.setAgencyId(item.agencyId);
			targetItem.setVersion(Integer.valueOf(item.version));
			targetItem.setIdentifier(item.identifier);
			objectColecticaPost.setItemTypes(itemTypes);
			objectColecticaPost.setTargetItem(targetItem);
			objectColecticaPost.setUseDistinctResultItem(true);
			objectColecticaPost.setUseDistinctTargetItem(true);
			if (itemTypes.size() > 0) {
				Relationship[] relationshipsSchemes = metadataService.getRelationship(objectColecticaPost);
				for (int i = 0; i < relationshipsSchemes.length; i++) {
					ItemWithParent itemWithParent = new ItemWithParent();
					itemWithParent.setItem(item);
					itemWithParent.setItemNode(DocumentBuilderUtils.getNode(
							UtilXML.nodeToString(xpathProcessor.queryList(item.getItem(), "/Fragment[1]/*").item(0)),
							docBuilder));
					itemWithParent.setParent(
							metadataServiceItem.getItem(relationshipsSchemes[i].getIdentifierTriple().getIdentifier()));
					itemWithParent.setParentNode(DocumentBuilderUtils.getNode(
							UtilXML.nodeToString(xpathProcessor
									.queryList(itemWithParent.getParent().getItem(), "/Fragment[1]/*").item(0)),
							docBuilder));
					// First adding of a parentNode
					if (!identifierParentsWithCildren.contains(itemWithParent.getParent().getIdentifier())) {
						removeReferences(itemWithParent.getParentNode());
						addParentNode(parentsWithCildren, itemWithParent, identifierParentsWithCildren, itemSchemes,
								itemSchemeNodes);
					} else {
						// Update of the parent node with a new child Node
						updateParentNode(parentsWithCildren, itemWithParent);
					}
				}
			}
		}
	}

	/**
	 * Add the parent Node and the Parent Item to the currentItem. The aim of this
	 * method is to construct the parent tree
	 * 
	 * @param parentsWithCildren
	 * @param itemWithParent
	 * @param identifierParentsWithCildren
	 * @param itemSchemes
	 * @param itemSchemeNodes
	 * @throws RMeSException
	 */
	public void addParentNode(List<ItemWithParent> parentsWithCildren, ItemWithParent itemWithParent,
			List<String> identifierParentsWithCildren, List<ColecticaItem> itemSchemes, List<Node> itemSchemeNodes)
			throws RMeSException {
		if (itemWithParent != null && itemWithParent.getParent() != null) {
			parentsWithCildren.add(itemWithParent);
			identifierParentsWithCildren.add(itemWithParent.getParent().getIdentifier());
			itemSchemes.add(itemWithParent.getParent());
			itemSchemeNodes.add(itemWithParent.getParentNode());
		} else {
			throw new RMeSException(500, "The parentNode of a Colectica Item was not found or is unreachable.",
					itemWithParent.toString());
		}
	}

	/**
	 * 
	 * @param parentsWithCildren
	 * @param itemWithParent
	 * @throws RMeSException
	 */
	public void updateParentNode(List<ItemWithParent> parentsWithCildren, ItemWithParent itemWithParent)
			throws RMeSException {
		for (ItemWithParent itemParentWithChildren : parentsWithCildren) {
			if (itemParentWithChildren != null && itemParentWithChildren.getParent() != null) {
				if (itemParentWithChildren.getParent().getIdentifier()
						.equals(itemWithParent.getParent().getIdentifier())) {
					removeReferences(itemWithParent.getItemNode());
					itemParentWithChildren.getParentNode().appendChild(itemWithParent.getItemNode());
				}
			} else {
				throw new RMeSException(500, "The parentNode of a Colectica Item was not found or is unreachable.",
						itemWithParent.toString());
			}
		}
	}

	/**
	 * Remove unused references of the DDIDocument builder
	 * 
	 * @param node
	 *            (root node for searching references)
	 */
	private void removeReferences(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node nodeRef = children.item(i);
			if (nodeRef.getNodeType() == Node.ELEMENT_NODE) {
				if (nodeRef.getNodeName().contains("Reference")) {
					Node parentNode = nodeRef.getParentNode();
					parentNode.removeChild(nodeRef);
				}
			}
		}
	}

}
