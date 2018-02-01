package fr.insee.rmes.webservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("env")
@Api(value = "RMeS Environment")
public class RMeSEnvironment {

    private final static Logger logger = LogManager.getLogger(RMeSEnvironment.class);

    @Autowired
    Environment env;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get DDI Access Services environment",
            notes = "This will return a safe (no secrets) projection of the current backend environment"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
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
            logger.error(e.getMessage(), e);
            throw e;
        }

    }
}
