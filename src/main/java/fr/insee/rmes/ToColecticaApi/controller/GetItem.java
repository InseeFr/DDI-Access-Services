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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.NonNull;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/Item")
@Tag(name= "DEMO-Colectica",description = "Services for upgrade Colectica-API")
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

    @Value("${fr.insee.rmes.api.remote.metadata.url}")
    String serviceUrl;

    @Value("${fr.insee.rmes.api.remote.metadata.agency}")
    String agency;

    private static final String API_BASE_URL = "http://metadonnees-operations.developpement3.insee.fr/api/v1/jsonset/fr.insee/";

    private final RestTemplate restTemplate;

    public GetItem(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private ElasticsearchController elasticsearchController;

    @GetMapping("ddiInstance/{uuid}")
    @Operation(summary = "Get ddiInstance by uuid", description = "Get an XML document for a ddi:Instance from Colectica repository.")
    @Produces(MediaType.APPLICATION_XML)
    public ResponseEntity<?> FindInstanceByUuidColectica (
            @Parameter(
                    description = "id de l'objet colectica",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")) String uuid) {
        ResponseEntity<?> responseEntity = searchColecticaInstanceByUuid(uuid);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        } else {
            return responseEntity;
        }

    }

    @GetMapping("ddiFragment/{uuid}")
    @Operation(summary = "Get Fragment by uuid", description = "Get an XML document for a ddi:Fragment from Colectica repository.")
    @Produces(MediaType.APPLICATION_XML)
    public ResponseEntity<?> FindFragmentByUuidColectica (
            @Parameter(
                    description = "id de l'objet colectica",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")) String uuid) {
        ResponseEntity<?> responseEntity = searchColecticaFragmentByUuid(uuid);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        } else {
            return responseEntity;
        }

    }

    private ResponseEntity<?> searchColecticaFragmentByUuid(String uuid) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = String.format("%s/api/v1/item/%s/%s", serviceUrl, agency, uuid);
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/xml");
            httpGet.addHeader("Accept", "application/json");

            String authToken;
            if (!serviceUrl.contains("kube")) {
                authToken = getFreshToken();
            } else {
                String token2 = getAuthToken();
                authToken = extractAccessToken(token2);
            }
            httpGet.setHeader("Authorization", "Bearer " + authToken);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    return ResponseEntity.status(response.getStatusLine().getStatusCode()).body("Erreur lors de la requête vers Colectica.");
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = new JSONObject(responseBody);
                String xmlContent = jsonResponse.get("Item").toString();
                xmlContent = xmlContent.replace("\\\"", "\"").replace("ï»¿", ""); // Remove escape characters and special characters
                return ResponseEntity.ok(xmlContent);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors de la requête vers Colectica.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExceptionColecticaUnreachable e) {
            throw new RuntimeException(e);
        }

    }
    private ResponseEntity<?> searchColecticaInstanceByUuid(String uuid) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet;
            String authentToken;
            String url = String.format("%s/api/v1/ddiset/%s/%s", serviceUrl, agency, uuid);
            httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/xml");
            httpGet.addHeader("Accept", "application/xml");
            if (!serviceUrl.contains("kube")){
            httpGet.setHeader("Authorization", "Bearer " + getFreshToken());}
            else {
                String token2 = getAuthToken();
                authentToken = extractAccessToken(token2);
                httpGet.setHeader("Authorization", "Bearer " + authentToken);
            }
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String preresponseBody = EntityUtils.toString(response.getEntity());
                String responseBody = preresponseBody.replace("ï»¿","");
                return ResponseEntity.ok(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Une erreur s'est produite lors de la requête vers Colectica.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExceptionColecticaUnreachable e) {
            throw new RuntimeException(e);
        }
    }

        @GetMapping("/filtered-search/{index}/{texte}")
        @Operation(summary = "Get list of match in elasticsearch database", description = "Get a JSON ")
        public ResponseEntity<?> filteredSearchText(
                @Parameter(
                        description = "nom par défaut de l'index colectica",
                        required = true,
                        schema = @Schema(
                                type = "string", example="portal*"))
             String index ,
                @Parameter(
                        description = "texte à rechercher. le * sert de wildcard",
                        required = true,
                        schema = @Schema(
                                type = "string", example="sugg*")) String texte) {
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


    @GetMapping("suggesters/jsonWithChild")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get JSON for Suggester/codelist simple (id,label)", description = "Get a JSON document for suggester or codelist from Colectica repository including an item with childs.")
       public Object getJsonWithChild(
            @Parameter(
            description = "id de l'objet colectica",
            required = true,
            schema = @Schema(
                    type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93"))  String identifier,
            @RequestParam(value = "fieldIdName",defaultValue = "id") String outputField,
            @RequestParam(value="fieldLabelName",defaultValue = "label") String fieldLabelName) throws Exception {
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

                List<Map<String, String>> idLabelPairs = new ArrayList<>();
                for (JsonNode codeNode : codesNode) {
                    String id = codeNode.get("Value").asText();
                    String label = codeNode.get("Category").get("Label").get("fr-FR").asText();

                    Map<String, String> idLabelPair = new HashMap<>();
                    idLabelPair.put(outputField, id);
                    idLabelPair.put(fieldLabelName, label);
                    idLabelPairs.add(idLabelPair);
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

    @PutMapping ("/replace-xml-parameters")
    @Operation(summary = "Modify a fragment DDI", description = "Modify a fragment DDI. All field need to be filled with the same data if there are no changes, except for the version number, which takes a plus 1.")
    public String replaceXmlParameters(@RequestBody String inputXml,
                                       @RequestParam ("Type") DDIItemType type,
                                       @RequestParam ("Label") String label,
                                       @RequestParam ("Version") int version,
                                       @RequestParam ("Name") String name,
                                       @RequestParam ("VersionResponsibility") String idepUtilisateur) {
        try {
            // Create a DocumentBuilder to parse the XML input
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(inputXml)));

            String typeName= type.getName();
            NodeList typeNodes = document.getElementsByTagNameNS("ddi:logicalproduct:3_3", typeName);
            Element typeNameDocument = (Element) typeNodes.item(0);
            if (!typeName.equals(typeNameDocument.getNodeName().toString())) {
                return "Erreur : Attention ce n'est pas le bon type. L'item chargé n'est pas du type que vous avez sélectionné.";
            }

            Document document2 = (Document) document.cloneNode(true);
            Node versionNode = document2.getElementsByTagName("r:Version").item(0);
            versionNode.setTextContent(String.valueOf(version));
            Node nameNode= document2.getElementsByTagName("r:String").item(0);
            nameNode.setTextContent(name);
            Node labelNode = document2.getElementsByTagName("r:Content").item(0);
            labelNode.setTextContent(label);
            Node UrnNode = document2.getElementsByTagName("r:URN").item(0);
            String IdFragment= UrnNode.getTextContent();
            UrnNode.setTextContent(IdFragment.substring(0,IdFragment.lastIndexOf(":"))+":"+version);
            // Convert the modified XML back to string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document2), new StreamResult(writer));
            InputStream xsltStream2 = getClass().getResourceAsStream("/DDIxmltojsonForOneObject.xsl");
            String jsonContent = transformToJson(new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8)), xsltStream2, idepUtilisateur,version);
            return jsonContent;
            //return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing XML";
        }
    }
    private String transformToJson(Resource resultResource, InputStream xsltFileJson, String idepUtilisateur, int version) throws IOException, TransformerException {

        // Créer un transformateur XSLT
        TransformerFactory factory = TransformerFactory.newInstance();

        Source xslt = new StreamSource(xsltFileJson);
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("idepUtilisateur", idepUtilisateur);
        transformer.setParameter("version", version);
        // on lance la transfo
        StreamSource text = new StreamSource(resultResource.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);
        transformer.transform(text, xmlResult);
        String jsonContent = xmlWriter.toString();
        return jsonContent;
    }


}
