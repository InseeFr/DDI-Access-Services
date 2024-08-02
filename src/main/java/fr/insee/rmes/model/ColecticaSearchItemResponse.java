package fr.insee.rmes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;
import java.util.List;


@JsonPropertyOrder({
		"Results",
		"TotalResults",
		"ReturnedResults",
		"DatabaseTime",
		"RepositoryTime"
})
@Generated("jsonschema2pojo")
public class ColecticaSearchItemResponse {
	
	@JsonProperty("Results")
	private List<ColecticaSearchItemResult> results;
	@JsonProperty("TotalResults")
	private Integer totalResults;
	@JsonProperty("ReturnedResults")
	private Integer returnedResults;
	@JsonProperty("DatabaseTime")
	private String databaseTime;
	@JsonProperty("RepositoryTime")
	private String repositoryTime;
	@JsonCreator
	public ColecticaSearchItemResponse() {
	}
	public List<ColecticaSearchItemResult> getResults() {
		return results;
	}
	public void setResults(List<ColecticaSearchItemResult> results) {
		this.results = results;
	}
	public Integer getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
	public Integer getReturnedResults() {
		return returnedResults;
	}
	public void setReturnedResults(Integer returnedResults) {
		this.returnedResults = returnedResults;
	}
	public String getDatabaseTime() {
		return databaseTime;
	}
	public void setDatabaseTime(String databaseTime) {
		this.databaseTime = databaseTime;
	}
	public String getRepositoryTime() {
		return repositoryTime;
	}
	public void setRepositoryTime(String repositoryTime) {
		this.repositoryTime = repositoryTime;
	}

}
