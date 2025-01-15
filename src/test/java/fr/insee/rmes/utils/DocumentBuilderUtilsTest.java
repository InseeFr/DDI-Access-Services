package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class DocumentBuilderUtilsTest {

    @Test
    void testGetDocument() throws Exception {
        String xml = "<root><element>value</element></root>";
        Document doc = DocumentBuilderUtils.getDocument(new ByteArrayInputStream(xml.getBytes()));

        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getNodeName());
    }

}