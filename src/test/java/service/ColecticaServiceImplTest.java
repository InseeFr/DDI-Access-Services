package service;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.tocolecticaapi.service.ColecticaServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ColecticaServiceImplTest {

    @Test
    void getResponseEntity_NotOkStatus() {
        var uuid="1";
        var agency="insee";

        MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
        String baseUrl = "http://keycloak/";
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl);
        customizer.customize(builder);
        customizer.getServer().expect(requestTo(baseUrl+"item/"+agency+"/"+ uuid)).andRespond(withResourceNotFound());

        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(null,  builder.build(), null, null, agency);
        var expectedException=assertThrows(RmesExceptionIO.class, ()-> colecticaService.findFragmentByUuid(uuid));
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
        customizer.getServer().expect(requestTo(baseUrl+"item/"+agency+"/"+ uuid)).andRespond(withSuccess(jsonContent, MediaType.APPLICATION_JSON));

        ColecticaServiceImpl colecticaService = new ColecticaServiceImpl(null,  builder.build(), null, null, agency);

        assertThat(colecticaService.findFragmentByUuid(uuid)).hasToString(xmlContent);
    }


}
