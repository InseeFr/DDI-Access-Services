package fr.insee.rmes.webservice.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by acordier on 04/07/17.
 */
@Provider
public class RMeSExceptionMapper implements ExceptionMapper<RMeSException> {
	public Response toResponse(RMeSException ex) {
		RestMessage message = ex.toRestMessage();
		return Response.status(message.getStatus()).entity(message).type(MediaType.APPLICATION_JSON).build();
	}
}
