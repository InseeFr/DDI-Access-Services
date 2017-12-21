package fr.insee.rmes.metadata.service.codeList;

import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Node;

public interface CodeListService {

	/**
	 * Get the fragment of a Codelist 
	 * @param res : variable to save the CodeList Fragment
	 * @param fragment : xml document of the main fragment
	 * @param itemId : id of the codeList
	 * @return StringBuilder res (modified)
	 * @throws Exception
	 */
	StringBuilder getFragmentCodeList(StringBuilder res, String fragment, String itemId) throws Exception;

	/**
	 * Add a categoryScheme to a new Map to store the customItems
	 * @param categoryIdRes : id of the first Category
	 * @param ressourcePackageId : id of the ressourcePackage
	 * @param indexInMap : position in the main Map (generally 1)
	 * @return TreeMap<Integer, Map<Node, String>> New Map of custom items
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategoryScheme(String categoryIdRes, String ressourcePackageId,
			int indexInMap) throws Exception;
	/**
	 * Add a category to an existing main Map of custom items
	 * @param categoryCustomItems : target main Map
	 * @param categoryIdRes : id of the new Category
	 * @param indexInMap : position in the main Map (generally >1)
	 * @return TreeMap<Integer, Map<Node, String>> Map with the new Category
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategoryById(TreeMap<Integer, Map<Node, String>> categoryCustomItems,
			String categoryIdRes, int indexInMap) throws Exception;
	/**
	 * Add all the categories to a new Main Map (including the CategoryScheme)
	 * @param fragment : fragment without the categories
	 * @param ressourcePackageId
	 * @param categoryCustomItems : target main Map
	 * @return TreeMap<Integer, Map<Node, String>> Map with the Categories.
	 * @throws Exception
	 */
	TreeMap<Integer, Map<Node, String>> addCategories(String fragment, String ressourcePackageId,
			TreeMap<Integer, Map<Node, String>> categoryCustomItems) throws Exception;

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
	String getCodeList(String id, String packageId) throws Exception;
}
