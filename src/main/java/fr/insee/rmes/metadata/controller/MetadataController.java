package fr.insee.rmes.metadata.controller;


import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.RelationshipOut;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import fr.insee.rmes.metadata.service.fragmentInstance.FragmentInstanceService;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.webservice.rest.RMeSException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    final static Logger log = LogManager.getLogger(MetadataController.class);

    @Autowired
    private MetadataService metadataService;

    @Autowired
    FragmentInstanceService fragmentInstanceService;
    @Autowired
    DDIInstanceService ddiInstanceService;

    @Autowired
    private MetadataServiceItem metadataServiceItem;

    @Hidden
    @GetMapping("/question/{uuid}/ddi")
    @Produces(MediaType.APPLICATION_JSON)
    public String  getQuestion(@PathVariable String uuid) throws Exception{
        try {
            return metadataService.getQuestion(uuid);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;        }
    }

    @GetMapping("/units")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Unit> getUnits() throws Exception{
        try {
            return metadataService.getUnits();
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;        }
    }
    @Hidden
    @GetMapping("/ddi-instance/{uuid}/ddi")
    @Produces(MediaType.APPLICATION_JSON)
    public  String getDDIInstance(@PathVariable String uuid) throws Exception{
        try {
            return ddiInstanceService.getDDIInstance(uuid);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;        }
    }

    @Hidden
    @GetMapping("/colectica-item/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the item with uuid {uuid}", description = "Get an item from Colectica Repository, given it's {id}")
    public  ColecticaItem getItemDDI(@PathVariable String uuid) throws Exception{
        try {
            return metadataServiceItem.getItem(uuid);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;       }
    }


    @GetMapping("/colectica-item/{uuid}/refs")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get the colectica item children refs with parent uuid {uuid}", description = "This will give a list of object containing a reference id, version and agency. Note that you will"
            + "need to map response objects keys to be able to use it for querying items "
            + "(see /items doc model)")
    public ColecticaItemRefList getChildrenRef(String uuid) throws Exception{
        try {
            return metadataServiceItem.getChildrenRef(uuid);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;        }
    }
    @Hidden
    @GetMapping("colectica-items/{itemType}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all referenced items of a certain type", description = "Retrieve a list of ColecticaItem of the type defined")
    public List<ColecticaItem> getItemsByType(@RequestParam DDIItemType ddiItemType)
            throws Exception {
        try {
            return metadataService.getItemsByType(ddiItemType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    @Hidden
    @GetMapping("colectica-item/{uuid}/toplevel-refs/")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the colectica item toplevel parents refs with item uuid {uuid}", description = "This will give a list of object containing a triple identifier (reference id, version and agency) and the itemtype. Note that you will"
            + "need to map response objects keys to be able to use it for querying items "
            + "(see /items doc model)")
    public Response gettopLevelRefs(@PathVariable String uuid) throws Exception {
        try {
            List<RelationshipOut> refs = metadataServiceItem.getTopLevelRefs(uuid);
            return Response.ok().entity(refs).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    @Hidden
    @GetMapping("fragmentInstance/{uuid}/ddi")
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Get DDI document", description = "Get a DDI document from Colectica repository including an item thanks to its {uuid} and its children as fragments.")
    public Response getDDIDocumentFragmentInstance(@PathVariable String uuid,
                                                   @RequestParam(value="withChild") boolean withChild) throws Exception {
        try {
            String ddiDocument = fragmentInstanceService.getFragmentInstance(uuid, null, withChild);
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
