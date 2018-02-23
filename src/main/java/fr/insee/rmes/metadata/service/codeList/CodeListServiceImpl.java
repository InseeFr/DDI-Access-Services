package fr.insee.rmes.metadata.service.codeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.XpathProcessor;

@Service
public class CodeListServiceImpl implements CodeListService {

	private final static Logger logger = LogManager.getLogger(CodeListServiceImpl.class);

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	MetadataService metadataService;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	XpathProcessor xpathProcessor;

	public StringBuilder getFragmentCodeList(StringBuilder res, String fragment, String itemId) throws Exception {
		String fragmentExpresseion = "//*[local-name()='Fragment']/*[local-name()='CodeList']";
		res.append(xpathProcessor.queryText(fragment, fragmentExpresseion));
		return res;
	}

	public TreeMap<Integer, Map<Node, String>> addCategoryScheme(String categoryIdRes, String ressourcePackageId,
			int indexInMap) throws Exception {
		TreeMap<Integer, Map<Node, String>> categoryCustomItems = new TreeMap<Integer, Map<Node, String>>();
//		String categoryScheme = metadataService.getDDIItemWithEnvelope(categoryIdRes, ressourcePackageId,
//				Envelope.CATEGORY_SCHEME);
//		String labelCategory = "//*[local-name()='DDIInstance']/*[local-name()='ResourcePackage']/*[local-name()='CategoryScheme']";
//		NodeList resCategoryScheme = xpathProcessor.queryList(categoryScheme, labelCategory);
//		Node nodeCategoryScheme = resCategoryScheme.item(0);
//		Map<Node, String> mapValue = new HashMap<Node, String>();
//		mapValue.put(nodeCategoryScheme, "g:ResourcePackage");
//		categoryCustomItems.put(indexInMap, mapValue);
//		logger.warn(nodeCategoryScheme.getTextContent());
		return categoryCustomItems;
	}

	public TreeMap<Integer, Map<Node, String>> addCategories(String fragment, String ressourcePackageId,
			TreeMap<Integer, Map<Node, String>> categoryCustomItems) throws Exception {
		String fragmentExp = "//*[local-name()='Fragment']/*[local-name()='CodeList']/*[local-name()='Code']";
		NodeList children = xpathProcessor.queryList(fragment, fragmentExp);
		ColecticaItemRefList refs = new ColecticaItemRefList();
		ColecticaItemRef ref;
		refs.identifiers = new ArrayList<ColecticaItemRef>();
		String categoryIdRes = "";
		for (int i = 1; i < children.getLength() + 1; i++) {
			String labelExp = "//*[local-name()='Code'][" + i
					+ "]/*[local-name()='CategoryReference']/*[local-name()='ID']/text()";
			categoryIdRes = xpathProcessor.queryText(fragment, labelExp);
			ref = new ColecticaItemRef();
			ref.agencyId = "fr.insee";
			ref.identifier = categoryIdRes;
			ref.version = 0;
			refs.identifiers.add(ref);
		}
		List<ColecticaItem> itemsCategories = metadataServiceItem.getItems(refs);
		int index = 1;
		for (ColecticaItem colecticaItem : itemsCategories) {
			if (index == 1) {
				categoryCustomItems = addCategoryScheme(colecticaItem.getIdentifier(), ressourcePackageId, index);
			} else {
				categoryCustomItems = addCategoryById(categoryCustomItems, colecticaItem, index);
			}
			index++;
		}

		return categoryCustomItems;
	}

	@Override
	public TreeMap<Integer, Map<Node, String>> addCategoryById(TreeMap<Integer, Map<Node, String>> categoryCustomItems,
			ColecticaItem itemCategory, int indexInMap) throws Exception {
		String categoryFragment = itemCategory.getItem();
		NodeList nodelistCategory = xpathProcessor.queryList(categoryFragment,
				"//*[local-name()='Fragment']/*[local-name()='Category']");
		Node nodeCategory;
		nodeCategory = nodelistCategory.item(0);
		Map<Node, String> mapValue = new HashMap<Node, String>();
		mapValue.put(nodeCategory, "l:CategoryScheme");
		categoryCustomItems.put(indexInMap, mapValue);
		logger.warn(nodeCategory.getTextContent());
		return categoryCustomItems;
	}

	@Override
	public String getCodeList(String itemId, String ressourcePackageId) throws Exception {
		String fragment = "";
//		try {
//			TreeMap<Integer, Map<Node, String>> categoryCustomItems = new TreeMap<Integer, Map<Node, String>>();
//			fragment = metadataServiceItem.getItem(itemId).item;
//			StringBuilder resRootFragment = new StringBuilder();
//			resRootFragment = getFragmentCodeList(resRootFragment, fragment, itemId);
//
//			if (!(resRootFragment.length() == 0)) {
//				resRootFragment = new StringBuilder();
//				categoryCustomItems = addCategories(fragment, ressourcePackageId, categoryCustomItems);
//				resRootFragment.append(metadataService.getDDIItemWithEnvelopeAndCustomItems(itemId, ressourcePackageId,
//						Envelope.CODE_LIST_SCHEME, categoryCustomItems));
//				return resRootFragment.toString();
//			} else {
//				throw new RMeSException(404, "The type of this item isn't a CodeList.", fragment);
//			}
//		} catch (Exception e) {
//			throw new RMeSException(404, "This item is not in the Colectica database.", fragment);
//		}
		return fragment;
	}

}
