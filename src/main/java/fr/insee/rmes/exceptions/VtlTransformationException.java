package fr.insee.rmes.exceptions;

public class VtlTransformationException extends RuntimeException {
    public VtlTransformationException(String message) {
        super(message);
    }

    public VtlTransformationException(String message, Throwable cause) {
        super(message, cause);
    }
}
