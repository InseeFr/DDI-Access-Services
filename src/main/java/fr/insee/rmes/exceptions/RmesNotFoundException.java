package fr.insee.rmes.exceptions;


import org.springframework.http.HttpStatus;

public class RmesNotFoundException extends RmesException {

	private static final long serialVersionUID = 1L;

	public RmesNotFoundException(String message, String details) {
		super(HttpStatus.NOT_FOUND, message, details);
	}

	public RmesNotFoundException(int errorCode, String message, String details) {
		super(HttpStatus.NOT_FOUND, errorCode + " : " + message, details);
	}
	public RmesNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message, "Not found");
	}
	
}
