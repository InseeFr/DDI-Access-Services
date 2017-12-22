package fr.insee.rmes.metadata.service.codeList;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.ColecticaItem;

public interface CodeListService {

	/**
	 * Get the fragment of a Codelist
	 * 
	 * @param res
	 *            : variable to save the CodeList Fragment
	 * @param fragment
	 *            : xml document of the main fragment
	 * @param itemId
	 *            : id of the codeList
	 * @return StringBuilder res (modified)
	 * @throws Exception
	 */
	StringBuilder getFragmentCodeList(StringBuilder res, String fragment, String itemId) throws Exception;

	/**
	 * Add a categoryScheme to a new Map to store the customItems
	 * 
	 * @param categoryIdRes
	 *            : id of the first Category
	 * @param ressourcePackageId
	 *            : id of the ressourcePackage
	 * @param indexInMap
	 *            : position in the main Map (generally 1)
	 * @return TreeMap<Integer, Map<Node, String>> New Map of custom items
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategoryScheme(String categoryIdRes, String ressourcePackageId,
			int indexInMap) throws Exception;

	

	/**
	 * Add all the categories to a new Main Map (including the CategoryScheme)
	 * 
	 * @param fragment
	 *            : fragment without the categories
	 * @param ressourcePackageId
	 * @param categoryCustomItems
	 *            : target main Map
	 * @return TreeMap<Integer, Map<Node, String>> Map with the Categories.
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategories(String fragment, String ressourcePackageId,
			TreeMap<Integer, Map<Node, String>> categoryCustomItems) throws Exception;

	/**
	 * Add a category from a ColecticaItemCategory
	 * @param categoryCustomItems
	 * @param itemCategory
	 * @param indexInMap
	 * @return TreeMap<Integer, Map<Node, String>>
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategoryById(TreeMap<Integer, Map<Node, String>> categoryCustomItems,
			ColecticaItem itemCategory, int indexInMap) throws Exception;

	
	/**
	 * Get a List of codes with their id, agency and versions
	 * 
	 * @param id
	 *            : id CodeListReference
	 * @param String
	 *            packageId : id of sourcePackage
	 * @return Fragments
	 * @throws Exception
	 */
	String getCodeList(String itemId, String ressourcePackageId) throws Exception;
}
