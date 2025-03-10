package fr.insee.rmes;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.insee.rmes.config.InseeSecurityTokenProperties;
import fr.insee.rmes.config.SecurityConfig;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.tocolecticaapi.controller.GetItem;
import fr.insee.rmes.tocolecticaapi.fragments.DdiFragmentServiceImpl;
import fr.insee.rmes.tocolecticaapi.service.ColecticaServiceImpl;
import fr.insee.rmes.tocolecticaapi.service.ElasticService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.service.internal.DDIDerefencer;
import fr.insee.rmes.utils.ExportUtils;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static fr.insee.rmes.config.keycloak.KeycloakServices.CLIENT_REGISTRATION_ID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GetItem.class, excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class, properties = {
        "fr.insee.rmes.api.remote.metadata.url=" + AuthenticatedCallsToColecticaTest.URL_WIREMOCK,
        "fr.insee.rmes.api.remote.metadata.agency=Insee",
        "spring.profiles.active=dev",
        "fr.insee.rmes.security.token.oidc-claim-role=roles",
        "fr.insee.rmes.security.token.oidc-claim-username=name",
        "fr.insee.rmes.security.whitelist-matchers=",
        "fr.insee.rmes.role.administrateur=",
        "fr.insee.rmes.role.gestionnaire.colectica="
})
@Import({DdiFragmentServiceImpl.class, XsltTransformationService.class, ColecticaServiceImpl.class, KeycloakServices.class, SecurityConfig.class})
@WireMockTest(httpPort = AuthenticatedCallsToColecticaTest.WIREMOCK_PORT)
class AuthenticatedCallsToColecticaTest {

    static final int WIREMOCK_PORT = 8282;
    static final String URL_WIREMOCK = "http://localhost:" + WIREMOCK_PORT;
    static final String EXPECTED_JSON = """
            [
                {
                  "id": "urn:ddi:fr.insee:77dfee13-1a96-43c1-b1ef-7b78b45be55d:2",
                  "nom": "Dessin de fichier thl-CASD",
                  "label": [
                    {
                      "contenu": "",
                      "langue": "fr"
                    },
                    {
                      "contenu": "",
                      "langue": "en"
                    }
                  ],
                  "variables": [
                    {
                      "id": "urn:ddi:fr.insee:aa090e13-252a-4da7-b776-8f8e505be6d9:1",
                      "nom": "ACTOCCUPE",
                      "label": [
                        {
                          "contenu": "Actif occupé",
                          "langue": "fr"
                        },
                        {
                          "contenu": "",
                          "langue": "en"
                        }
                      ],
                      "ordre": "1",
                      "representation": "codes",
                      "controles": {
                        "codes": [
                          {
                            "code": "0",
                            "label": [
                              {
                                "langue": "fr",
                                "contenu": "Inactif ou actif non occupé"
                              },
                              {
                                "langue": "en",
                                "contenu": ""
                              }
                            ]
                          },
                          {
                            "code": "1",
                            "label": [
                              {
                                "langue": "fr",
                                "contenu": "Actif occupé"
                              },
                              {
                                "langue": "en",
                                "contenu": ""
                              }
                            ]
                          }
                        ]
                      }
                    },
                    {
                      "id": "urn:ddi:fr.insee:7f2ae666-2208-46ce-bb1d-fd3fda8c6667:1",
                      "nom": "AG",
                      "label": [
                        {
                          "contenu": "Age au 31 décembre de l'année de l'enquête",
                          "langue": "fr"
                        },
                        {
                          "contenu": "",
                          "langue": "en"
                        }
                      ],
                      "ordre": "2",
                      "representation": "numerique"
                    }
                  ]
                }
            ]
            """;

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ElasticService elasticService;
    @MockitoBean
    ExportUtils exportUtils;
    @MockitoBean
    DDIDerefencer ddiDerefencer;

    @Test
    void authenticatedCallsToColectica() throws Exception {
        String tokenValue = "blablabla";
        String uuid = "16a35b68-4479-4282-95ed-ff7d151746e4";
        String endpoint = "/Item/ddiFragment/" + uuid + "/dataRelationship";
        String colecticaRepositoryEndpoint = "/api/v1/ddiset/Insee/" + uuid;
        prepareWiremock(colecticaRepositoryEndpoint);

        mvc.perform(get(endpoint)
                        .with(oauth2Client(CLIENT_REGISTRATION_ID)
                                .accessToken(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, null, null, Collections.emptySet()))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(EXPECTED_JSON));
        WireMock.verify(newRequestPattern().withHeader("Authorization", equalTo("Bearer " + tokenValue)));
    }

    private void prepareWiremock(String ressource) throws IOException {
        ResponseDefinitionBuilder responseDefinitionBuilder = ok()
                .withHeader("Content-Type", ContentType.APPLICATION_XML.getMimeType())
                .withBody(wiremockReturn());
        stubFor(WireMock.get(ressource).willReturn(responseDefinitionBuilder));
    }

    private String wiremockReturn() throws IOException {
        try (InputStream resourceAsStream = AuthenticatedCallsConfigurationTest.class.getResourceAsStream("/AuthenticatedCalls/wiremockReturn.xml")) {
            return new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @TestConfiguration
    @EnableConfigurationProperties(InseeSecurityTokenProperties.class)
    static class AuthenticatedCallsConfigurationTest {

        @Bean
        InMemoryClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(Map.of());
        }

        @Bean
        OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
            return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }

        @Bean
        OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
            return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
        }
    }

}
