package fr.insee.rmes.metadata.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.utils.DocumentBuilderUtils;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.model.ResourcePackage;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.search.service.SearchService;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;

@Service
public class MetadataServiceImpl implements MetadataService {

	private final static Logger logger = LogManager.getLogger(MetadataServiceImpl.class);

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	SearchService searchService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	XpathProcessor xpathProcessor;

	@Override
	public List<Unit> getUnits() throws Exception {
		return metadataRepository.getUnits();
	}

	public ResponseItem getDDIRoot(String id) throws Exception {
		ResponseItem ddiRoot = new ResponseItem();
		String fragment = metadataServiceItem.getItem(id).item;
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

	public List<ResponseItem> getDDICodeListSchemeFromResourcePackage(String idRP) throws Exception {
		String fragment = metadataServiceItem.getItem(idRP).item;
		String rootExp = "//*[local-name()='Fragment']";
		Node rootNode = xpathProcessor.queryList(fragment, rootExp).item(0);
		List<ResponseItem> clsList = new ArrayList<>();
		String childExp = ".//*[local-name()='CodeListSchemeReference']";
		NodeList children = xpathProcessor.queryList(rootNode, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			fragment = metadataServiceItem.getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem cls = new ResponseItem();
			cls.setId(id);
			cls.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='CodeListScheme']/*[local-name()='Label']/*[local-name()='Title']/*[local-name()='String']/text()"));
			cls.setParent(idRP);
			cls.setResourcePackageId(idRP);
			cls.setChildren(getCodeListResponseItem(child, cls));
			clsList.add(cls);
			logger.debug("CodeListScheme : " + id);
		}
		return clsList;
	}

	public List<ResponseItem> getDDICodeListSchemeFromGroupRoot(String idGroupRoot) throws Exception {
		List<ResponseItem> clsList = new ArrayList<>();
		logger.debug("GroupRoot id : " + idGroupRoot);
		String fragment = metadataServiceItem.getItem(idGroupRoot).item;
		String rootExp = "//*[local-name()='Fragment']";
		Node rootNode = xpathProcessor.queryList(fragment, rootExp).item(0);
		String childGroupExp = ".//*[local-name()='GroupReference']/*[local-name()='ID']/text()";
		String idGroup = xpathProcessor.queryString(rootNode, childGroupExp);
		logger.debug("Group id : " + idGroup);
		fragment = metadataServiceItem.getItem(idGroup).item;
		Node groupNode = xpathProcessor.queryList(fragment, rootExp).item(0);
		String childSubGroupExp = ".//*[local-name()='SubGroupReference']/*[local-name()='ID']/text()";
		String idSubGroup = xpathProcessor.queryString(groupNode, childSubGroupExp);
		logger.debug("SubGroup id : " + idSubGroup);
		String childExp = ".//*[local-name()='ResourcePackageReference']";
		NodeList children = xpathProcessor.queryList(rootNode, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String idRP = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			fragment = metadataServiceItem.getItem(idRP).item;
			rootExp = "//*[local-name()='Fragment']";
			rootNode = xpathProcessor.queryList(fragment, rootExp).item(0);
			childExp = ".//*[local-name()='CodeListSchemeReference']";
			children = xpathProcessor.queryList(rootNode, childExp);
			for (int j = 0; j < children.getLength(); j++) {
				String id = xpathProcessor.queryString(children.item(j), ".//*[local-name()='ID']/text()");
				fragment = metadataServiceItem.getItem(id).item;
				Node child = xpathProcessor.toDocument(fragment);
				ResponseItem cls = new ResponseItem();
				cls.setId(id);
				// cls.setLabel(xpathProcessor.queryText(child,
				// ".//*[local-name()='CodeListScheme']/*[local-name()='Label']/*[local-name()='Title']/*[local-name()='String']/text()"));
				cls.setParent(idRP);
				cls.setResourcePackageId(idRP);
				cls.setSubGroupId(idSubGroup);
				cls.setChildren(getCodeListResponseItem(child, cls));
				clsList.add(cls);
				logger.debug("CodeListScheme : " + id);
			}

		}
		return clsList;
	}

	private List<ResponseItem> getCodeListResponseItem(Node node, ResponseItem cls) throws Exception {
		List<ResponseItem> clList = new ArrayList<>();
		String childExp = ".//*[local-name()='CodeListReference']";
		NodeList children = xpathProcessor.queryList(node, childExp);
		for (int i = 0; i < children.getLength(); i++) {
			String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
			String fragment = metadataServiceItem.getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem cl = new ResponseItem();
			cl.setId(id);
			cl.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='Label']/*[local-name()='Content']/text()"));
			cl.setParent(cls.getId());
			cl.setResourcePackageId(cls.getResourcePackageId());
			cl.setSubGroupId(cls.getSubGroupId());
			clList.add(cl);
		}
		return clList;
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
			String fragment = metadataServiceItem.getItem(id).item;
			Node child = xpathProcessor.toDocument(fragment);
			ResponseItem group = new ResponseItem();
			group.setId(id);
			group.setLabel(xpathProcessor.queryText(child,
					".//*[local-name()='Group']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
			group.setParent(ddiRoot.getId());
			group.setResourcePackageId(ddiRoot.getResourcePackageId());
			group.setGroupId(id);
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
			String fragment = metadataServiceItem.getItem(id).item;
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
			String fragment = metadataServiceItem.getItem(id).item;
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
			String fragment = metadataServiceItem.getItem(id).item;
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
			String fragment = metadataServiceItem.getItem(id).item;
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
			String fragment = metadataServiceItem.getItem(id).item;
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
			instrument.setName(
					xpathProcessor.queryText(child, ".//*[local-name()='Instrument']/*[local-name()='UserID']/text()"));
			instruments.add(instrument);
		}
		return instruments;
	}

	@Override
	public String getDerefDDIDocumentWithExternalRP(String itemId, String resourcePackageId) throws Exception {
		Map<String, String> refs = getChildrenRefs(itemId);
		ResourcePackage resourcePackage = getResourcePackage(resourcePackageId);
		refs.putAll(resourcePackage.getReferences());
		return new DDIDocumentBuilder()
				// .buildResourcePackageDocument(resourcePackage.getId(),
				// resourcePackage.getReferences())
				.buildItemDocument(itemId, refs).build().toString();
	}

	@Override
	public String getDerefDDIDocument(String itemId) throws Exception {
		Map<String, String> refs = getChildrenRefs(itemId);
		return new DDIDocumentBuilder().buildItemDocument(itemId, refs).build().toString();
	}

	private Map<String, String> getChildrenRefs(String itemId) throws Exception {
		List<ColecticaItem> items = metadataServiceItem.getItems(metadataServiceItem.getChildrenRef(itemId));
		Map<String, String> refs = items.stream().filter(item -> null != item)
				.collect(Collectors.toMap(ColecticaItem::getIdentifier, item -> {
					try {
						return xpathProcessor.queryString(item.getItem(), "/Fragment/*");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}));
		return refs;
	}

	public ResourcePackage getResourcePackage(String id) throws Exception {
		ResourcePackage resourcePackage = new ResourcePackage(id);
		Map<String, String> refs = getChildrenRefs(id);
		resourcePackage.setReferences(refs);
		return resourcePackage;
	}

	@Override
	public List<String> getGroupIds() throws Exception {
		return groupRepository.getRootIds();
	}

	@Override
	public List<String> getRessourcePackageIds() throws Exception {
		return groupRepository.getRessourcePackageIds();
	}

	@Override
	public String getDDIDocument(String itemId) throws Exception {
		// TODO Auto-generated method stub
		return "Test";
	}

	@Override
	public String getDDIInstance(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSequence(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestion(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relationship[] getRelationship(ObjectColecticaPost relationshipPost) throws Exception {
		return metadataRepository.getRelationship(relationshipPost);
	}

	@Override
	public Relationship[] getRelationshipChildren(ObjectColecticaPost relationshipPost) throws Exception {
		return metadataRepository.getRelationshipChildren(relationshipPost);
	}

	

}