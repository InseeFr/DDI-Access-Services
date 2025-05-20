package fr.insee.rmes.config.keycloak;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import static org.junit.jupiter.api.Assertions.*;

class KeycloakServicesTest {

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGetFreshToken() {
        KeycloakServices keycloakServices = new KeycloakServices("secret","resource","server","realm");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, keycloakServices::getFreshToken);
        assertTrue(exception.getMessage().contains("URI is not absolute"));

    }

    @Test
    void shouldThrowResourceAccessExceptionWhenGetFreshToken() {
        KeycloakServices keycloakServices = new KeycloakServices("secret","resource","http://localhost:8080/","realm");
        ResourceAccessException exception = assertThrows(ResourceAccessException.class, keycloakServices::getFreshToken);
        assertTrue(exception.getMessage().contains("Connection refused"));
    }


}