package fr.insee.rmes.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Created by acordier on 04/07/17.
 */
@Provider
public class RMeSExceptionMapper implements ExceptionMapper<RmesExceptionIO> {
	public Response toResponse(RmesExceptionIO ex) {
		RestMessage message = ex.toRestMessage();
		return Response.status(message.getStatus()).entity(message).type(MediaType.APPLICATION_JSON).build();
	}
}
