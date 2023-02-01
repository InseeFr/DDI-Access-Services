package fr.insee.rmes.metadata.Controller;

import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseSearchItem;
import fr.insee.rmes.search.service.SearchService;
import fr.insee.rmes.webservice.rest.RMeSSearch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/search")
@Tag(name = "DDI Search")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "406", description = "Not Acceptable"),
        @ApiResponse(responseCode = "500", description = "Internal server error") })
public class DDISearch {

    final static Logger log = LogManager.getLogger(RMeSSearch.class);

    @Autowired
    SearchService searchService;


    @PostMapping
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


    @GetMapping("/familles")
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

    @GetMapping("/series")
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


    @GetMapping("series/{id}/operations")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all study-units (operations) for a given sub-group (series)",
            description = "Retrieve all operations with a parent id matching the series id given as a path parameter")
    public List<DDIItem> getStudyUnits(@PathVariable String id) throws Exception {
        try {
            return searchService.getStudyUnits(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/operations")
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

    @GetMapping("context/data-collection/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get data collection context (Sub-group id, StudyUnit id) for a given data collection", description = "Retrieve the context (Sub-group id, StudyUnit id) for a id given as a path parameter")
    public DataCollectionContext getDataCollectionContext(@PathVariable String id) throws Exception {
        try {
            return searchService.getDataCollectionContext(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("operations/{id}/data-collection")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all data collections for a given operation", description = "Retrieve all data collections with a parent id matching the operation id given as a path parameter")
    public List<DDIItem> getDataCollections(@PathVariable String id) throws Exception {
        try {
            return searchService.getDataCollections(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

//    @GetMapping("items/{label}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Operation(summary = "Get all items for a given label", description = "Retrieve all items with a matching label")
//    public List<ResponseSearchItem> getItemsFromLabel(@PathParam(value = "label") String label,
//                                                      @QueryParam(value = "subGroupId") String subGroupId,
//                                                      @QueryParam(value = "studyUnitId") String studyUnitId,
//                                                      @QueryParam(value = "dataCollectionId") String dataCollectionId) throws Exception {
//        try {
//            return searchService.searchByLabel(subGroupId, studyUnitId, dataCollectionId, new DDIQuery(label));
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            throw e;
//        }
//    }
}
