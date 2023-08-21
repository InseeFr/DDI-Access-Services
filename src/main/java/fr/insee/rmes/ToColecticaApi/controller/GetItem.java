package fr.insee.rmes.ToColecticaApi.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.ToColecticaApi.models.AuthRequest;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.controller.ElasticsearchController;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.model.IdLabelPair;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.NonNull;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/Item")
@Tag(name= "Colectica-suggesters ",description = "Service pour gerer les suggesters dans Colectica")
public class GetItem {
    final static Logger logger = LogManager.getLogger(GetItem.class);

    @NonNull
    @Autowired
    private KeycloakServices kc;

    private static String token;
    @Value("${auth.api.url}")
    private String authApiUrl;

    @Value("http://metadonnees-operations.developpement3.insee.fr/api/v1/_query")
    private String colecticaUrlQuery;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    private static final String API_BASE_URL = "http://metadonnees-operations.developpement3.insee.fr/api/v1/jsonset/fr.insee/";

    private final RestTemplate restTemplate;

    public GetItem(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private ElasticsearchController elasticsearchController;

    @GetMapping("/filtered-search/{index}/{texte}")
    public ResponseEntity<?> filteredSearchText(
            @PathVariable("index") String index,
            @PathVariable("texte") String texte) {
        ResponseEntity<?> responseEntity = elasticsearchController.searchText(index, texte);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            String filteredResponse = filterAndTransformResponse(responseBody);
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;  // Propagate the original error response
        }
    }

    private String filterAndTransformResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            JsonNode hitsArray = jsonResponse.path("hits").path("hits");

            ArrayNode filteredHitsArray = objectMapper.createArrayNode();
            for (JsonNode hit : hitsArray) {
                JsonNode source = hit.path("_source");

                ObjectNode filteredSource = objectMapper.createObjectNode();
                filteredSource.put("name", source.path("name_fr-FR").asText());
                filteredSource.put("label", source.path("label_fr-FR").asText());

                String versionlessId = source.path("versionlessId").asText();
                if (versionlessId.startsWith("fr.insee:")) {
                    versionlessId = versionlessId.substring("fr.insee:".length());
                }
                filteredSource.put("Id",  versionlessId);

                filteredHitsArray.add(filteredSource);
            }

            return filteredHitsArray.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Une erreur s'est produite lors de la manipulation du JSON.";
        }
    }


    @GetMapping("suggesters/{identifier}/jsonWithChild")
    @Produces(MediaType.APPLICATION_JSON)
       @Operation(summary = "Get JSON for Suggester", description = "Get a JSON document from Colectica repository including an item with childs.")
       public Object getJsonWithChilds(@PathVariable String identifier) throws Exception {
           String apiUrl = API_BASE_URL + identifier;
           String token = getFreshToken();
           HttpHeaders headers = new HttpHeaders();
           headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode codesNode = jsonNode.get("Codes");

                List<IdLabelPair> idLabelPairs = new ArrayList<>();
                for (JsonNode codeNode : codesNode) {
                    String id = codeNode.get("Value").asText();
                    String label = codeNode.get("Category").get("Label").get("fr-FR").asText();
                    idLabelPairs.add(new IdLabelPair(id, label));
                }

                return ResponseEntity.ok(idLabelPairs);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @Hidden
    @PostMapping("{type}/json")
    @Operation(summary = "Get JSON for a type of DDI item", description = "Get a JSON list of item for a type of DDI items .")
    public ResponseEntity<?> ByType (
            @PathVariable ("type") DDIItemType type)
            throws IOException, ExceptionColecticaUnreachable {


        String token = getFreshToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = colecticaUrlQuery;
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Content-Type", "application/json-patch+json");
            String requestBody = "{ \"ItemTypes\": [\"" + type.getUUID().toLowerCase() + "\"] }";
            httpPost.setEntity(new StringEntity(requestBody));

            StringBuilder result;
            String responseBody;
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                responseBody = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Une erreur s'est produite lors de la requête vers Colectica.");
            }
            // return ResponseEntity.ok("{" + System.lineSeparator() + "\"" + type.getName() + "\" : [" + System.lineSeparator() + result + "]" + System.lineSeparator() + "}");
            return ResponseEntity.ok(responseBody);
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

    public String getFreshToken() throws ExceptionColecticaUnreachable, JsonProcessingException {
        if ( ! kc.isTokenValid(token)) {
            token = getToken();
        }
        return token;
    }

    public String getToken() throws ExceptionColecticaUnreachable, JsonProcessingException {
        return kc.getKeycloakAccessToken();

    }

}
