package fr.insee.rmes.exceptions;

public class XsltTransformationException extends RuntimeException {
    public XsltTransformationException(String message) {
        super(message);
    }

    public XsltTransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}