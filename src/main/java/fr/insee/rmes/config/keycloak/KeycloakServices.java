package fr.insee.rmes.config.keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class KeycloakServices {


	@Value("${fr.insee.rmes.metadata.keycloak.secret}")
    String secret;
	
	@Value("${fr.insee.rmes.metadata.keycloak.resource}")
    String resource;
	
	@Value("${fr.insee.rmes.metadata.keycloak.server}")
    String server;
	
	@Value("${fr.insee.rmes.metadata.keycloak.realm}")
    String realm;

    /**
     * Permet de récuperer un jeton keycloak
     * @return jeton
     */
    public String getKeycloakAccessToken() throws ExceptionColecticaUnreachable {

        String authString = resource + ":" + secret;

        byte[] authBytes = authString.getBytes(StandardCharsets.UTF_8);
        String encodedAuthString = Base64.getEncoder().encodeToString(authBytes);

        RestTemplate keycloakClient = new RestTemplate();
        String keycloakUrl = server + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedAuthString);

        String request = "grant_type=client_credentials";

        HttpEntity<String> entity = new HttpEntity<String>(request, headers);
        try {
            String result = keycloakClient.postForObject(keycloakUrl, entity, String.class);

            String accessToken =
                (String) result.split(",")[0].split(":")[1]
                    .subSequence(1, result.split(",")[0].split(":")[1].length() - 1);

            log.info("Retrieve Keycloak token");

            return accessToken;
        }
        catch (RestClientException e) {
            throw new ExceptionColecticaUnreachable("Le serveur Keycloak est injoignable");
        }

    }

    /**
     * Verifie si le jeton keycloak a expiré
     * @param token
     * @return boolean
     */
    public boolean isTokenValid(String token) {
        if (token == null) {
            return false;
        }
        boolean isValid = false;
        Date now = new Date();
        try {
            DecodedJWT jwt = JWT.decode(token);
            if (jwt.getExpiresAt().after(now)) {
                log.info("Token is valid");
                isValid = true;
            }
        }
        catch (JWTDecodeException exception) {
            System.out.println("erreur" + exception.toString());

        }
        return isValid;
    }
}
