package fr.insee.rmes.search.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import fr.insee.rmes.config.DDIItemRepositoryImplCondition;
import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.search.model.ResponseSearchItem;

@Repository
@Conditional(value = DDIItemRepositoryImplCondition.class)
public class DDIItemRepositoryImpl implements DDIItemRepository {

	@Value("${fr.insee.rmes.solr.host}")
	private String solrHost;

	@Autowired
	RestHighLevelClient client;

	@Override
	public IndexResponse save(String type, ResponseItem item) throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		byte[] data = mapper.writeValueAsBytes(item);
//		IndexRequest request = new IndexRequest(index, type, item.getId()).source(data, XContentType.JSON);
//		return client.index(request);
		return null;
	}

	@Override
	public List<DDIItem> findByLabel(String label, String... types) throws Exception {
//		SearchSourceBuilder srcBuilder = new SearchSourceBuilder()
//				.query(QueryBuilders.fuzzyQuery("label", label).maxExpansions(1).prefixLength(3));
//		SearchRequest request = new SearchRequest().indices(index).types(types).source(srcBuilder);
//		return mapResponse(client.search(request));
		return null;
	}

	@Override
	public List<DDIItem> findByLabelInSubGroup(String label, String subgroupId, String... types) throws Exception {
//		SearchSourceBuilder srcBuilder = new SearchSourceBuilder()
//				.query(QueryBuilders.boolQuery()
//						.filter(QueryBuilders.fuzzyQuery("label", label).maxExpansions(1)
//								.prefixLength(label.length() - 2))
//						.filter(QueryBuilders.termQuery("subGroupId.keyword", subgroupId)));
//		SearchRequest request = new SearchRequest().indices(index).types(types).source(srcBuilder);
//		return mapResponse(client.search(request));
		return null;
	}

	@Override
	public List<DDIItem> getSubGroups() throws Exception {
//		SearchRequest request = new SearchRequest().indices(index).types("sub-group");
//		return mapResponse(client.search(request));
		return null;
	}

	@Override
	public List<DDIItem> getStudyUnits(String subgGroupId) throws Exception {
//		SearchRequest request = new SearchRequest().indices(index).types("study-unit");
//		if (subgGroupId != null) {
//			SearchSourceBuilder srcBuilder = new SearchSourceBuilder()
//					.query(QueryBuilders.termQuery("parent", subgGroupId));
//			request.source(srcBuilder);
//		}
//		return mapResponse(client.search(request));
		return null;
	}

	@Override
	public List<DDIItem> getDataCollections(String studyUnitId) throws Exception {
//		SearchSourceBuilder srcBuilder = new SearchSourceBuilder()
//				.query(QueryBuilders.termQuery("parent", studyUnitId));
//		SearchRequest request = new SearchRequest().indices(index).types("data-collection").source(srcBuilder);
//		return mapResponse(client.search(request));
		return null;
	}

	@Override
	public DeleteResponse delete(String type, String id) throws Exception {
//		DeleteRequest request = new DeleteRequest(index, type, id);
//		return client.delete(request);
		return null;
	}

	private List<DDIItem> mapResponse(SearchResponse response) {
		List<SearchHit> esHits = Arrays.asList(response.getHits().getHits());
		return esHits.stream().map(hit -> {
			DDIItem item = new DDIItem(hit.getId(), hit.getSource().get("label").toString(),
					hit.getSource().get("parent").toString(), hit.getType());
			item.setGroupId(getHitValueOrNull(hit, "groupId"));
			item.setSubGroupId(getHitValueOrNull(hit, "subGroupId"));
			item.setStudyUnitId(getHitValueOrNull(hit, "studyUnitId"));
			item.setDataCollectionId(getHitValueOrNull(hit, "dataCollectionId"));
			item.setResourcePackageId(getHitValueOrNull(hit, "resourcePackageId"));
			return item;
		}).collect(Collectors.toList());
	}

	private String getHitValueOrNull(SearchHit hit, String field) {
		if (null == hit.getSource().get(field)) {
			return null;
		}
		return hit.getSource().get(field).toString();
	}

	@Override
	public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
		// TODO
		return null;
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
//		SearchRequest request = new SearchRequest().indices(index).types("group");
//		return mapResponse(client.search(request));
		return null;
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
