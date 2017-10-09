package fr.insee.rmes.webservice.rest;

import java.io.IOException;

/**
 * Created by acordier on 04/07/17.
 */
public class RMeSException extends IOException {

	private int status;
	private String details;

	/**
	 *
	 * @param status
	 * @param message
	 * @param details
	 */
	public RMeSException(int status, String message, String details) {
		super(message);
		this.status = status;
		this.details = details;
	}

	public RestMessage toRestMessage() {
		return new RestMessage(this.status, this.getMessage(), this.details);
	}
}
