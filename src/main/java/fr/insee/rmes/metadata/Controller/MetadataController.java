package fr.insee.rmes.metadata.Controller;


import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meta-data")
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

    @Autowired
    private MetadataService metadataService;

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

    @GetMapping("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Object> getDDIDocument(@PathVariable String id) throws Exception{
        String jsonResultat;
        try {
            jsonResultat = metadataService.getDDIDocument(id);
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
        String jsonResultat;
        try {
            jsonResultat = metadataService.getDDIInstance(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }


    @GetMapping("/colectica-item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
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
    public ResponseEntity<Object> getChildrenRef(String id) throws Exception{
        ColecticaItemRefList jsonResultat;
        try {
            jsonResultat = metadataServiceItem.getChildrenRef(id);
        } catch (Exception e){
            return (ResponseEntity<Object>) ResponseEntity.status(HttpStatus.NOT_FOUND_404);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED_202).body(jsonResultat);
    }

//    @PostMapping("colectica-items")
//    public Response


}
