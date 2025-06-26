package fr.insee.rmes.exceptions;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class XsltTransformationExceptionTest {

    @ParameterizedTest
    @ValueSource(strings = { "example", "---","b/**","mockedMessage","4589"})
    void shouldGetNotNullXmlErrorMessageWhenGetXmlErrorMessage(String message) {
        XsltTransformationException exception = new XsltTransformationException(message,new SaxonApiException(message));
        assertEquals(exception.getXmlErrorMessage().toString(),"Optional["+message+"]");
    }
}