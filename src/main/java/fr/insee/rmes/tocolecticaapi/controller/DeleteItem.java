package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

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

    final static Logger logger = LogManager.getLogger(DeleteItem.class);

    private final ColecticaService colecticaService;

    private final RestTemplate restTemplate;

    @Autowired
    public DeleteItem(ColecticaService colecticaService, RestTemplate restTemplate) {
        this.colecticaService = colecticaService;
        this.restTemplate = restTemplate;
    }

    @DeleteMapping(value = "/deleteCodeList")
    @Operation(summary = "Delete a CodeList via Colectica API",
            description = "Delete a CodeList and all their children.")
    public String deleteCodeList(@RequestParam("uuid") String uuid)
            throws IOException, ExceptionColecticaUnreachable, ParseException {
        return colecticaService.sendDeleteColectica(uuid, TransactionType.COPYCOMMIT);
    }

}