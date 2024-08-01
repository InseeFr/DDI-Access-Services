package fr.insee.rmes.metadata.controller;

import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.model.Unit;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    static final Logger log = LogManager.getLogger(MetadataController.class);
    @Autowired
    private MetadataService metadataService;
    @GetMapping("/units")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Unit> getUnits() throws Exception{
        try {
            return metadataService.getUnits();
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw e;        }
    }
}
