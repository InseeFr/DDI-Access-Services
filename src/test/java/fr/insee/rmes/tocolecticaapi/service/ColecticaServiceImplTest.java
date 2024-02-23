package fr.insee.rmes.tocolecticaapi.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class ColecticaServiceImplTest {

    @Mock
    private CloseableHttpClient mockHttpClient;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private KeycloakServices keycloakServices;

    @Value("${fr.insee.rmes.api.remote.metadata.url}")
    private String serviceUrl;
    @MockBean
    private ColecticaServiceImpl colecticaService;
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
        when(mockHttpClient.execute(Mockito.any(HttpGet.class))).thenReturn(mockResponse);

        colecticaServiceImpl = new ColecticaServiceImpl(mockHttpClient, elasticsearchClient, restTemplate);
        //ReflectionTestUtils.setField(colecticaServiceImpl, "serviceUrl", "http://dvrmesgopslm003.ad.insee.intra:8080");
        when(keycloakServices.getKeycloakAccessToken()).thenReturn("un_token_valide");
    }

    @Test
    void getResponseEntity_NotOkStatus() throws IOException {
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 404, "Not Found"));
        ResponseEntity<String> result = colecticaServiceImpl.getResponseEntitySearchColecticaFragmentByUuid(mockResponse);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().contains("Erreur lors de la requête vers Colectica."));
    }


    @Test
    void getResponseEntity_OkStatus() throws IOException {
        String jsonContent = "{\"Item\": \"<xml>Contenu</xml>\"}";
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(jsonContent));
        ResponseEntity<String> result = colecticaServiceImpl.getResponseEntitySearchColecticaFragmentByUuid(mockResponse);
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
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(elasticsearchClient);
        String result = colecticaService.replaceXmlParameters(inputXml, type, label, version, name, idepUtilisateur);
        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    public void testGetByType() throws ExceptionColecticaUnreachable, IOException {

        DDIItemType type =  DDIItemType.CODE_LIST;
        when(colecticaService.getByType(type)).thenReturn(ResponseEntity.ok("test"));
        ResponseEntity<String> result = colecticaService.getByType(type);
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
    }

    @Test
    public void testTransformFile() {
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl();
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", "[{\"id\": \"value\",\"label\": \"value\" }]".getBytes());
        ResponseEntity<String> result = colecticaService.transformFile(file, "idValue", "nomenclatureName", "suggesterDescription", "version", "idepUtilisateur", "timbre");
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
    }

    @Test
    public void testUploadItem() {
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
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Le fichier a été envoyé avec succès à l'API.", result.getBody());
    }
}
