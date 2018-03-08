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
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.TargetItem;
import fr.insee.rmes.metadata.model.identifierTriple;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.service.SearchService;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.utils.ddi.Envelope;
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
	SearchService searchService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	XpathProcessor xpathProcessor;

	private String idDDIInstrument;

	private ColecticaItem instrument;

	private ColecticaItem DDIInstance;

	private ColecticaItem subGroupItem;

	private ColecticaItem groupItem;

	private ColecticaItem studyUnitItem;

	private ColecticaItem dataCollection;

	private ColecticaItem instrumentScheme;

	private Node groupNode;

	private Node subGroupNode;

	private Node studyUnitNode;

	private Node DCNode;

	private Node instrumentSchemeNode;

	private Node instrumentNode;

	private Node urnNode;

	private Node agencyNode;

	private Node idNode;

	private Node versionNode;

	private Node userIDNode;

	private Node citationNode;

	private Node RP1;

	@Override
	public String getQuestionnaire(String idDDIInstance, String idDDIInstrument) throws Exception {
		this.idDDIInstrument = idDDIInstrument;

		// Step 1 : Get the DDIInstance, the DDIInstrument and Check type (an
		// Exception throws if not)
		this.DDIInstance = metadataServiceItem.getItemByType(idDDIInstance, DDIItemType.DDI_INSTANCE);
		try {
			ColecticaItem DDIInstrument = metadataServiceItem.getItemByType(idDDIInstrument, DDIItemType.QUESTIONNAIRE);
		} catch (Exception e) {
			throw e;
		}
		//////////////////////////////////////////////
		// While Instrument not found
		// Step 2 : Get all the group references
		NodeList childrenInstance = xpathProcessor.queryList(DDIInstance.getItem(),
				"//*[local-name()='Fragment']/*[local-name()='DDIInstance']/*[local-name()='GroupReference']");
		for (int indexGroup = 1; indexGroup < childrenInstance.getLength() + 1; indexGroup++) {
			String idGroup = xpathProcessor.queryString(DDIInstance.getItem(),
					"//*[local-name()='Fragment']/*[local-name()='DDIInstance']/*[local-name()='GroupReference']["
							+ indexGroup + "]/*[local-name()='ID']/text()");
			this.groupItem = metadataServiceItem.getItem(idGroup);
			// Step 3 : foreach group in groups --> Search subGroups and
			// store the
			// currentGroup as ColecticaItem
			NodeList childrenGroup = xpathProcessor.queryList(groupItem.getItem(),
					"//*[local-name()='Fragment']/*[local-name()='Group']/*[local-name()='SubGroupReference']");
			for (int indexSubGroup = 1; indexSubGroup < childrenGroup.getLength() + 1; indexSubGroup++) {
				String idSubGroup = xpathProcessor.queryString(groupItem.getItem(),
						"//*[local-name()='Fragment']/*[local-name()='Group']/*[local-name()='SubGroupReference']["
								+ indexSubGroup + "]/*[local-name()='ID']/text()");
				this.subGroupItem = metadataServiceItem.getItem(idSubGroup);
				// Step 4 : foreach subGroup in subGroups in currentGroup
				// --> Search
				// StudyUnits and store the currentSubGroup as ColecticaItem
				NodeList childrenSubGroup = xpathProcessor.queryList(subGroupItem.getItem(),
						"//*[local-name()='Fragment']/*[local-name()='SubGroup']/*[local-name()='StudyUnitReference']");
				for (int indexStudyUnit = 1; indexStudyUnit < childrenSubGroup.getLength() + 1; indexStudyUnit++) {
					String idStudyUnit = xpathProcessor.queryString(subGroupItem.getItem(),
							"//*[local-name()='Fragment']/*[local-name()='SubGroup']/*[local-name()='StudyUnitReference']["
									+ indexStudyUnit + "]/*[local-name()='ID']/text()");
					this.studyUnitItem = metadataServiceItem.getItem(idStudyUnit);

					// Step 5 : foreach StudyUnit in currentStudyUnit in
					// currentSubGroup in
					// currentGroup -->
					// Search Instruments and store the currentStudyUnit as
					// ColecticaItem
					NodeList childrenStudyUnit = xpathProcessor.queryList(subGroupItem.getItem(),
							"//*[local-name()='Fragment']/*[local-name()='SubGroup']/*[local-name()='StudyUnitReference']");
					for (int indexDataCollection = 1; indexDataCollection < childrenStudyUnit.getLength()
							+ 1; indexDataCollection++) {
						String idDataCollection = xpathProcessor.queryString(studyUnitItem.getItem(),
								"//*[local-name()='Fragment']/*[local-name()='StudyUnit']/*[local-name()='DataCollectionReference']["
										+ indexDataCollection + "]/*[local-name()='ID']/text()");
						this.dataCollection = metadataServiceItem.getItem(idDataCollection);
						// Step 6 : foreach DataCollection in currentDC in
						// currentStudyUnit in
						// currentSubGroup ...
						NodeList childrenDC = xpathProcessor.queryList(dataCollection.getItem(),
								"//*[local-name()='Fragment']/*[local-name()='DataCollection']/*[local-name()='InstrumentSchemeReference']");
						for (int indexInstrumentScheme = 1; indexInstrumentScheme < childrenDC.getLength()
								+ 1; indexInstrumentScheme++) {
							String idInstrumentScheme = xpathProcessor.queryString(dataCollection.getItem(),
									"//*[local-name()='Fragment']/*[local-name()='DataCollection']/*[local-name()='InstrumentSchemeReference']["
											+ indexDataCollection + "]/*[local-name()='ID']/text()");
							this.instrumentScheme = metadataServiceItem.getItem(idInstrumentScheme);

							NodeList instrumentSchemeDC = xpathProcessor.queryList(instrumentScheme.getItem(),
									"//*[local-name()='Fragment']/*[local-name()='InstrumentScheme']/*[local-name()='InstrumentReference']");
							//// Step 7 : if the idDDIInstrument : leave the
							//// Loop and get the
							//// children list of this instrument
							for (int indexInstrument = 1; indexInstrument < instrumentSchemeDC.getLength()
									+ 1; indexInstrument++) {
								String idInstrument = xpathProcessor.queryString(instrumentScheme.getItem(),
										"//*[local-name()='Fragment']/*[local-name()='InstrumentScheme']/*[local-name()='InstrumentReference']["
												+ indexDataCollection + "]/*[local-name()='ID']/text()");
								this.instrument = metadataServiceItem.getItem(idInstrument);
								// Step 8 : Among all of the Instrument's
								// chidren, search the instrument
								// relating to its id as parameter
								if (instrument.identifier.equals(idDDIInstrument)) {
									return buildQuestionnaire();
								}

							}
						}
					}
				}
			}
		}

		throw new RMeSException(404, "The DDI Instrument specified as parameter was not found.", "");
	}

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
		DDIDocumentBuilder docBuilder = new DDIDocumentBuilder(true, Envelope.INSTRUMENT);
		convertAsNodesWithXPath(docBuilder);
		// Step 4 : Get the first Resource package
		String idRP = xpathProcessor.queryString(DDIInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/ResourcePackageReference[1]/ID[1]/text()");
		String rpString = xpathProcessor.queryString(metadataServiceItem.getItem(idRP).item, "/Fragment[1]/*");
		this.RP1 = getNode(rpString, docBuilder.getDocument());
		convertAsDDINodesInformation(docBuilder);
		appendChildsByParent(docBuilder);
		// Step 5 : return the filled out enveloppe
		// as result
		processItemsRessourcePackage(docBuilder, listChildrenWithoutInstrument);
		return docBuilder.toString();

	}

	private void appendChildsByParent(DDIDocumentBuilder docBuilder) {
		docBuilder.appendChild(urnNode);
		docBuilder.appendChild(agencyNode);
		docBuilder.appendChild(idNode);
		docBuilder.appendChild(versionNode);
		docBuilder.appendChild(userIDNode);
		docBuilder.appendChild(citationNode);
		docBuilder.appendChild(groupNode);
		docBuilder.appendChildByParent("Group", subGroupNode);
		docBuilder.appendChildByParent("SubGroup", studyUnitNode);
		// Step 1 : Insert the content of the
		// DataCollection got to the enveloppe as
		// a child of the StudyUnit.
		docBuilder.appendChildByParent("StudyUnit", DCNode);
		docBuilder.appendChildByParent("DataCollection", instrumentSchemeNode);
		docBuilder.appendChildByParent("InstrumentScheme", instrumentNode);
		docBuilder.appendChild(RP1);

	}

	private void convertAsDDINodesInformation(DDIDocumentBuilder docBuilder) throws Exception {
		// Step
		// 1 :
		// Get
		// DDI
		// Instance
		// informations
		// on
		// root : r:URN, r:Agency, r:ID, r:Version,
		// r:UserID, r:Citation
		String urnString = xpathProcessor.queryString(DDIInstance.getItem(), "/Fragment[1]/DDIInstance[1]/URN[1]");
		this.urnNode = getNode(urnString.trim(), docBuilder.getDocument());
		String agencyString = xpathProcessor.queryString(DDIInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/Agency[1]");
		this.agencyNode = getNode(agencyString, docBuilder.getDocument());
		String idString = xpathProcessor.queryString(DDIInstance.getItem(), "/Fragment[1]/DDIInstance[1]/ID[1]");
		this.idNode = getNode(idString, docBuilder.getDocument());
		String versionString = xpathProcessor.queryString(DDIInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/Version[1]");
		this.versionNode = getNode(versionString, docBuilder.getDocument());
		String userIDString = xpathProcessor.queryString(DDIInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/UserID[1]");
		this.userIDNode = getNode(userIDString, docBuilder.getDocument());
		String citationString = xpathProcessor.queryString(DDIInstance.getItem(),
				"/Fragment[1]/DDIInstance[1]/Citation[1]");
		this.citationNode = getNode(citationString, docBuilder.getDocument());

	}

	private void convertAsNodesWithXPath(DDIDocumentBuilder docBuilder) throws Exception {
		this.subGroupNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(subGroupItem.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

		subGroupNode = getNode(UtilXML.nodeToString(subGroupNode), docBuilder.getDocument());
		this.groupNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(groupItem.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

		this.studyUnitNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(studyUnitItem.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

		this.DCNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(dataCollection.getItem(), "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

		this.instrumentSchemeNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(instrumentScheme.item, "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

		this.instrumentNode = getNode(
				UtilXML.nodeToString(xpathProcessor.queryList(instrument.item, "/Fragment[1]/*").item(0)),
				docBuilder.getDocument());

	}

	private void processItemsRessourcePackage(DDIDocumentBuilder docBuilder,
			ColecticaItemRefList listItemsChildrenInstrument) throws Exception {

		List<ColecticaItem> items = metadataServiceItem.getItems(listItemsChildrenInstrument);
		// Step 1 : Insert the other references of
		// the studyUnit to the
		// enveloppe as children of
		// the first RessourcePackage
		for (ColecticaItem item : items) {
			// ObjectColecticaPost objectColecticaPost = new
			// ObjectColecticaPost();
			// List<String> itemTypes = new ArrayList<String>();
			// itemTypes.add("F152EE61-08BA-4FCA-8A3A-DAF8F87F972E");
			// TargetItem targetItem = new TargetItem();
			// targetItem.setAgencyId("fr.insee");
			// targetItem.setVersion(0);
			// targetItem.setIdentifier("14c03455-8546-49a7-b664-a6623b22b2ad");
			// objectColecticaPost.setItemTypes(itemTypes);
			//
			// objectColecticaPost.setTargetItem(targetItem);
			// objectColecticaPost.setUseDistinctResultItem(true);
			// objectColecticaPost.setUseDistinctTargetItem(true);
			// TODO: send POST requests to get the
			// relationship (parent) of each item
			// which has a Scheme (example :
			// Category --> CategoryScheme
			//Relationship[] relationships = metadataService.getRelationship(objectColecticaPost);
			// relationships = searchSchemes(relationships);
			// insertSchemes(relationships, docBuilder);
			Node itemNode = getNode(
					UtilXML.nodeToString(xpathProcessor.queryList(item.getItem(), "/Fragment[1]/*").item(0)),
					docBuilder.getDocument());
			docBuilder.appendChildByParent("ResourcePackage", itemNode);
		}
	}

	private Relationship[] searchSchemes(Relationship[] relationships) {
		//TODO: serach schemes among the Relationships tab.
		List<Relationship> relationShipFilter = new ArrayList<Relationship>();
		for (Relationship relationship : relationships) {
			// ColecticaItem colecticaItem = new ColecticaItemRef()
			// relationship.getIdentifierTriple().getIdentifier()
		}
		return null;
	}

	private void insertSchemes(Relationship[] relationships, DDIDocumentBuilder docBuilder) {

	}

	private Node getNode(String fragment, Document doc) throws Exception {
		Element node = getDocument(fragment).getDocumentElement();
		Node newNode = node.cloneNode(true);
		// Transfer ownership of the new node into the destination document
		doc.adoptNode(newNode);
		return newNode;
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

}
