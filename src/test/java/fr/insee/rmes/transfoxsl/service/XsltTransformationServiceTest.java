package fr.insee.rmes.transfoxsl.service;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.model.DDIItemType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class XsltTransformationServiceTest {

    private final XsltTransformationService xsltTransformationService = new XsltTransformationService();

    @Test
    void transform_ShouldReturnTransformedText_WhenXslTransformationIsSuccessful() throws Exception {
        // Utilisation d'un vrai fichier XML en tant qu'InputStream
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("physicalInstance-test.xml");
        assertNotNull(inputStream, "The XML input stream should not be null");

        // Exécution de la transformation avec un fichier XSL réel
        byte[] result = xsltTransformationService.transformToRawText(inputStream, "testTransformation.xsl");

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("Transformation result: fr.insee", new String(result));
    }

    @Test
    void transform_ShouldThrowException_WhenXslFileIsInvalid() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("physicalInstance-test.xml");
        assertNotNull(inputStream, "The XML input stream should not be null");

        // Simuler un fichier XSL non valide en passant un nom de fichier inexistant
        assertThrows(Exception.class, () -> {
            xsltTransformationService.transformToRawText(inputStream, "invalid.xsl");
        });
    }

    @Test
    void testReplaceXmlParameters() {
        String inputXml = "<example><r:Version>1</r:Version><r:String>Old Name</r:String><r:Content>Old Label</r:Content><r:URN>urn:example:1</r:URN></example>";
        DDIItemType type = DDIItemType.CODE_LIST;
        String label = "New Label";
        int version = 2;
        String name = "New Name";
        String idepUtilisateur = "user";
        String result = xsltTransformationService.replaceXmlParameters(inputXml, type, label, version, name, idepUtilisateur);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testTransformFile() throws RmesException {
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", "[{\"id\": \"value\",\"label\": \"value\" }]".getBytes());
        String result = xsltTransformationService.transformFile(file, "idValue", "nomenclatureName", "suggesterDescription", "version", "idepUtilisateur", "timbre");
        assertNotNull(result);
    }

    @Test
    void testTransformToJson() throws Exception {
        // Mock the Resource to simulate the XML input
        Resource mockResource = Mockito.mock(Resource.class);
        Mockito.when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream("<root><element>Test</element></root>".getBytes()));

        // Provide a valid XSLT as InputStream
        String xsltContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                    <xsl:output method="xml" indent="yes"/>
                    <xsl:template match="/">
                        <transformed>
                            <xsl:copy-of select="*"/>
                        </transformed>
                    </xsl:template>
                </xsl:stylesheet>""";
        InputStream xsltStream = new ByteArrayInputStream(xsltContent.getBytes());

        // Call the method to be tested
        String result = new String(XsltTransformationService.transformToJson(mockResource, xsltStream, "user"));

        // Assertions
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("<transformed>"));
        assertTrue(result.contains("<element>Test</element>"));
    }

}
