package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;
import static org.junit.jupiter.api.Assertions.*;

class XpathProcessorImplTest {

    XpathProcessorImpl xpathProcessor = new XpathProcessorImpl();

    String mockedXmlOrFragment = """
         <?xml version="1.0"?>
            <SOFTWARE>
            <NAME>DDI-Access-Services</NAME>
            <PUBLISHER>Unknown</PUBLISHER>
            <CREATOR>Unknown</CREATOR>
            </SOFTWARE>""";

    String mockedXpathExpression ="mockedXpathExpression";

    @Test
    void shouldReturnNodeListWhenQueryList() throws Exception {
        var actual = xpathProcessor.queryList(mockedXmlOrFragment,mockedXpathExpression);
        String expectedClass ="com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnNodeListWhenQueryListWithNode() throws Exception {
        Document node = xpathProcessor.toDocument(mockedXmlOrFragment);
        var actual = xpathProcessor.queryList(node,mockedXpathExpression);
        String expectedClass ="com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnStringWhenQueryString() throws Exception {
        var actual = xpathProcessor.queryString(mockedXmlOrFragment,mockedXpathExpression);
        String expectedClass ="java.lang.String";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnStringWhenQueryStringWithNode() throws Exception {
        Document node = xpathProcessor.toDocument(mockedXmlOrFragment);
        var actual = xpathProcessor.queryString(node,mockedXpathExpression);
        String expectedClass ="java.lang.String";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnSameValuesWhenQueryTextAndQueryString() throws Exception {
        var actualQueryString = xpathProcessor.queryString(mockedXmlOrFragment,mockedXpathExpression);
        var actualQueryText = xpathProcessor.queryText(mockedXmlOrFragment,mockedXpathExpression);
        assertEquals( actualQueryString,actualQueryText);
    }

    @Test
    void shouldReturnStringWhenQueryTextWithNode() throws Exception {
        Document node = xpathProcessor.toDocument(mockedXmlOrFragment);
        var actual = xpathProcessor.queryText(node,mockedXpathExpression);
        String expectedClass ="java.lang.String";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnDocumentWhenToDocument() throws Exception {
        var actual = xpathProcessor.toDocument(mockedXmlOrFragment);
        String expectedClass ="com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnStringWhenToString() throws Exception {
        Document node = xpathProcessor.toDocument(mockedXmlOrFragment);
        var actual = xpathProcessor.toString(node);
        String expectedClass ="java.lang.String";
        assertEquals(expectedClass, actual.getClass().getName());
    }

    @Test
    void shouldReturnSAXParseExceptionWhenWrongXmlEntered() {
        SAXParseException exception = assertThrows(SAXParseException.class, () -> xpathProcessor.toDocument("Fake mocked Xml Or Fragment"));
        assertEquals("Content is not allowed in prolog.",exception.getMessage());
    }

}