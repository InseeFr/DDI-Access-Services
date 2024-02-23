package fr.insee.rmes.config.keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.tocolecticaapi.models.AuthRequest;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    @Value("${fr.insee.rmes.api.remote.metadata.url}")
    String testKube;

    @Value("${auth.api.url}")
    private String authApiUrl;

    @Value("${item.api.url}")
    private String itemApiUrl;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;
    /**
     * Permet de récuperer un jeton keycloak
     * @return jeton
     */
    public String getKeycloakAccessToken() throws ExceptionColecticaUnreachable, JsonProcessingException {

        if (!testKube.contains(".dev.kube.insee.fr")) {
            RestTemplate keycloakClient = new RestTemplate();
            String keycloakUrl = server + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", resource);
            body.add("client_secret", secret);
            HttpEntity<Object> entity = new HttpEntity<>(body, headers);
            try {

                Token accessToken = keycloakClient.postForObject(keycloakUrl, entity, Token.class);

                log.trace("Keycloak token provided");
                return accessToken.getAccessToken();
            } catch (RestClientException e) {
                throw new ExceptionColecticaUnreachable("Le serveur Keycloak est injoignable");
            }
        } else {

            String token = getAuthToken();
            return extractAccessToken(token);
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

    private String getAuthToken() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Créer un objet représentant les informations d'identification
        AuthRequest authRequest = new AuthRequest(username, password);

        // Convertir l'objet en JSON
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(authRequest);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(authApiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Impossible d'obtenir le token d'authentification.");
        }
    }

    public static String extractAccessToken(String token) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(token);
            return (String) json.get("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
