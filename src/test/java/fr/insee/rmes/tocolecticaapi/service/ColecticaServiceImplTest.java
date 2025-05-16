package fr.insee.rmes.tocolecticaapi.service;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.transfoxsl.service.internal.DDIDerefencer;
import fr.insee.rmes.utils.ExportUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import java.io.IOException;
import java.net.URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ColecticaServiceImplTest {

    @Mock
    ElasticService elasticService;
    RestClient restClient;
    ExportUtils exportUtils;
    DDIDerefencer ddiDerefencer;
    String agency;


    @Test
    void getResponseEntity_NotOkStatus() {
        var uuid="1";
        var agency="insee";

        MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
        String baseUrl = "http://keycloak/";
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl);
        customizer.customize(builder);
        customizer.getServer().expect(MockRestRequestMatchers.requestTo(baseUrl+"item/"+agency+"/"+ uuid)).andRespond(MockRestResponseCreators.withResourceNotFound());

        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(null,  builder.build(), null, null, agency);
        var expectedException= assertThrows(RmesExceptionIO.class, ()-> colecticaService.findFragmentByUuid(uuid));
        assertThat(expectedException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(expectedException.getMessage()).isEqualTo("Bad request or inexisting resource");
    }

    @Test
    void getResponseEntity_OkStatus() throws IOException {
        String xmlContent = "<xml>Contenu</xml>";
        String jsonContent = "{\"Item\": \""+xmlContent+"\"}";
        var uuid="1";
        var agency="insee";
        String baseUrl = "http://keycloak/";

        MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl);
        customizer.customize(builder);
        customizer.getServer().expect(MockRestRequestMatchers.requestTo(baseUrl+"item/"+agency+"/"+ uuid)).andRespond(withSuccess(jsonContent, MediaType.APPLICATION_JSON));

        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(null,  builder.build(), null, null, agency);

        assertThat(colecticaService.findFragmentByUuid(uuid)).hasToString(xmlContent);
    }

    @Test
    void getHttpResponseWithBom_shouldNotReturnUglyChars() throws IOException {
        String baseUrl = "http://collectica";
        byte[] xmlContentWithBom = ColecticaServiceImplTest.class.getResourceAsStream("/utf8-bom/fichierAvecBom.xml").readAllBytes();
        var expectedXml= new String(ColecticaServiceImplTest.class.getResourceAsStream("/utf8-bom/fichierSansBom.xml").readAllBytes());
                MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl);
        customizer.customize(builder);
        customizer.getServer().expect(MockRestRequestMatchers.requestTo(baseUrl+"/ddi")).andRespond(withSuccess(xmlContentWithBom, MediaType.APPLICATION_XML));
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(null,  builder.build(), null, null, null);
        var diff= DiffBuilder.compare(Input.fromString(colecticaService.getWithRestClient(URI.create("ddi"), MediaType.APPLICATION_XML)))
                .withTest(Input.fromString(expectedXml))
                .ignoreWhitespace()
                .build();
        assertFalse(diff.hasDifferences(), diff.toString());

    }

    @ParameterizedTest
    @ValueSource(strings = { "example-of-string !", ")à)ç)àçà)ç)","784854$" })
    void shouldReturnRmesExceptionWhenSearchColecticaInstanceByUuid(String uuid)  {
        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(elasticService,restClient,exportUtils, ddiDerefencer, agency);
        RmesException exception = assertThrows(RmesException.class, () -> colecticaService.searchColecticaInstanceByUuid(uuid));
        assertTrue(exception.getDetails().contains("ne respecte pas le pattern d'un uuid\",\"message\":\"UUID invalide\""));
    }

}
