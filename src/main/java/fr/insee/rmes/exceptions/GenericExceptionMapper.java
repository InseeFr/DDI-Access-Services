package fr.insee.rmes.exceptions;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


/**
 * Created by acordier on 04/07/17.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private Response.Status STATUS = Response.Status.INTERNAL_SERVER_ERROR;

	public Response toResponse(Throwable error) {
		RestMessage message = new RestMessage(STATUS.getStatusCode(), "An unexpected error occured",
				error.getMessage());
		if (error instanceof NotFoundException) {
			STATUS = Response.Status.NOT_FOUND;
			message.setMessage("Not Found");
			message.setDetails("No JAX-RS resource found for this path");
		}
		return Response.status(STATUS).entity(message).type(MediaType.APPLICATION_JSON).build();
	}
}
