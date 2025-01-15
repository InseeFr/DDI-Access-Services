package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;


@Controller
@RequestMapping("/postItem")
@Tag(name= "DEMO-Colectica",description = "Services for upgrade Colectica-API")
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
public class DeleteItem {

    private final ColecticaService colecticaService;

    @Autowired
    public DeleteItem(ColecticaService colecticaService) {
        this.colecticaService = colecticaService;
    }

    @DeleteMapping(value = "/deleteCodeList")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(summary = "Delete a CodeList via Colectica API",
            description = "Delete a CodeList and all their children.")
    public ResponseEntity<Void> deleteCodeList(@RequestParam("uuid") String uuid)
            throws IOException, ExceptionColecticaUnreachable, RmesException {
        colecticaService.sendDeleteColectica(uuid, TransactionType.COPYCOMMIT);
        return ResponseEntity.noContent().build();
    }

}