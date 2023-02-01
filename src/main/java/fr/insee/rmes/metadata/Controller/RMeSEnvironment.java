package fr.insee.rmes.metadata.Controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "RMeS Environment")
@RestController("/env")
public class RMeSEnvironment {

    private final static Logger log = LogManager.getLogger(fr.insee.rmes.webservice.rest.RMeSEnvironment.class);

    @Autowired
    Environment env;


    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get DDI Access Services environment",
            description = "This will return a safe (no secrets) projection of the current backend environment"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public Response getEnvironment() throws Exception {
        try {
            JSONObject entity = new JSONObject();
            entity.put("Swagger Host", env.getProperty("fr.insee.rmes.api.host"));
            entity.put("Swagger Name", env.getProperty("fr.insee.rmes.api.name"));
            entity.put("Swagger Scheme", env.getProperty("fr.insee.rmes.api.scheme"));
            entity.put("Database", env.getProperty("fr.insee.rmes.search.db.host"));
            entity.put("Colectica Metadata services", env.getProperty("fr.insee.rmes.api.remote.metadata.url"));
            return Response.ok().entity(entity).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }
}
