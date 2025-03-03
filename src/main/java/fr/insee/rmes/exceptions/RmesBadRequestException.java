package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class RmesBadRequestException extends RmesException {

	private static final long serialVersionUID = 400L;

	public RmesBadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST, message, "");
	}
	
	public RmesBadRequestException(String message, String details) {
		super(HttpStatus.BAD_REQUEST, message, details);
	}

	public RmesBadRequestException(String message, JSONArray details) {
		super(HttpStatus.BAD_REQUEST.value(), message, details);
	}	
	
	public RmesBadRequestException(int errorCode, String message, String details) {
		super(HttpStatus.BAD_REQUEST.value(), errorCode, message, details);
	}

	public RmesBadRequestException(int errorCode, String message, JSONArray details) {
		super(HttpStatus.BAD_REQUEST.value(), errorCode, message, details);
	}

	public RmesBadRequestException(int errorCode, String message) {
		super(HttpStatus.BAD_REQUEST.value(), errorCode, message, "");
	}
	public RmesBadRequestException(int errorCode, String message, JSONObject details) {
		super(HttpStatus.FORBIDDEN.value(), errorCode, message, details);	}
}
