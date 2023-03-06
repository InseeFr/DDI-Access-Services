package fr.insee.rmes.metadata.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

@JsonPropertyOrder({
		"Cultures",
		"ItemTypes",
		"LanguageSortOrder",
		"MaxResults",
		"RankResults",
		"ResultOffset",
		"ResultOrdering",
		"SearchDepricatedItems",
		"SearchLatestVersion",
		"SearchTerms"
})
@Generated("jsonschema2pojo")
public class ColecticaSearchItemRequest {
	
	@JsonProperty("Cultures")
	private List<String> cultures = new ArrayList<>();
	@JsonProperty("ItemTypes")
	private List<String> itemTypes = new ArrayList<>();
	@JsonProperty("LanguageSortOrder")
	private List<String> languageSortOrder = new ArrayList<>();
	@JsonProperty("MaxResults")
	private Integer maxResults = 0;
	@JsonProperty("RankResults")
	private Boolean rankResults = true;
	@JsonProperty("ResultOffset")
	private Integer resultOffset = 0;
	@JsonProperty("ResultOrdering")
	private String resultOrdering = "None";
	@JsonProperty("SearchDepricatedItems")
	private Boolean searchDepricatedItems = false;
	@JsonProperty("SearchLatestVersion")
	private Boolean searchLatestVersion = true;
//	@JsonProperty("SearchSets")
//	List<ColecticaItemRef> searchSets = new ArrayList<>();
	@JsonProperty("SearchTerms")
	List<String> searchTerms = new ArrayList<>();

	@JsonCreator
	public ColecticaSearchItemRequest() {
	}

	@JsonProperty("Cultures")
	public List<String> getCultures() {
		return cultures;
	}
	@JsonProperty("Cultures")
	public void setCultures(List<String> cultures) {
		this.cultures = cultures;
	}
	@JsonProperty("ItemTypes")
	public List<String> getItemTypes() {
		return itemTypes;
	}
	@JsonProperty("ItemTypes")
	public void setItemTypes(List<String> itemTypes) {
		this.itemTypes = itemTypes;
	}
	@JsonProperty("LanguageSortOrder")
	public List<String> getLanguageSortOrder() {
		return languageSortOrder;
	}
	@JsonProperty("LanguageSortOrder")
	public void setLanguageSortOrder(List<String> languageSortOrder) {
		this.languageSortOrder = languageSortOrder;
	}
	@JsonProperty("MaxResults")
	public Integer getMaxResults() {
		return maxResults;
	}
	@JsonProperty("MaxResults")
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}
	@JsonProperty("RankResults")
	public Boolean getRankResults() {
		return rankResults;
	}
	@JsonProperty("RankResults")
	public void setRankResults(Boolean rankResults) {
		this.rankResults = rankResults;
	}
	@JsonProperty("ResultOffset")
	public Integer getResultOffset() {
		return resultOffset;
	}
	@JsonProperty("ResultOffset")
	public void setResultOffset(Integer resultOffset) {
		this.resultOffset = resultOffset;
	}
	@JsonProperty("ResultOrdering")
	public String getResultOrdering() {
		return resultOrdering;
	}
	@JsonProperty("ResultOrdering")
	public void setResultOrdering(String resultOrdering) {
		this.resultOrdering = resultOrdering;
	}
	@JsonProperty("SearchDepricatedItems")
	public Boolean getSearchDepricatedItems() {
		return searchDepricatedItems;
	}
	@JsonProperty("SearchDepricatedItems")
	public void setSearchDepricatedItems(Boolean searchDepricatedItems) {this.searchDepricatedItems = searchDepricatedItems;}
	@JsonProperty("SearchLatestVersion")
	public Boolean getSearchLatestVersion() {
		return searchLatestVersion;
	}
	@JsonProperty("SearchLatestVersion")
	public void setSearchLatestVersion(Boolean searchLatestVersion) {
		this.searchLatestVersion = searchLatestVersion;
	}
//	@JsonProperty("SearchSets")
//	public List<ColecticaItemRef> getSearchSets() {
//		return searchSets;
//	}
//	@JsonProperty("SearchSets")
//	public void setSearchSets(List<ColecticaItemRef> searchSets) {
//		this.searchSets = searchSets;
//	}
	@JsonProperty("SearchTerms")
	public List<String> getSearchTerms() {
		return searchTerms;
	}
	@JsonProperty("SearchTerms")
	public void setSearchTerms(List<String> searchTerms) {
		this.searchTerms = searchTerms;
	}


}
