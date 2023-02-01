package fr.insee.rmes.metadata.Controller;


import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import fr.insee.rmes.metadata.service.fragmentInstance.FragmentInstanceService;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.webservice.rest.RMeSException;
import fr.insee.rmes.webservice.rest.RMeSMetadata;
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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meta-data")
@Tag(name = "DDI MetaData API" )
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
public class MetadataController {

    final static Logger log = LogManager.getLogger(RMeSMetadata.class);

    @Autowired
    private MetadataService metadataService;

    @Autowired
    FragmentInstanceService fragmentInstanceService;
    @Autowired
    DDIInstanceService ddiInstanceService;

    @Autowired
    private MetadataServiceItem metadataServiceItem;

    @GetMapping("/item/{id}/rp/{resourcePackageId}/deref-ddi")
    @Operation(summary = "Get Deref DDI document")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getDerefDDIDocumentWithExternalRP(@PathVariable String id, @PathVariable String resourcePackageId) throws Exception{
        String jsonResultat;
        try {
            jsonResultat = metadataService.getDerefDDIDocumentWithExternalRP(id,resourcePackageId);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }


    @GetMapping("/item/{id}/deref-ddi")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getDerefDDIDocument(@PathVariable String id) throws Exception{
        String jsonResultat;
        try {
            jsonResultat = metadataService.getDerefDDIDocument(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }


    @GetMapping("/sequence/{id}/ddi")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getSequence(@PathVariable String id) throws Exception{
        String jsonResultat;
        try {
            jsonResultat = metadataService.getSequence(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }


    @GetMapping("/question/{id}/ddi")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object>  getQuestion(@PathVariable String id) throws Exception{
        String jsonResultat;
        try {
            jsonResultat = metadataService.getQuestion(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }

    @GetMapping("/units")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getUnits() throws Exception{
        List<Unit> jsonResultat;
        try {
            jsonResultat = metadataService.getUnits();
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }

    @GetMapping("/ddi-instance/{id}/ddi")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getDDIInstance(@PathVariable String id) throws Exception{
        String questionnaire;
        try {
            questionnaire = ddiInstanceService.getDDIInstance(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(questionnaire);
    }


    @GetMapping("/colectica-item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the item with id {id}", description = "Get an item from Colectica Repository, given it's {id}")
    public ResponseEntity<Object> getItem(@PathVariable String id) throws Exception{
        ColecticaItem jsonResultat;
        try {
            jsonResultat = metadataServiceItem.getItem(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }

    @GetMapping("/colectica-item/{id}/refs")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the colectica item children refs with parent id {id}", description = "This will give a list of object containing a reference id, version and agency. Note that you will"
            + "need to map response objects keys to be able to use it for querying items "
            + "(see /items doc model)")
    public ResponseEntity<Object> getChildrenRef(String id) throws Exception{
        ColecticaItemRefList jsonResultat;
        try {
            jsonResultat = metadataServiceItem.getChildrenRef(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }

    @GetMapping("colectica-items/{itemType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all referenced items of a certain type", description = "Retrieve a list of ColecticaItem of the type defined")
    public ResponseEntity<Object> getItemsByType(@PathVariable DDIItemType itemType)
            throws Exception {
        try {
            List<ColecticaItem> children = metadataService.getItemsByType(itemType);
            return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(children);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("variables/{idQuestion}/ddi")
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Get the variables that references a specific question")
    public Response getVariablesFromQuestion(@PathParam(value = "idQuestion") String idQuestion,
                                             @QueryParam(value="agency") String agency,
                                             @QueryParam(value="version") String version) throws Exception {
        Map<String,String> params = new HashMap<String,String>();
        params.put("idQuestion",idQuestion);
        params.put("agency",agency);
        params.put("version",version);
        try {
            String ddiDocument = metadataService.getVariablesFromQuestionId(params);
            StreamingOutput stream = output -> {
                try {
                    output.write(ddiDocument.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    throw new RMeSException(500, "Transformation error", e.getMessage());
                }
            };
            return Response.ok(stream).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }





    @PostMapping("colectica-items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all de-referenced items", description = "Maps a list of ColecticaItemRef given as a payload to a list of actual full ColecticaItem objects")
    public Response getItems(
            @Parameter(description = "Item references query object", required = true) ColecticaItemRefList query)
            throws Exception {
        try {
            List<ColecticaItem> children = metadataServiceItem.getItems(query);
            return Response.ok().entity(children).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("fragmentInstance/{id}/ddi")
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Get DDI document", description = "Get a DDI document from Colectica repository including an item thanks to its {id} and its children as fragments.")
    public Response getDDIDocumentFragmentInstance(@PathParam(value = "id") String id,
                                                   @QueryParam(value="withChild") boolean withChild) throws Exception {
        try {
            String ddiDocument = fragmentInstanceService.getFragmentInstance(id, null, withChild);
            StreamingOutput stream = output -> {
                try {
                    output.write(ddiDocument.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    throw new RMeSException(500, "Transformation error", e.getMessage());
                }
            };
            return Response.ok(stream).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }





}
