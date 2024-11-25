package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.springframework.http.HttpStatus;

public class RmesNotAcceptableException extends RmesException {

	private static final long serialVersionUID = 2L;

	public RmesNotAcceptableException(String message, String details) {
		super(HttpStatus.NOT_ACCEPTABLE, message, details);
	}

	public RmesNotAcceptableException(String message, JSONArray details) {
		super(HttpStatus.NOT_ACCEPTABLE.value(), message, details);
	}	
	
	public RmesNotAcceptableException(int errorCode, String message, String details) {
		super(HttpStatus.NOT_ACCEPTABLE.value(), errorCode, message, details);
	}

	public RmesNotAcceptableException(int errorCode, String message, JSONArray details) {
		super(HttpStatus.NOT_ACCEPTABLE.value(), errorCode, message, details);
	}	
	
}
