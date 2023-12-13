package fr.insee.rmes.ToColecticaApi.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.ToColecticaApi.models.AuthRequest;
import fr.insee.rmes.ToColecticaApi.models.CustomMultipartFile;
import fr.insee.rmes.ToColecticaApi.models.RessourcePackage;
import fr.insee.rmes.ToColecticaApi.models.TransactionType;
import fr.insee.rmes.ToColecticaApi.randomUUID;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import lombok.NonNull;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;
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
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ColecticaServiceImpl implements ColecticaService {

    private final static String CONTENT_TYPE = "Content-Type";
    private final static String APPLICATION_XML = "application/xml";
    private final static String APPLICATION_JSON = "application/json";
    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer ";
    private final static String HTTP = "http://";
    private final static String VERSION = "version";
    private final static String SEARCH = "/_search";
    private final static String BASIC = "Basic ";
    private final static String ERREUR_COLECTICA = "Une erreur s'est produite lors de la requête vers Colectica.";
    private final static String ERREUR_ELASTICSEARCH = "Une erreur s'est produite lors de la requête Elasticsearch.";
    private final static String TRANSACTIONID = "{\"TransactionId\":";





    @NonNull
    @Autowired
    private KeycloakServices kc;

    private String token;
    @Value("${auth.api.url}")
    private String authApiUrl;


    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Value("${fr.insee.rmes.api.remote.metadata.url}")
    private String serviceUrl;

    @Value("${fr.insee.rmes.api.remote.metadata.agency}")
    private String agency;

   @Value("${fr.insee.rmes.elasticsearch.host}")
    private String  elasticHost;

    @Value("${fr.insee.rmes.elasticsearch.port}")
    private int  elasticHostPort;

    @Value("${fr.insee.rmes.elasticsearch.apiId}")
    private String apiId;

    @Value("${fr.insee.rmes.elasticsearch.apikey}")
    private String apiKey;

    private final ElasticsearchClient elasticsearchClient;
    private final RestTemplate restTemplate;
    public ColecticaServiceImpl(ElasticsearchClient elasticsearchClient,RestTemplate restTemplate) {
        this.elasticsearchClient = elasticsearchClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> findFragmentByUuid(String uuid) {
        ResponseEntity<String> responseEntity = searchColecticaFragmentByUuid(uuid);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        } else {
            return responseEntity;
        }
    }

    private ResponseEntity<String> searchColecticaFragmentByUuid(String uuid) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = String.format("%s/api/v1/item/%s/%s", serviceUrl, agency, uuid);
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader(CONTENT_TYPE, APPLICATION_XML);
            httpGet.addHeader("Accept", APPLICATION_JSON);

            String authToken;
            if (!serviceUrl.contains("kube")) {
                authToken = getFreshToken();
            } else {
                String token2 = getAuthToken();
                authToken = extractAccessToken(token2);
            }
            httpGet.setHeader(AUTHORIZATION, BEARER + authToken);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                    return ResponseEntity.status(response.getStatusLine().getStatusCode())
                            .body("Erreur lors de la requête vers Colectica.");
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = new JSONObject(responseBody);
                String xmlContent = jsonResponse.get("Item").toString();
                xmlContent = xmlContent.replace("\\\"", "\"").replace("ï»¿", "");
                return ResponseEntity.ok(xmlContent);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ERREUR_COLECTICA);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExceptionColecticaUnreachable e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public ResponseEntity<String> findInstanceByUuid(String uuid) {
        ResponseEntity<String> responseEntity = searchColecticaInstanceByUuid(uuid);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        } else {
            return responseEntity;
        }
    }

    private ResponseEntity<String> searchColecticaInstanceByUuid(String uuid) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet;
            String authentToken;
            String url = String.format("%s/api/v1/ddiset/%s/%s", serviceUrl, agency, uuid);
            httpGet = new HttpGet(url);
            httpGet.addHeader(CONTENT_TYPE, APPLICATION_XML);
            httpGet.addHeader("Accept", APPLICATION_XML);
            if (!serviceUrl.contains("kube")){
                httpGet.setHeader(AUTHORIZATION, BEARER + getFreshToken());
            } else {
                String token2 = getAuthToken();
                authentToken = extractAccessToken(token2);
                httpGet.setHeader(AUTHORIZATION, BEARER + authentToken);
            }
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String preresponseBody = EntityUtils.toString(response.getEntity());
                String responseBody = preresponseBody.replace("ï»¿","");
                return ResponseEntity.ok(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ERREUR_COLECTICA);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ExceptionColecticaUnreachable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<String> filteredSearchText(String index, String texte) {
        ResponseEntity<String> responseEntity = searchText(index, texte);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            String filteredResponse = filterAndTransformResponse(responseBody);
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;
        }
    }

    @Override
    public ResponseEntity<String> SearchByType(String index, DDIItemType type) {
        ResponseEntity<String> responseEntity = searchType(index, String.valueOf(type.getUUID()).toLowerCase());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            String filteredResponse = filterAndTransformResponse(responseBody);
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;
        }
    }

    @Override
    public ResponseEntity<String> SearchTexteByType(String index,String texte, DDIItemType type) {
        ResponseEntity<String> responseEntity = searchTextByType(index, texte, String.valueOf(type.getUUID()).toLowerCase());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            String filteredResponse = filterAndTransformResponse(responseBody);
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;
        }
    }


    private ResponseEntity<String> searchType(String index,String type) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost;
            String jsonBody = "{\"query\": {\"match\": {\"itemType\":\""+ type + "\"}}, \"_source\": true, \"size\": 10000, \"from\": 0}";
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);

            if (elasticHost.contains("kube")) {
                httpPost = new HttpPost("https://" + elasticHost + ":" + elasticHostPort + "/" + index + SEARCH);
                httpPost.setEntity(entity);
            }
            else {
                httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + SEARCH);
                String token = Base64.getEncoder().encodeToString((apiId + ":" + apiKey).getBytes(StandardCharsets.UTF_8));
                httpPost.setHeader(AUTHORIZATION, BASIC + token);
                httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
                httpPost.setEntity(entity);
            }


            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ERREUR_ELASTICSEARCH);
        }
    }


    private ResponseEntity<String> searchText(String index, String texte) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String encodedTexte = URLEncoder.encode(texte, StandardCharsets.UTF_8.toString());
            HttpGet httpGet;

            if (elasticHost.contains("kube")) {
                httpGet = new HttpGet("https://" + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q=" + encodedTexte);
            } else {
                httpGet = new HttpGet(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q=*" + encodedTexte + "*");
                String token = Base64.getEncoder().encodeToString((apiId + ":" + apiKey).getBytes(StandardCharsets.UTF_8));
                httpGet.addHeader(AUTHORIZATION, BASIC + token);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ERREUR_ELASTICSEARCH);
        }
    }

    private ResponseEntity<String> searchTextByType(String index, String texte, String type) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + SEARCH);

            // Ajouter l'en-tête pour l'authentification et le type de contenu si nécessaire
            if (!elasticHost.contains("kube")) {
                String token = Base64.getEncoder().encodeToString((apiId + ":" + apiKey).getBytes(StandardCharsets.UTF_8));
                httpPost.setHeader(AUTHORIZATION, BASIC + token);
                httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            }

            // Construction de la requête wildcard pour chercher 'texte' dans tous les champs indexés
            StringEntity entity = new StringEntity(
                    "{\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        { \"match\": { \"itemType\":\"" + type + "\" }},\n" +
                    "        { \"query_string\": {\n" +
                    "            \"query\": \"*" + texte + "*\",\n" +
                    "            \"fields\": [\"*\"]\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}", ContentType.APPLICATION_JSON);

            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ERREUR_ELASTICSEARCH);
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

                String name = source.path("name_fr-FR").asText();
                String label = source.path("label_fr-FR").asText();

                // Filtrer les éléments avec name ou label non vide
                if (!name.isEmpty() || !label.isEmpty()) {
                    ObjectNode filteredSource = objectMapper.createObjectNode();
                    filteredSource.put("name", name);
                    filteredSource.put("label", label);

                    String versionlessId = source.path("versionlessId").asText();
                    if (versionlessId.startsWith("fr.insee:")) {
                        versionlessId = versionlessId.substring("fr.insee:".length());
                    }
                    filteredSource.put("Id",  versionlessId);

                    filteredHitsArray.add(filteredSource);
                }}

            return filteredHitsArray.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Une erreur s'est produite lors de la manipulation du JSON.";
        }
    }

    @Override
    public ResponseEntity<List<Map<String,String>>> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws Exception {
        String apiUrl = serviceUrl+"/api/v1/jsonset/fr.insee/" + identifier;

        if (!serviceUrl.contains("kube")){
            token = getFreshToken();
        } else {
            String token2 = getAuthToken();
            token = extractAccessToken(token2);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, BEARER + token);
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

    @Override
    public String convertXmlToJson(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException {

        String apiUrl = serviceUrl + "/api/v1/jsonset/fr.insee/" + uuid;

        if (!serviceUrl.contains("kube")) {
            token = getFreshToken();
        } else {
            String token2 = getAuthToken();
            token = extractAccessToken(token2);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, BEARER + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        String result = null;
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                RessourcePackage ressourcePackage = mapper.readValue(response.getBody(), RessourcePackage.class);
                List<Map<String, String>> countriesWithCodesAndLabels = ressourcePackage.mapCodesToLabels();
                ObjectMapper mapperFinal= new ObjectMapper();
                result = mapperFinal.writeValueAsString(countriesWithCodesAndLabels);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

        @Override
    public String replaceXmlParameters(@RequestBody String inputXml,
                                       @RequestParam("Type") DDIItemType type,
                                       @RequestParam ("Label") String label,
                                       @RequestParam (VERSION) int version,
                                       @RequestParam ("Name") String name,
                                       @RequestParam ("VersionResponsibility") String idepUtilisateur) {
        try {

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
            NodeList versionNodes = document2.getElementsByTagName("r:Version");
            for (int i = 0; i < versionNodes.getLength(); i++) {
                Node versionNode = versionNodes.item(i);
                if (versionNode instanceof Element) {
                    Element versionElement = (Element) versionNode;
                    versionElement.setTextContent(String.valueOf(version));
                }
            }

            Node nameNode= document2.getElementsByTagName("r:String").item(0);
            nameNode.setTextContent(name);

            Node labelNode = document2.getElementsByTagName("r:Content").item(0);
            labelNode.setTextContent(label);

            NodeList urnNodes = document2.getElementsByTagName("r:URN");

            for (int i = 0; i < urnNodes.getLength(); i++) {
                Node urnNode = urnNodes.item(i);
                if (urnNode instanceof Element) {
                    Element urnElement = (Element) urnNode;
                    String urnCode = urnElement.getTextContent();
                    urnElement.setTextContent(urnCode.substring(0,urnCode.lastIndexOf(":"))+":"+version); // Remplacez par le contenu souhaité
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document2), new StreamResult(writer));
            InputStream xsltStream2 = getClass().getResourceAsStream("/DDIxmltojsonForOneObject.xsl");
            String jsonContent = transformToJson(new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8)), xsltStream2, idepUtilisateur,version);
            return jsonContent;


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
        transformer.setParameter(VERSION, version);
        // on lance la transfo
        StreamSource text = new StreamSource(resultResource.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);
        transformer.transform(text, xmlResult);
        String jsonContent = xmlWriter.toString();
        return jsonContent;
    }

    @Override
    public ResponseEntity<?> getByType(DDIItemType type) throws IOException, ExceptionColecticaUnreachable {

        if (!serviceUrl.contains("kube")){
            token = getFreshToken();
        } else {
            String token2 = getAuthToken();
            token = extractAccessToken(token2);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, BEARER + token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = serviceUrl + "/api/v1/_query";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(AUTHORIZATION, BEARER + token);
            httpPost.setHeader(CONTENT_TYPE, "application/json-patch+json");
            String requestBody = "{ \"ItemTypes\": [\"" + type.getUUID().toLowerCase() + "\"] }";
            httpPost.setEntity(new StringEntity(requestBody));

            String responseBody;
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                responseBody = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(ERREUR_COLECTICA);
            }

            return ResponseEntity.ok(responseBody);
        }
    }

    @Override
    public ResponseEntity<String> sendUpdateColectica(String DdiUpdatingInJson, TransactionType transactionType) {
        try {
            // Étape 1: Initialiser la transaction
            String initTransactionUrl = serviceUrl + "/api/v1/transaction";
            String authentToken;
            if (serviceUrl.contains("kube")) {
                String token2 = getAuthToken();
                authentToken = extractAccessToken(token2);
            } else {
                authentToken = getFreshToken();
            }

            HttpHeaders initTransactionHeaders = new HttpHeaders();
            initTransactionHeaders.setContentType(MediaType.APPLICATION_JSON);
            initTransactionHeaders.setBearerAuth(authentToken);
            HttpEntity<String> initTransactionRequest = new HttpEntity<>(initTransactionHeaders);
            ResponseEntity<String> initTransactionResponse = restTemplate.exchange(
                    initTransactionUrl,
                    HttpMethod.POST,
                    initTransactionRequest,
                    String.class
            );

            if (initTransactionResponse.getStatusCode() == HttpStatus.OK) {
                String responseBody = initTransactionResponse.getBody();
                int transactionId = extractTransactionId(responseBody);
                // Étape 2: Peupler la transaction
                String populateTransactionUrl = serviceUrl + "/api/v1/transaction/_addItemsToTransaction";
                HttpHeaders populateTransactionHeaders = new HttpHeaders();
                populateTransactionHeaders.setContentType(MediaType.APPLICATION_JSON);
                populateTransactionHeaders.setBearerAuth(authentToken);
                // Créez un objet de demande pour peupler la transaction ici avec transactionId et DdiUpdatingInJson
                String updatedJson = DdiUpdatingInJson.replaceFirst("\\{", TRANSACTIONID + transactionId + ",");
                HttpEntity<String> populateTransactionRequest = new HttpEntity<>(updatedJson, populateTransactionHeaders);
                ResponseEntity<String> populateTransactionResponse = restTemplate.exchange(
                        populateTransactionUrl,
                        HttpMethod.POST,
                        populateTransactionRequest,
                        String.class
                );

                if (populateTransactionResponse.getStatusCode() == HttpStatus.OK) {
                    // Étape 3: Commit la transaction
                    String commitTransactionUrl = serviceUrl + "/api/v1/transaction/_commitTransaction";
                    HttpHeaders commitTransactionHeaders = new HttpHeaders();
                    commitTransactionHeaders.setContentType(MediaType.APPLICATION_JSON);
                    commitTransactionHeaders.setBearerAuth(authentToken);
                    // Créez un objet de demande pour commettre la transaction ici avec transactionId et transactionType
                    String commitTransactionRequestBody = TRANSACTIONID + transactionId + ",\"TransactionType\":\"" + transactionType + "\"}";
                    HttpEntity<String> commitTransactionRequest = new HttpEntity<>(commitTransactionRequestBody, commitTransactionHeaders);
                    ResponseEntity<String> commitTransactionResponse = restTemplate.exchange(
                            commitTransactionUrl,
                            HttpMethod.POST,
                            commitTransactionRequest,
                            String.class
                    );

                    if (commitTransactionResponse.getStatusCode() == HttpStatus.OK) {
                        return ResponseEntity.ok("Transaction réussie.");
                    } else {
                        // Échec de la transaction, annuler la transaction
                        cancelTransaction(transactionId,authentToken);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de la transaction.");
                    }
                } else {
                    // Échec du peuplement de la transaction, annuler la transaction
                    cancelTransaction(transactionId,authentToken);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec du peuplement de la transaction.");
                }
            } else {
                // Échec de l'initialisation de la transaction
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de l'initialisation de la transaction.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite.");
        }
    }

    private int extractTransactionId(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            int transactionId = jsonNode.get("TransactionId").asInt();
            return transactionId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String cancelTransaction(int transactionId, String authentToken) {
        // Étape 4: Annuler la transaction en cas d'échec
        String cancelTransactionUrl = serviceUrl + "/api/v1/transaction/_cancelTransaction";
        HttpHeaders cancelTransactionHeaders = new HttpHeaders();
        cancelTransactionHeaders.setContentType(MediaType.APPLICATION_JSON);
        cancelTransactionHeaders.setBearerAuth(authentToken);
        // Créez un objet de demande pour annuler la transaction ici avec transactionId
        String cancelTransactionRequestBody = TRANSACTIONID + transactionId + "}";
        HttpEntity<String> cancelTransactionRequest = new HttpEntity<>(cancelTransactionRequestBody, cancelTransactionHeaders);
        ResponseEntity<String> cancelTransactionResponse = restTemplate.exchange(
                cancelTransactionUrl,
                HttpMethod.POST,
                cancelTransactionRequest,
                String.class
        );

        if (cancelTransactionResponse.getStatusCode() == HttpStatus.OK) {
            return "Transaction annulée.";
        } else {
            return "Échec de l'annulation de la transaction.";
        }
    }

    @Override
    public ResponseEntity<?> transformFile(
            MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String resultFileName2 = UUID.randomUUID().toString() + ".json";

            MultipartFile outputFile = processFile(file);
            System.out.println("Le fichier a été modifié avec succès (ajout balise data) !");

            InputStream xsltStream1 = getClass().getResourceAsStream("/jsontoDDIXML.xsl");
            String xmlContent = transformToXml(outputFile, xsltStream1, idValue, nomenclatureName, suggesterDescription, timbre,version);

            InputStream xsltStream2 = getClass().getResourceAsStream("/DDIxmltojson.xsl");
            String jsonContent = transformToJson(new ByteArrayResource(xmlContent.getBytes(StandardCharsets.UTF_8)), xsltStream2, idepUtilisateur);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", UriUtils.encode(resultFileName2, StandardCharsets.UTF_8));

            return new ResponseEntity<>(jsonContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la transformation du fichier.", e);
        }
    }
    @Override
    public ResponseEntity<?> transformFileForComplexList(
            MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre,
            String principale,
            List secondaire,
            List labelSecondaire
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String resultFileName2 = UUID.randomUUID().toString() + ".json";

            MultipartFile outputFile = processFile(file);
            System.out.println("Le fichier a été modifié avec succès (ajout balise data) !");

            InputStream xsltStream1 = getClass().getResourceAsStream("/jsonToDdiXmlForComplexList.xsl");
            String xmlContent = transformToXmlForComplexList(outputFile, xsltStream1, idValue, nomenclatureName, suggesterDescription, timbre,version,principale,secondaire,labelSecondaire);

            InputStream xsltStream2 = getClass().getResourceAsStream("/DDIComplexxmltojson.xsl");
            String jsonContent = transformToJson(new ByteArrayResource(xmlContent.getBytes(StandardCharsets.UTF_8)), xsltStream2, idepUtilisateur);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", UriUtils.encode(resultFileName2, StandardCharsets.UTF_8));

            return new ResponseEntity<>(jsonContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la transformation du fichier.", e);
        }
    }

    public static MultipartFile processFile(MultipartFile inputFile) throws Exception {
        byte[] modifiedContent;
        try (InputStream inputStream = inputFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Écriture de la balise d'ouverture "<data>"
            outputStream.write("<data>".getBytes());

            // Copie du contenu du fichier d'entrée dans le flux de sortie
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Écriture de la balise de fermeture "</data>"
            outputStream.write("</data>".getBytes());

            // Récupération du contenu modifié sous forme de tableau de bytes
            modifiedContent = outputStream.toByteArray();
        }

        // Création d'un objet FileItem à partir du contenu modifié
        DiskFileItemFactory factory = new DiskFileItemFactory();
        FileItem fileItem = factory.createItem(
                inputFile.getName(),
                inputFile.getContentType(),
                false,
                inputFile.getOriginalFilename()
        );
        try (InputStream modifiedInputStream = new ByteArrayInputStream(modifiedContent)) {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = modifiedInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                fileItem.getOutputStream().write(output.toByteArray());
            }
        }

        // Création d'un nouvel objet MultipartFile à partir du FileItem modifié
        return new CustomMultipartFile(fileItem);
    }

    private String transformToXml(MultipartFile file, InputStream xsltFile, String idValue, String nomenclatureName,
                                  String suggesterDescription,  String timbre, String version)
            throws IOException, TransformerException {

        // Créer un transformateur XSLT
        TransformerFactory factory = TransformerFactory.newInstance();

        // on doit passer à TransformerFactoryImpl pour pouvoir faire des modifs
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;

        // on appelle le "processor" actuel
        net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
        Processor processor = (Processor) saxonConfig.getProcessor();

        // on injecte ici la class que l'on appelle dans le xslt
        ExtensionFunction randomUUID = new randomUUID();
        processor.registerExtensionFunction(randomUUID);
        Source xslt = new StreamSource(xsltFile);
        Transformer transformer = factory.newTransformer(xslt);

        // param pour la transfo

        transformer.setParameter("idValue", idValue);
        transformer.setParameter("suggesterName", nomenclatureName);
        transformer.setParameter("suggesterDescription", suggesterDescription);
        transformer.setParameter("timbre", timbre);
        transformer.setParameter(VERSION, version);
        // on lance la transfo
        StreamSource text = new StreamSource(file.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);
        transformer.transform(text, xmlResult);
        String xmlContent = xmlWriter.toString();
        return xmlContent;
    }

    private String transformToXmlForComplexList(MultipartFile file, InputStream xsltFile, String idValue, String nomenclatureName,
                                  String suggesterDescription,  String timbre, String version, String principale,List <String> secondaire, List  <String> labelSecondaire)
            throws IOException, TransformerException {

        // Créer un transformateur XSLT
        TransformerFactory factory = TransformerFactory.newInstance();

        // on doit passer à TransformerFactoryImpl pour pouvoir faire des modifs
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;

        // on appelle le "processor" actuel
        net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
        Processor processor = (Processor) saxonConfig.getProcessor();

        // on injecte ici la class que l'on appelle dans le xslt
        ExtensionFunction randomUUID = new randomUUID();
        processor.registerExtensionFunction(randomUUID);
        Source xslt = new StreamSource(xsltFile);
        Transformer transformer = factory.newTransformer(xslt);

        // param pour la transfo

        transformer.setParameter("idValue", idValue);
        transformer.setParameter("suggesterName", nomenclatureName);
        transformer.setParameter("suggesterDescription", suggesterDescription);
        transformer.setParameter("timbre", timbre);
        transformer.setParameter(VERSION, version);
        transformer.setParameter("principale", principale);
        transformer.setParameter("secondaire", secondaire);
        transformer.setParameter("labelSecondaire", labelSecondaire);
        // on lance la transfo
        StreamSource text = new StreamSource(file.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);
        transformer.transform(text, xmlResult);
        String xmlContent = xmlWriter.toString();
        return xmlContent;
    }

    private String transformToJson(Resource resultResource, InputStream xsltFileJson, String idepUtilisateur) throws IOException, TransformerException {

        // Créer un transformateur XSLT
        TransformerFactory factory = TransformerFactory.newInstance();

        Source xslt = new StreamSource(xsltFileJson);
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("idepUtilisateur", idepUtilisateur);
        // on lance la transfo
        StreamSource text = new StreamSource(resultResource.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);
        transformer.transform(text, xmlResult);
        String jsonContent = xmlWriter.toString();
        return jsonContent;
    }

    @Override
    public ResponseEntity<String> uploadItem(MultipartFile file)  {
        try {
            String authentToken;
            if (serviceUrl.contains("kube")) {
                String token2 = getAuthToken();
                authentToken = extractAccessToken(token2);
            } else {
                authentToken = getFreshToken();
            }
            String fileContent = new String(file.getBytes());
            String itemApiUrl = serviceUrl + "/api/v1/item";
            boolean success = sendFileToApi(fileContent, authentToken, itemApiUrl);

            if (success) {
                return ResponseEntity.ok("Le fichier a été envoyé avec succès à l'API.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Une erreur s'est produite lors de l'envoi du fichier à l'API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite.");
        }
    }

    private boolean sendFileToApi(String fileContent, String token, String url) {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(100000); // Temps de connexion en millisecondes
        factory.setReadTimeout(100000); // Temps de lecture en millisecondes

        restTemplate.setRequestFactory(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(fileContent, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,request, String.class);

        return response.getStatusCode() == HttpStatus.OK;
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
