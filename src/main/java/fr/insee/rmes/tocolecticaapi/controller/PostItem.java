package fr.insee.rmes.tocolecticaapi.controller;


import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/postItem")
@Tag(name = "DEMO-Colectica", description = "Services for upgrade Colectica-API")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "406", description = "Not Acceptable"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
public class PostItem {

    private final ColecticaService colecticaService;

    @Autowired
    public PostItem(ColecticaService colecticaService) {
        this.colecticaService = colecticaService;
    }


    @PostMapping("/UpdateToColecticaRepository/{transactionType}")
    @Operation(summary = "Send an update to Colectica Repository via Colectica API ",
            description = "Send a json make with /replace-xml-parameters/{Type}/{Label}/{Version}/{Name}/{VersionResponsibility} COPYCOMMIT is for Upload a new object not for update")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> sendUpdateColectica(
            @RequestBody String ddiUpdatingInJson,
            @PathVariable("transactionType") TransactionType transactionType) throws RmesException {
        colecticaService.sendUpdateColectica(ddiUpdatingInJson, transactionType);
        return ResponseEntity.ok("Transaction success");
    }


    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Send suggester JSON to Colectica Repository via Colectica API",
            description = "Send a suggester JSON to /api/v1/item. This suggester must be simple, a list of Id,Label transform with transformJsonToJsonForAPi")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<Void> uploadItem(@RequestParam("file") MultipartFile file)
            throws IOException, RmesException {
        String fileContent = new String(file.getBytes());
        colecticaService.sendUpdateColectica(fileContent, TransactionType.COPYCOMMIT);
        return ResponseEntity.noContent().build();
    }


    @Hidden
    @PostMapping(value = "{type}/json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get JSON for a type of DDI item", description = "Get a JSON list of item for a type of DDI items .")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> byType(
            @PathVariable("type") DDIItemType type)
            throws IOException, ExceptionColecticaUnreachable{

        return ResponseEntity.ok(colecticaService.getByType(type));
    }
}
