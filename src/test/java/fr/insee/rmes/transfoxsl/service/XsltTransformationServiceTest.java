package fr.insee.rmes.transfoxsl.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XsltTransformationServiceTest {

    @InjectMocks
    private XsltTransformationService xsltTransformationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialiser les mocks
    }

    @Test
    public void transform_ShouldReturnTransformedText_WhenXslTransformationIsSuccessful() throws Exception {
        // Utilisation d'un vrai fichier XML en tant qu'InputStream
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("physicalInstance-test.xml");
        assertNotNull(inputStream, "The XML input stream should not be null");

        // Exécution de la transformation avec un fichier XSL réel
        List<String> result = xsltTransformationService.transform(inputStream, "testTransformation.xsl", true);

        // Vérification des résultats
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertEquals("Transformation result: fr.insee", result.get(0));  // Personnalisez cette valeur selon vos attentes
    }

    @Test
    public void transform_ShouldThrowException_WhenXslFileIsInvalid() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("physicalInstance-test.xml");
        assertNotNull(inputStream, "The XML input stream should not be null");

        // Simuler un fichier XSL non valide en passant un nom de fichier inexistant
        assertThrows(Exception.class, () -> {
            xsltTransformationService.transform(inputStream, "invalid.xsl", true);
        });
    }
}
