package fr.insee.rmes.metadata.service;

import fr.insee.rmes.metadata.model.*;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.ResourcePackage;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.webservice.rest.RMeSException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetadataServiceFakeImpl implements MetadataService {

	private final static Logger logger = LogManager.getLogger(MetadataServiceFakeImpl.class);

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	XpathProcessor xpathProcessor;

	@Override
	public List<String> getGroupIds() throws Exception {
		return groupRepository.getRootIds();
	}

	@Override
	public ColecticaItem getItem(String id) throws Exception {
		return metadataRepository.findById(id);
	}

	@Override
	public ColecticaItemRefList getChildrenRef(String id) throws Exception {
		return metadataRepository.getChildrenRef(id);
	}

	@Override
	public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
		return metadataRepository.getItems(refs);
	}

	@Override
	public List<Unit> getUnits() throws Exception {
		return metadataRepository.getUnits();
	}

	public ResponseItem getDDIRoot(String id) throws Exception {
		ResponseItem ddiRoot = new ResponseItem();
		String fragment = getItem(id).item;
		logger.debug("Fragment : " + fragment);
		String rootExp = "//*[local-name()='DDIInstance']";
		String labelExp = "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()";
		Node rootNode = xpathProcessor.queryList(fragment, rootExp).item(0);
		ddiRoot.setId(id);
		ddiRoot.setLabel(xpathProcessor.queryText(rootNode, labelExp));
		ddiRoot.setResourcePackageId(getResourcePackageId(rootNode));
		ddiRoot.setChildren(getGroups(rootNode, ddiRoot));
		logger.debug("ddiRoot : " + ddiRoot.toString());
		return ddiRoot;
	}

	public String getResourcePackageId(Node rootNode) throws Exception {
		String childExp = ".//*[local-name()='ResourcePackageReference']";
		Node rpNode = xpathProcessor.queryList(rootNode, childExp).item(0);
		return xpathProcessor.queryText(rpNode, ".//*[local-name()='ID']/text()");
	}

	public List<ResponseItem> getGroups(Node node, ResponseItem ddiRoot) throws Exception {
		List<ResponseItem> groups = new ArrayList<>();
		String childExp = ".//*[local-name()='GroupReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem group = new ResponseItem();
			group.setId(id);
			group.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='Group']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
			group.setParent(ddiRoot.getId());
			group.setResourcePackageId(ddiRoot.getResourcePackageId());
			group.setChildren(getSubGroups(child, group));
			groups.add(group);
		}
		return groups;
	}

	private List<ResponseItem> getSubGroups(Node node, ResponseItem group) throws Exception {
		List<ResponseItem> subGroups = new ArrayList<>();
		String childExp = ".//*[local-name()='SubGroupReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem subGroup = new ResponseItem();
			subGroup.setGroupId(group.getId());
			subGroup.setId(id);
			subGroup.setSubGroupId(id);
			subGroup.setResourcePackageId(group.getResourcePackageId());
			subGroup.setParent(group.getId());
			subGroup.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
			subGroup.setChildren(getStudyUnits(child, subGroup));
			subGroups.add(subGroup);
		}
		return subGroups;
	}

	private List<ResponseItem> getStudyUnits(Node node, ResponseItem subGroup) throws Exception {
		List<ResponseItem> studyUnits = new ArrayList<>();
		String childExp = ".//*[local-name()='StudyUnitReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem studyUnit = new ResponseItem();
			studyUnit.setGroupId(subGroup.getGroupId());
			studyUnit.setSubGroupId(subGroup.getId());
			studyUnit.setId(id);
			studyUnit.setStudyUnitId(id);
			studyUnit.setResourcePackageId(subGroup.getResourcePackageId());
			studyUnit.setParent(subGroup.getId());
			studyUnit.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
			studyUnit.setChildren(getDataCollections(child, studyUnit));
			studyUnits.add(studyUnit);
		}
		return studyUnits;
	}

	private List<ResponseItem> getDataCollections(Node node, ResponseItem studyUnit) throws Exception {
		List<ResponseItem> dataCollections = new ArrayList<>();
		String childExp = ".//*[local-name()='DataCollectionReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem dataCollection = new ResponseItem();
			dataCollection.setId(id);
			dataCollection.setDataCollectionId(id);
			dataCollection.setGroupId(studyUnit.getGroupId());
			dataCollection.setSubGroupId(studyUnit.getSubGroupId());
			dataCollection.setStudyUnitId(studyUnit.getId());
			dataCollection.setResourcePackageId(studyUnit.getResourcePackageId());
			dataCollection.setParent(studyUnit.getId());
			dataCollection.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()"));
			dataCollections.add(dataCollection);
			dataCollection.setChildren(getInstrumentSchemes(child, dataCollection));
		}
		return dataCollections;
	}

	private List<ResponseItem> getInstrumentSchemes(Node node, ResponseItem dataCollection) throws Exception {
		List<ResponseItem> instrumentSchemes = new ArrayList<>();
		String childExp = ".//*[local-name()='InstrumentSchemeReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem instrumentScheme = new ResponseItem();
			instrumentScheme.setId(id);
			instrumentScheme.setParent(dataCollection.getId());
			instrumentScheme.setGroupId(dataCollection.getGroupId());
			instrumentScheme.setSubGroupId(dataCollection.getSubGroupId());
			instrumentScheme.setStudyUnitId(dataCollection.getStudyUnitId());
			instrumentScheme.setDataCollectionId(dataCollection.getId());
			instrumentScheme.setResourcePackageId(dataCollection.getResourcePackageId());
			instrumentSchemes.add(instrumentScheme);
			instrumentScheme.setChildren(getInstruments(child, instrumentScheme));
		}
		return instrumentSchemes;
	}

	private List<ResponseItem> getInstruments(Node node, ResponseItem instrumentScheme) throws Exception {
		List<ResponseItem> instruments = new ArrayList<>();
		String childExp = ".//*[local-name()='InstrumentReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem instrument = new ResponseItem();
			instrument.setId(id);
			instrument.setParent(instrumentScheme.getId());
			instrument.setDataCollectionId(instrumentScheme.getDataCollectionId());
			instrument.setStudyUnitId(instrumentScheme.getStudyUnitId());
			instrument.setSubGroupId(instrumentScheme.getSubGroupId());
			instrument.setGroupId(instrumentScheme.getGroupId());
			instrument.setResourcePackageId(instrumentScheme.getResourcePackageId());
			instrument.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='Instrument']/*[local-name()='Label']/*[local-name()='Content']/text()"));
			instruments.add(instrument);
		}
		return instruments;
	}

	@Override
	public String getDDIDocument(String itemId, String resourcePackageId) throws Exception {
		List<ColecticaItem> items = getItems(getChildrenRef(itemId));
		Map<String, String> refs = items.stream().filter(item -> null != item)
				.collect(Collectors.toMap(ColecticaItem::getIdentifier, item -> {
					try {
						return xpathProcessor.queryString(item.getItem(), "/Fragment/*");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}));
		ResourcePackage resourcePackage = getResourcePackage(resourcePackageId);
		refs.putAll(resourcePackage.getReferences());
		return new DDIDocumentBuilder()
				// .buildResourcePackageDocument(resourcePackage.getId(),
				// resourcePackage.getReferences())
				.buildItemDocument(itemId, refs).build().toString();
	}

	public ResourcePackage getResourcePackage(String id) throws Exception {
		ResourcePackage resourcePackage = new ResourcePackage(id);
		List<ColecticaItem> items = getItems(getChildrenRef(id));
		Map<String, String> refs = items.stream().filter(item -> null != item)
				.collect(Collectors.toMap(ColecticaItem::getIdentifier, item -> {
					try {
						return xpathProcessor.queryString(item.getItem(), "/Fragment/*");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}));
		resourcePackage.setReferences(refs);
		return resourcePackage;
	}

	@Override
	public String getCodeList(String itemId, String ressourcePackageId) throws Exception {
		CodeList codeList = new CodeList();
		List<Code> codes = new ArrayList<Code>();
		Code code;
		CategoryReference categoryReference;
		String fragment = getItem(itemId).item;
		logger.debug("Fragment : " + fragment);

		String childExp = "//*[local-name()='CodeList']";
		Node node = xpathProcessor.queryList(fragment, childExp).item(0);
		NodeList children = xpathProcessor.queryList(node, childExp);

		codeList.setUniversallyUnique(Boolean.valueOf(
				xpathProcessor.queryText(children.item(0), ".//*[local-name()='isUniversallyUnique']/text()")));

		for (int i = 0; i < children.getLength(); i++) {
			codeList.agencyId = xpathProcessor.queryText(children.item(i), ".//*[local-name()='Agency']/text()");
			codeList.version = xpathProcessor.queryText(children.item(i), ".//*[local-name()='Version']/text()");

			codeList.identifier = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			logger.debug("\n");
			logger.debug(codeList.toString());
		}
		
		//Valorisation de la liste de codes
		childExp = "//*[local-name()='Code']";
		node = xpathProcessor.queryList(fragment, childExp).item(0);
		children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			code = new Code();
			code.agencyId = xpathProcessor.queryText(children.item(i), ".//*[local-name()='Agency']/text()");
			code.version = xpathProcessor.queryText(children.item(i), ".//*[local-name()='Version']/text()");
			code.identifier = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			code.setValue(xpathProcessor.queryText(children.item(i), ".//*[local-name()='Value']/text()"));
			
			//Valorisation de la Categorie
			childExp = "//*[local-name()='CategoryReference']";
			node = xpathProcessor.queryList(fragment, childExp).item(0);
			children = xpathProcessor.queryList(node, childExp);
			categoryReference = new CategoryReference();
			categoryReference.agencyId = xpathProcessor.queryText(children.item(i), ".//*[local-name()='Agency']/text()");
			categoryReference.version = xpathProcessor.queryText(children.item(i), ".//*[local-name()='Version']/text()");
			categoryReference.identifier = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			categoryReference.setTypeOfObject(xpathProcessor.queryText(children.item(i), ".//*[local-name()='TypeOfObject']/text()"));
			code.setCategorieReference(categoryReference);
			codes.add(code);
			logger.debug(code.toString());
		}
		codeList.setCodeList(codes);
		//TODO: retourner la CodeList
		return fragment;
	}

	@Override
	public String getSerie(String id, String packageId) throws Exception {
		String fragment = getItem(id).item;
		if (fragment.contains("SubGroup"))
		{
		return getDDIDocument(id,packageId);
		}
		else
		{
			throw new RMeSException(404, "L'identifiant de l'objet demandé n'est pas du bon type", fragment);
		}
	}

	@Override
	public String getOperation(String id, String packageId) throws Exception {
		String fragment = getItem(id).item;
		if (fragment.contains("StudyUnit"))
		{
		return getDDIDocument(id,packageId);
		}
		else
		{
			throw new RMeSException(404, "L'identifiant de l'objet demandé n'est pas du bon type", fragment);
		}
	}

	@Override
	public String getDataCollection(String id, String packageId) throws Exception {
		String fragment = getItem(id).item;
		if (fragment.contains("DataCollection"))
		{
		return getDDIDocument(id,packageId);
		}
		else
		{
			throw new RMeSException(404, "L'identifiant de l'objet demandé n'est pas du bon type", fragment);
		}
	}

	@Override
	public String getQuestionnaire(String id, String packageId) throws Exception {
		String fragment = getItem(id).item;
		if (fragment.contains("Instrument"))
		{
		return getDDIDocument(id,packageId);
		}
		else
		{
			throw new RMeSException(404, "L'identifiant de l'objet demandé n'est pas du bon type", fragment);
		}
	}

	@Override
	public String getSequence(String id, String packageId) throws Exception {
		String fragment = getItem(id).item;
		if (fragment.contains("Sequence"))
		{
		return getDDIDocument(id,packageId);
		}
		else
		{
			throw new RMeSException(404, "L'identifiant de l'objet demandé n'est pas du bon type", fragment);
		}
	}
	
	

}
