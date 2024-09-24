package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class RmesUnauthorizedException extends RmesException {
	
	private static final long serialVersionUID = 5611172589954490294L;

	public RmesUnauthorizedException() {
		super(HttpStatus.FORBIDDEN, "Unauthorized", "");
	}
	
	public RmesUnauthorizedException(String message, String details) {
		super(HttpStatus.FORBIDDEN, message, details);
	}

	public RmesUnauthorizedException(String message, JSONArray details) {
		super(HttpStatus.FORBIDDEN.value(), message, details);
	}
	
	public RmesUnauthorizedException(int errorCode, String details) {
		super(HttpStatus.FORBIDDEN.value(), errorCode, details);
	}

	public RmesUnauthorizedException(int errorCode, JSONArray details) {
		super(HttpStatus.FORBIDDEN.value(), errorCode, details);
	}

	public RmesUnauthorizedException(int errorCode, String message, JSONArray details) {
		super(HttpStatus.FORBIDDEN.value(), errorCode, message, details);
	}

	public RmesUnauthorizedException(int errorCode, String message, String details) {
		super(HttpStatus.FORBIDDEN.value(), errorCode, message, details);
	}

	public RmesUnauthorizedException(int errorCode, String message, JSONObject details) {
		super(HttpStatus.FORBIDDEN.value(), errorCode, message, details);	}
}
