package fr.insee.rmes.webservice.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/env")
@Tag(name = "RMeS Environment")
public class RMeSEnvironment {

    private static final Logger log = LogManager.getLogger(fr.insee.rmes.webservice.rest.RMeSEnvironment.class);

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
    public String getEnvironment() throws Exception {
        try {
            JSONObject envRep = new JSONObject();
            envRep.put("Swagger Host", env.getProperty("fr.insee.rmes.api.host"));
            envRep.put("Swagger Name", env.getProperty("fr.insee.rmes.api.name"));
            envRep.put("Swagger Scheme", env.getProperty("fr.insee.rmes.api.scheme"));
            envRep.put("Colectica Metadata services", env.getProperty("fr.insee.rmes.api.remote.metadata.url"));
            return envRep.toString(2);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }
}
