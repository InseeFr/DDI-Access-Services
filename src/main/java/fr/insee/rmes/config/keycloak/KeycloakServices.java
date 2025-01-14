package fr.insee.rmes.config.keycloak;

import com.auth0.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class KeycloakServices {

    final String secret;

    final String resource;

    final String server;

    final String realm;

    private String token;
    //TODO is it the right way to synchronize ?
    private final AtomicReference<Instant> expiration;

    public KeycloakServices(@Value("${fr.insee.rmes.metadata.keycloak.secret}") String secret, @Value("${fr.insee.rmes.metadata.keycloak.resource}") String resource, @Value("${fr.insee.rmes.metadata.keycloak.server}") String server, @Value("${fr.insee.rmes.metadata.keycloak.realm}") String realm) {
        this.secret = secret;
        this.resource = resource;
        this.server = server;
        this.realm = realm;
        this.expiration = new AtomicReference<>();
    }

    /**
     * Permet de récuperer un jeton keycloak
     *
     * @return jeton
     */
    private String getKeycloakAccessToken() {

        RestTemplate keycloakClient = new RestTemplate();
        String keycloakUrl = server + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", resource);
        body.add("client_secret", secret);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        Token accessToken = keycloakClient.postForObject(keycloakUrl, entity, Token.class);

        log.trace("Keycloak token provided");
        return accessToken.accessToken();

    }

    /**
     * check if oidc token has expired
     *
     * @return boolean
     */
    private boolean isCurrentTokenValid() {
        return expiration.get() != null && Instant.now().isBefore(expiration.get().minus(1, ChronoUnit.SECONDS));
    }

    public String getFreshToken() {
        if (!this.isCurrentTokenValid()) {
            synchronized (this.expiration) {
                token = getKeycloakAccessToken();
                this.expiration.set(decodeToken(token));
            }
        }
        return token;
    }

    private Instant decodeToken(String token) {
        return JWT.decode(token).getExpiresAtAsInstant();
    }

}
