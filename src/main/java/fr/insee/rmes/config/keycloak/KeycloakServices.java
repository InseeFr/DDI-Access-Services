package fr.insee.rmes.config.keycloak;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Service
public record KeycloakServices(OAuth2AuthorizedClientManager authorizedClientManager) {

    public static final String CLIENT_REGISTRATION_ID = "colectica-client";

    public Consumer<RestClient.Builder> configureOidcClientAutoAuthentication() {
        return builder -> builder
                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(authorizedClientManager))
                .defaultRequest(requestHeadersSpec -> requestHeadersSpec.attributes(clientRegistrationId(CLIENT_REGISTRATION_ID)));
    }
}
