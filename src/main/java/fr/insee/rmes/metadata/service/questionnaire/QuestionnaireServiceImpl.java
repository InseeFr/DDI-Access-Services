package fr.insee.rmes.metadata.service.questionnaire;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.recycler.Recycler.C;
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

	@Override
	public String getQuestionnaire(String idDDIInstance, String idDDIInstrument) throws Exception {
		List<ColecticaItem> parentsOfInstrument = new ArrayList<ColecticaItem>();

		// Step 1 : Get the DDIInstance, the DDIInstrument and Check type (an
		// Exception throws if not)
		ColecticaItem ddiInstance = metadataServiceItem.getItemByType(idDDIInstance, DDIItemType.DDI_INSTANCE);
		logger.info(ddiInstance.getIdentifier());
		//////////////////////////////////////////////
		// While Instrument not found
		// Step 2 : Get all the group references
		NodeList childrenInstance = xpathProcessor.queryList(ddiInstance.getItem(),
				"//*[local-name()='Fragment']/*[local-name()='DDIInstance']/*[local-name()='GroupReference']");
		for (int indexGroup = 1; indexGroup < childrenInstance.getLength() + 1; indexGroup++) {
			String idGroup = xpathProcessor.queryString(ddiInstance.getItem(),
					"//*[local-name()='Fragment']/*[local-name()='DDIInstance']/*[local-name()='GroupReference']["
							+ indexGroup + "]/*[local-name()='ID']/text()");
			ColecticaItem groupItem = metadataServiceItem.getItem(idGroup);
			// Step 3 : foreach group in groups --> Search subGroups and
			// store the
			// currentGroup as ColecticaItem
			NodeList childrenGroup = xpathProcessor.queryList(groupItem.getItem(),
					"//*[local-name()='Fragment']/*[local-name()='Group']/*[local-name()='SubGroupReference']");
			for (int indexSubGroup = 1; indexSubGroup < childrenGroup.getLength() + 1; indexSubGroup++) {
				String idSubGroup = xpathProcessor.queryString(groupItem.getItem(),
						"//*[local-name()='Fragment']/*[local-name()='Group']/*[local-name()='SubGroupReference']["
								+ indexSubGroup + "]/*[local-name()='ID']/text()");
				ColecticaItem subGroupItem = metadataServiceItem.getItem(idSubGroup);
				// Step 4 : foreach subGroup in subGroups in currentGroup
				// --> Search
				// StudyUnits and store the currentSubGroup as ColecticaItem
				NodeList childrenSubGroup = xpathProcessor.queryList(subGroupItem.getItem(),
						"//*[local-name()='Fragment']/*[local-name()='SubGroup']/*[local-name()='StudyUnitReference']");
				for (int indexStudyUnit = 1; indexStudyUnit < childrenSubGroup.getLength() + 1; indexStudyUnit++) {
					String idStudyUnit = xpathProcessor.queryString(subGroupItem.getItem(),
							"//*[local-name()='Fragment']/*[local-name()='SubGroup']/*[local-name()='StudyUnitReference']["
									+ indexStudyUnit + "]/*[local-name()='ID']/text()");
					ColecticaItem studyUnitItem = metadataServiceItem.getItem(idStudyUnit);

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
						ColecticaItem dataCollection = metadataServiceItem.getItem(idDataCollection);
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
							ColecticaItem instrumentScheme = metadataServiceItem.getItem(idInstrumentScheme);
							logger.info(instrumentScheme.identifier);

							NodeList instrumentSchemeDC = xpathProcessor.queryList(instrumentScheme.getItem(),
									"//*[local-name()='Fragment']/*[local-name()='InstrumentScheme']/*[local-name()='InstrumentReference']");
							for (int indexInstrument = 1; indexInstrument < instrumentSchemeDC.getLength()
									+ 1; indexInstrument++) {
								String idInstrument = xpathProcessor.queryString(instrumentScheme.getItem(),
										"//*[local-name()='Fragment']/*[local-name()='InstrumentScheme']/*[local-name()='InstrumentReference']["
												+ indexDataCollection + "]/*[local-name()='ID']/text()");
								ColecticaItem instrument = metadataServiceItem.getItem(idInstrument);
								logger.info(instrument.identifier);

								if (instrument.identifier.equals(idDDIInstrument)) {
									parentsOfInstrument.add(groupItem);
									parentsOfInstrument.add(subGroupItem);
									parentsOfInstrument.add(studyUnitItem);
									parentsOfInstrument.add(dataCollection);
									parentsOfInstrument.add(instrumentScheme);

									ColecticaItemRefList listChildrenInstrument = metadataServiceItem
											.getChildrenRef(instrument.getIdentifier());
									ColecticaItemRef instrumentTemp = null;
									for (ColecticaItemRef childInstrument : listChildrenInstrument.identifiers) {
										if (childInstrument.identifier.equals(idDDIInstrument)) {
											instrumentTemp = childInstrument;
										}
									}
									if (instrumentTemp != null) {
										listChildrenInstrument.identifiers.remove(instrumentTemp);
									}

									// Step : Build the group, from the
									// studyUnit to the group
									DDIDocumentBuilder docBuilder = new DDIDocumentBuilder(true, Envelope.INSTRUMENT);

									Node subGroupNode = getNode(
											UtilXML.nodeToString(xpathProcessor
													.queryList(subGroupItem.getItem(), "/Fragment[1]/*").item(0)),
											docBuilder.getDocument());

									subGroupNode = getNode(UtilXML.nodeToString(subGroupNode),
											docBuilder.getDocument());
									Node groupNode = getNode(
											UtilXML.nodeToString(xpathProcessor
													.queryList(groupItem.getItem(), "/Fragment[1]/*").item(0)),
											docBuilder.getDocument());

									Node studyUnitNode = getNode(
											UtilXML.nodeToString(xpathProcessor
													.queryList(studyUnitItem.getItem(), "/Fragment[1]/*").item(0)),
											docBuilder.getDocument());

									Node DCNode = getNode(
											UtilXML.nodeToString(xpathProcessor
													.queryList(dataCollection.getItem(), "/Fragment[1]/*").item(0)),
											docBuilder.getDocument());

									Node instrumentSchemeNode = getNode(
											UtilXML.nodeToString(xpathProcessor
													.queryList(instrumentScheme.item, "/Fragment[1]/*").item(0)),
											docBuilder.getDocument());

									Node instrumentNode = getNode(
											UtilXML.nodeToString(xpathProcessor
													.queryList(instrument.item, "/Fragment[1]/*").item(0)),
											docBuilder.getDocument());

									// Step : Get the first Resource package
									String idRP = xpathProcessor.queryString(ddiInstance.getItem(),
											"/Fragment[1]/DDIInstance[1]/ResourcePackageReference[1]/ID[1]/text()");
									String rpString = xpathProcessor.queryString(metadataServiceItem.getItem(idRP).item,
											"/Fragment[1]/*");
									Node RP1 = getNode(rpString, docBuilder.getDocument());

									// Step : Get DDI Instance informations on
									// root : r:URN, r:Agency, r:ID, r:Version,
									// r:UserID, r:Citation
									String urnString = xpathProcessor.queryString(ddiInstance.getItem(),
											"/Fragment[1]/DDIInstance[1]/URN[1]");
									Node urnNode = getNode(urnString.trim(), docBuilder.getDocument());
									String agencyString = xpathProcessor.queryString(ddiInstance.getItem(),
											"/Fragment[1]/DDIInstance[1]/Agency[1]");
									Node agencyNode = getNode(agencyString, docBuilder.getDocument());
									String idString = xpathProcessor.queryString(ddiInstance.getItem(),
											"/Fragment[1]/DDIInstance[1]/ID[1]");
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
									docBuilder.appendChildByParent("Group", subGroupNode);
									docBuilder.appendChildByParent("SubGroup", studyUnitNode);
									docBuilder.appendChildByParent("StudyUnit", DCNode);
									docBuilder.appendChildByParent("DataCollection", instrumentSchemeNode);
									docBuilder.appendChildByParent("InstrumentScheme", instrumentNode);
									docBuilder.appendChild(RP1);
									List<ColecticaItem> items = metadataServiceItem.getItems(listChildrenInstrument);
									for (ColecticaItem item : items) {
										Node itemNode = getNode(
												UtilXML.nodeToString(xpathProcessor
														.queryList(item.getItem(), "/Fragment[1]/*").item(0)),
												docBuilder.getDocument());
										docBuilder.appendChildByParent("ResourcePackage", itemNode);
									}

									return docBuilder.toString();
								}

							}
						}
					}
				}
			}
		}

		//// Step 7 : if the idDDIInstrument : leave the Loop and get the
		//// children list of this instrument
		/////////////////////////////////////////////////////////////////

		// Step 8 : Among all of the Instrument's chidren, search the instrument
		// relating to its id as parameter

		// Step 9 : Insert the content of the instrument got to the enveloppe as
		// a child of StudyUnit's template.

		// Step 10 : Insert the other references of the studyUnit to the
		// enveloppe as children of RessourcePackageTemplate

		// Step 11 : return the filled out enveloppe as result

		return "";
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
