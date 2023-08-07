package fr.insee.rmes.ToColecticaApi.controller;

import fr.insee.rmes.search.controller.ElasticsearchController;
import fr.insee.rmes.webservice.rest.RMeSException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/getItem")
@Tag(name = "Get item from colectica repository")
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
public class GetItem {
    final static Logger logger = LogManager.getLogger(GetItem.class);

    @GetMapping("Item/{id}/ddi")
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Get DDI document", description = "Get a DDI document from Colectica repository including an item thanks to its {id} and its children as fragments.")
    public Response getDDIDocumentFragmentInstance(@PathVariable String id,
                                                   @RequestParam(value="withChild") boolean withChild) throws Exception {
        try {


            StreamingOutput stream = output -> {
                try {

                } catch (Exception e) {
                    throw new RMeSException(500, "Transformation error", e.getMessage());
                }
            };
            return Response.ok(stream).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
