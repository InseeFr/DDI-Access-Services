package fr.insee.rmes.exceptions;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/env")
@Tag(name = "RMeS Environment")
@Slf4j
public class RMeSEnvironment {

    private final Environment env;

    public RMeSEnvironment(Environment env) {
        this.env = env;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get DDI Access Services environment",
            description = "This will return a safe (no secrets) projection of the current backend environment"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public String getEnvironment() {
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
