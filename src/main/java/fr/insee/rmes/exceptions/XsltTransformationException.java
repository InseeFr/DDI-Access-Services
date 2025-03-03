package fr.insee.rmes.exceptions;

import net.sf.saxon.s9api.SaxonApiException;

import java.util.Optional;

public class XsltTransformationException extends Exception {

    public XsltTransformationException(String message, SaxonApiException cause) {
        super(message, cause);
    }

    public Optional<String> getXmlErrorMessage(){
        var cause = getCause();
        return cause != null ? Optional.of(cause.getMessage()) : Optional.empty();
    }

}