package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import javax.xml.transform.*;
import static org.junit.jupiter.api.Assertions.*;

class XMLUtilsTest {

    String mockedXmlOrFragment = """
         <?xml version="1.0"?>
            <SOFTWARE>
            <NAME>DDI-Access-Services</NAME>
            <PUBLISHER>Unknown</PUBLISHER>
            <CREATOR>Unknown</CREATOR>
            </SOFTWARE>""";

@Test
    void shouldReturnTransformerWhenNewTransformer() throws TransformerConfigurationException {
        var actual = XMLUtils.newTransformer();
        String expectedClass ="net.sf.saxon.jaxp.IdentityTransformer";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnStringTransformerWhenToString() throws Exception {
        XpathProcessorImpl xpathProcessor = new XpathProcessorImpl();
        Document node = xpathProcessor.toDocument(mockedXmlOrFragment);
        var actual = XMLUtils.toString(node);
        String expectedClass ="java.lang.String";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnInitTransformerFactoryWhenGetTransformerFactory() {
        var actual = XMLUtils.getTransformerFactory();
        String expectedClass ="net.sf.saxon.TransformerFactoryImpl";
        assertEquals(expectedClass, actual.getClass().getName());
    }
}