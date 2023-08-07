package fr.insee.rmes.ToColecticaApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.ToColecticaApi.models.AuthRequest;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.webservice.rest.RMeSException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class GetItem {
    final static Logger logger = LogManager.getLogger(GetItem.class);

    private final ResourceLoader resourceLoader;

    @Value("${auth.api.url}")
    private String authApiUrl;

    @Value("https://colectica-metadonnees.dev.kube.insee.fr/api/v1/_query")
    private String colecticaUrlQuery;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    public GetItem(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("Item/{id}/ddi")
    @Produces(MediaType.APPLICATION_XML)
    @Operation(summary = "Get DDI document", description = "Get a DDI document from Colectica repository including an item thanks to its {id} and its children as fragments.")
    public Response getDDIDocumentFragmentInstance(@PathVariable String id,
                                                   @RequestParam(value="withChild") boolean withChild) throws Exception {
        try {


            StreamingOutput stream = output -> {
                try {

                } catch (Exception e) {
                    throw new RMeSException(500, "Transformation error", e.getMessage());
                }
            };
            return Response.ok(stream).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("Item/{type}/json")
    public ResponseEntity<?> ByType (
            @PathVariable ("type") DDIItemType type)
            throws IOException {

        String token = getAuthToken();
        String accessToken = extractAccessToken(token);


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = colecticaUrlQuery;
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization","Bearer " + accessToken);
            httpPost.setHeader("Content-Type", "application/json-patch+json");
            String requestBody = "{ \"ItemTypes\": [\"" + type.getUUID().toLowerCase() + "\"] }";
            httpPost.setEntity(new StringEntity(requestBody));
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
               /* String regex = "\"Identifier\":\\s\"[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}\"";
                Pattern p = Pattern.compile(regex,Pattern.DOTALL);
                Matcher m = p.matcher(responseBody);*/
                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Une erreur s'est produite lors de la requête vers Colectica.");
        }

    }
    private String getAuthToken() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

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
