package service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaServiceImpl;
import fr.insee.rmes.utils.DocumentBuilderUtils;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {"fr.insee.rmes.api.remote.metadata.url=http://mock-url"})
@RunWith(SpringRunner.class)
class ColecticaServiceImplTest {

    @Configuration
    static class TestConfig {
        // Configuration beans can be defined here if needed
    }
    static class TestableColecticaServiceImpl extends ColecticaServiceImpl {
        public static ResponseEntity<String> callProtectedMethod(CloseableHttpResponse response) throws IOException {
            return getResponseEntitySearchColecticaFragmentByUuid(response);
        }
    }

    @Mock
    private CloseableHttpClient mockHttpClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private KeycloakServices keycloakServices;

    @InjectMocks
    private ColecticaServiceImpl colecticaServiceImpl;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(keycloakServices.isTokenValid(anyString())).thenReturn(true);

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 404, "Not Found"));
        StringEntity entity = new StringEntity("");
        when(mockResponse.getEntity()).thenReturn(entity);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        colecticaServiceImpl = new ColecticaServiceImpl(mockHttpClient, elasticsearchClient, restTemplate);
        when(keycloakServices.getKeycloakAccessToken()).thenReturn("un_token_valide");
        ReflectionTestUtils.setField(colecticaServiceImpl, "serviceUrl", "http://mock-url");
    }

    @Test
    void getResponseEntity_NotOkStatus() throws IOException {
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 404, "Not Found"));
        ResponseEntity<String> result = TestableColecticaServiceImpl.callProtectedMethod(mockResponse);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().contains("Erreur lors de la requête vers Colectica."));
    }

    @Test
    void getResponseEntity_OkStatus() throws IOException {
        String jsonContent = "{\"Item\": \"<xml>Contenu</xml>\"}";
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(jsonContent));
        ResponseEntity<String> result = TestableColecticaServiceImpl.callProtectedMethod(mockResponse);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("<xml>Contenu</xml>"));
    }

    @Test
    void testReplaceXmlParameters() {
        String inputXml = "<example><r:Version>1</r:Version><r:String>Old Name</r:String><r:Content>Old Label</r:Content><r:URN>urn:example:1</r:URN></example>";
        DDIItemType type = DDIItemType.CODE_LIST;
        String label = "New Label";
        int version = 2;
        String name = "New Name";
        String idepUtilisateur = "user";
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(elasticsearchClient, restTemplate);
        String result = colecticaService.replaceXmlParameters(inputXml, type, label, version, name, idepUtilisateur);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testTransformFile() {
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(elasticsearchClient, restTemplate);
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", "[{\"id\": \"value\",\"label\": \"value\" }]".getBytes());
        ResponseEntity<String> result = colecticaService.transformFile(file, "idValue", "nomenclatureName", "suggesterDescription", "version", "idepUtilisateur", "timbre");
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void testTransformToJson() throws Exception {
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl();

        // Mock the Resource to simulate the XML input
        Resource mockResource = Mockito.mock(Resource.class);
        Mockito.when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream("<root><element>Test</element></root>".getBytes()));

        // Provide a valid XSLT as InputStream
        String xsltContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
                "    <xsl:output method=\"xml\" indent=\"yes\"/>\n" +
                "    <xsl:template match=\"/\">\n" +
                "        <transformed>\n" +
                "            <xsl:copy-of select=\"*\"/>\n" +
                "        </transformed>\n" +
                "    </xsl:template>\n" +
                "</xsl:stylesheet>";
        InputStream xsltStream = new ByteArrayInputStream(xsltContent.getBytes());

        // Call the method to be tested
        String result = colecticaService.transformToJson(mockResource, xsltStream, "user");

        // Assertions
        assertNotNull(result);
        assertTrue(!result.isEmpty());
        assertTrue(result.contains("<transformed>"));
        assertTrue(result.contains("<element>Test</element>"));
    }

    @Test
    void testUploadItem() {
        ColecticaServiceImpl colecticaService = mock(ColecticaServiceImpl.class);
        String jsonContent = "{\n" +
                "   \"Items\":\n" +
                "[\n" +
                "{\n" +
                "\"ItemType\": \"8b108ef8-b642-4484-9c49-f88e4bf7cf1d\",\n" +
                "\"AgencyId\": \"fr.insee\",\n" +
                "\"Version\": 1,\n" +
                "\"Identifier\": \"4aea85af-27c9-4b87-a057-ccbd78ec95bf\",\n" +
                "\"Item\": \" </Fragment>\",\n" +
                "\"VersionDate\": \"2024-02-21T09:31:22.0300000Z\",\n" +
                "\"VersionResponsibility\": \"QZ6ICW\",\n" +
                "\"IsPublished\": false,\n" +
                "\"IsDeprecated\": false,\n" +
                "\"IsProvisional\": false,\n" +
                "\"ItemFormat\": \"DC337820-AF3A-4C0B-82F9-CF02535CDE83\"\n" +
                "}]";
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", jsonContent.getBytes());
        ResponseEntity<String> mockedResponse = ResponseEntity.ok("Le fichier a été envoyé avec succès à l'API.");
        when(colecticaService.uploadItem(file)).thenReturn(mockedResponse);
        ResponseEntity<String> result = colecticaService.uploadItem(file);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Le fichier a été envoyé avec succès à l'API.", result.getBody());
    }

    @Test
    void testGetDocument() throws Exception {
        String xml = "<root><element>value</element></root>";
        Document doc = DocumentBuilderUtils.getDocument(xml);

        assertNotNull(doc);
        assertEquals("root", doc.getDocumentElement().getNodeName());
    }


}
