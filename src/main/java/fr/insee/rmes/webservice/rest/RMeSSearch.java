package fr.insee.rmes.webservice.rest;


import java.util.List;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseSearchItem;
import fr.insee.rmes.search.service.SearchService;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


@Component
@Path("/search")
//@Api(value = "DDI Search")
@OpenAPIDefinition(info = @Info(description = "DDI Search"))
public class RMeSSearch {

	final static Logger log = LogManager.getLogger(RMeSSearch.class);

	@Autowired
	SearchService searchService;

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@Operation(summary = "Search Item", description = "Search the application index for item across types`")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Unexpected error") })
	public List<ResponseSearchItem> search(
			@Parameter(description = "Search only items referring to sub-group id", required = false) @QueryParam("subgroupId") String subgroupId,
			@Parameter(description = "Search only items referring to study-unit id", required = false) @QueryParam("studyUnitId") String studyUnitId,
			@Parameter(description = "Search only items referring to data-collection id", required = false) @QueryParam("dataCollectionId") String dataCollectionId,
			DDIQuery criteria) throws Exception {
		try {
			return searchService.searchByLabel(subgroupId, studyUnitId, dataCollectionId, criteria);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("familles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all groups (familles)", description = "Retrieve all groups")
	public List<DDIItem> getGroups() throws Exception {
		try {
			return searchService.getGroups();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("series")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all sub-group (series)", description = "Retrieve all sub-groups")
	public List<DDIItem> getSubGroups() throws Exception {
		try {
			return searchService.getSubGroups();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("series/{id}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all study-units (operations) for a given sub-group (series)",
			description = "Retrieve all operations with a parent id matching the series id given as a path parameter")
	public List<DDIItem> getStudyUnits(@PathParam(value = "id") String id) throws Exception {
		try {
			return searchService.getStudyUnits(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("/operations")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all study-units (operations) ", description = "Retrieve all operations ")
	public List<DDIItem> getStudyUnits() throws Exception {
		try {
			return searchService.getStudyUnits(null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("context/data-collection/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get data collection context (Sub-group id, StudyUnit id) for a given data collection", description = "Retrieve the context (Sub-group id, StudyUnit id) for a id given as a path parameter")
	public DataCollectionContext getDataCollectionContext(@PathParam(value = "id") String id) throws Exception {
		try {
			return searchService.getDataCollectionContext(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@GET
	@Path("operations/{id}/data-collection")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all data collections for a given operation", description = "Retrieve all data collections with a parent id matching the operation id given as a path parameter")
	public List<DDIItem> getDataCollections(@PathParam(value = "id") String id) throws Exception {
		try {
			return searchService.getDataCollections(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@GET
	@Path("items/{label}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get all items for a given label", description = "Retrieve all items with a matching label")
	public List<ResponseSearchItem> getItemsFromLabel(@PathParam(value = "label") String label,
			@QueryParam(value = "subGroupId") String subGroupId,
			@QueryParam(value = "studyUnitId") String studyUnitId,
			@QueryParam(value = "dataCollectionId") String dataCollectionId) throws Exception {
		try {
			return searchService.searchByLabel(subGroupId, studyUnitId, dataCollectionId, new DDIQuery(label));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}


}