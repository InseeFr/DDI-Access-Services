package fr.insee.rmes.search.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.HighlightParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseSearchItem;

@Repository
public class DDIItemRepositoryImpl implements DDIItemRepository {

	@Value("${fr.insee.rmes.solr.host}")
	private String solrHost;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<DDIItem> findByLabel(String label, String... types) throws Exception {
		//TODO
		return null;
	}

	@Override
	public List<DDIItem> findByLabelInSubGroup(String label, String subgroupId, String... types) throws Exception {
		//TODO
		return null;
	}

	@Override
	public List<DDIItem> getSubGroups() throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='sub-group'",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<DDIItem> getStudyUnits(String subGroupId) throws Exception {
		List<DDIItem> ddiItems;
		try {
			String query = "SELECT * FROM ddi_item WHERE type='study-unit' ";
			if (subGroupId != null) {
				query = query.concat("and subgroupid=?");
				ddiItems = jdbcTemplate.query(query, new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subGroupId);
			} else {
				ddiItems = jdbcTemplate.query(query, new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
			}
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<DDIItem> getDataCollections(String studyUnitId) throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query(
					"SELECT * FROM ddi_item WHERE type='data-collection' and studyunitid=?",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class), studyUnitId);
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
		List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='data-collection' and id=?",
				new BeanPropertyRowMapper<DDIItem>(DDIItem.class), dataCollectionId);
		DataCollectionContext dcContext = new DataCollectionContext();
		dcContext.setDataCollectionId(dataCollectionId);
		dcContext.setOperationId(ddiItems.get(0).getStudyUnitId());
		dcContext.setSerieId(ddiItems.get(0).getSubGroupId());
		return dcContext;
	}

	@Override
	public List<ResponseSearchItem> getItemsByCriteria(String subgroupId, String operationId, String dataCollectionId,
			DDIQuery criteria) throws Exception{
		SolrClient solrClient = new HttpSolrClient.Builder(String.format("http://%s/solr", solrHost)).build();
		SolrQuery query = new SolrQuery();
		
		//Request
		query.set("defType", "edismax");
		query.set(CommonParams.Q, criteria.getLabel());
		query.set(DisMaxParams.QF, "label description");
		query.set(DisMaxParams.BQ, "reusable:true");
		query.set(DisMaxParams.PF, "label description");
		query.set(CommonParams.SORT, "score desc");
		
		//Highlighting
		query.set(HighlightParams.HIGHLIGHT,true);
		query.set(HighlightParams.METHOD, "unified");
		query.set(HighlightParams.FIELDS, "label");
		query.set(HighlightParams.BS_TYPE, "WHOLE");
		
		//Filters
		//First filter on subgroups, studyUnits and dataCollections
		List<String> parentFilterQueries = new ArrayList<>();
		if (subgroupId != null) {
			parentFilterQueries.add(String.format("subGroup:%s", subgroupId));
		}
		if (operationId != null) {
			parentFilterQueries.add(String.format("studyUnit:%s", operationId));
		}
		if (dataCollectionId != null) {
			parentFilterQueries.add(String.format("dataCollection:%s", dataCollectionId));
		}
		if (!parentFilterQueries.isEmpty()) {
			query.addFilterQuery(String.join(" OR ", parentFilterQueries));
		}
		
		//Second filter on type of item
		if(criteria.getType()!=null) {
			query.addFilterQuery(String.format("type:%s", criteria.getType()));
		};
		
		QueryResponse response = null;
		try {
			response = solrClient.query("Test", query);
		} catch (SolrServerException | IOException e1) {
			e1.printStackTrace();
		}
		
		List<ResponseSearchItem> itemsResult = new ArrayList<>();
		SolrDocumentList results = response.getResults();
		for (SolrDocument result : results) {
			String type = getString(result, "type");
			String id = (String) result.getFieldValue("id");
			String labelResult = "";
			if (response.getHighlighting().get(id).get("label").size()>0) {
				labelResult = response.getHighlighting().get(id).get("label").get(0);
			} else {
				labelResult = getString(result, "label");
			}
			List<Long> versions = (List<Long>) result.getFieldValue("version");
			List<String> modalities = getStrings(result,"modalities");
			List<String> subGroups = getStrings(result,"subGroup");
			List<String> subGroupLabels = getStrings(result,"subGroupLabel");
			List<String> studyUnits = getStrings(result,"studyUnit");
			List<String> dataCollections = getStrings(result,"dataCollection");
			if (id != null && labelResult != null && type != null) {
				ResponseSearchItem item = new ResponseSearchItem(id, labelResult);
				item.setVersion(versions.get(0).intValue());
				item.setType(DDIItemType.searchByUUID(type.toUpperCase()).getName());
				item.setDescription(getString(result,"description")); 
				item.setModalities(modalities);
				item.setSubGroups(subGroups);
				item.setSubGroupLabels(subGroupLabels);
				item.setStudyUnits(studyUnits);
				item.setDataCollections(dataCollections);
				itemsResult.add(item);
			}
		}
		return itemsResult;
	}

	@Override
	public void deleteAll() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public DDIItem getItemById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DDIItem> getGroups() throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='group'",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Safely gets a String for the given field of a solrDocument.
	 *
	 * @param solrDocument the document to get the field from
	 * @param field        the field to get
	 * @return the String value of the field
	 */
	public String getString(SolrDocument solrDocument, String field) {
		String returnVal = null;
		final Object object = solrDocument.getFieldValue(field);
		if (object != null) {
			if (object instanceof String) {
				returnVal = (String) object;
			} else if (object instanceof ArrayList) {
				Collection<Object> objects = solrDocument.getFieldValues(field);
				if (!objects.isEmpty()) {
					returnVal = (String) objects.iterator().next();
				}
			} else {
				returnVal = object.toString();
			}
		}
		return returnVal;
	}
	
	public List<String> getStrings(SolrDocument solrDocument, String field) {
		List<String> vals = new ArrayList<>();
		Collection<Object> fieldValues = solrDocument.getFieldValues(field);
		if (fieldValues != null) {
			for (Object val : fieldValues) {
				vals.add((String) val);
			}
		}
		return vals;
	}
}
