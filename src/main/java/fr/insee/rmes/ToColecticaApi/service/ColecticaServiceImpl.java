package fr.insee.rmes.tocolecticaapi.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.*;
import fr.insee.rmes.tocolecticaapi.randomUUID;
import fr.insee.rmes.webservice.rest.RMeSException;
import lombok.NonNull;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ERROR;

@Service
public class ColecticaServiceImpl implements ColecticaService {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_XML = "application/xml";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String HTTP = "http://";
    private static final String VERSION = "version";
    private static final String SEARCH = "/_search";
    private static final String APIKEYHEADER = "apiKey ";
    private static final String ERREUR_COLECTICA = "Une erreur s'est produite lors de la requête vers Colectica.";
    private static final String ERREUR_ELASTICSEARCH = "Une erreur s'est produite lors de la requête Elasticsearch.";
    private static final String TRANSACTIONID = "{\"TransactionId\":";

    static final Logger logger = LogManager.getLogger(ColecticaServiceImpl.class);



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

    @Autowired
    public ElasticsearchClient elasticsearchClient;
    @Autowired
    public  RestTemplate restTemplate;

    public ColecticaServiceImpl(ElasticsearchClient elasticsearchClient, RestTemplate restTemplate) {
    this.elasticsearchClient=elasticsearchClient;
    this.restTemplate=restTemplate;
    }
    private CloseableHttpClient mockHttpClient;
    public ColecticaServiceImpl(CloseableHttpClient mockHttpClient,ElasticsearchClient elasticsearchClient, RestTemplate restTemplate) {
        this.elasticsearchClient=elasticsearchClient;
        this.restTemplate=restTemplate;
        this.mockHttpClient=mockHttpClient;
    }

    public ColecticaServiceImpl(){

    }
    @Override
    public ResponseEntity<String> findFragmentByUuid(String uuid) throws ExceptionColecticaUnreachable, IOException {
        ResponseEntity<String> responseEntity = searchColecticaFragmentByUuid(uuid);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        } else {
            return responseEntity;
        }
    }

    private ResponseEntity<String> searchColecticaFragmentByUuid(String uuid) throws
            ExceptionColecticaUnreachable, IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = getGetSearchColecticaFragmentByUuid(uuid);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return getResponseEntitySearchColecticaFragmentByUuid(response);
            } catch (IOException e) {

                throw new IOException("Erreur IO lors de la récupération des données pour UUID: " + uuid, e);
            }
        } catch (ExceptionColecticaUnreachable e) {
            throw new ExceptionColecticaUnreachable("Le service Colectica est injoignable pour UUID: " + uuid, e);
        } catch (ParseException e) {
            throw new RMeSException(400,"erreur durant la lecture du jeton","");
        }
    }

    private HttpGet getGetSearchColecticaFragmentByUuid(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RMeSException, ParseException {
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
        return httpGet;
    }

    protected static ResponseEntity<String> getResponseEntitySearchColecticaFragmentByUuid(CloseableHttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            return ResponseEntity.status(response.getStatusLine().getStatusCode())
                    .body("Erreur lors de la requête vers Colectica.");
        }

        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject jsonResponse = new JSONObject(responseBody);
        String xmlContent = jsonResponse.get("Item").toString();
        xmlContent = xmlContent.replace("\\\"", "\"").replace("ï»¿", "");
        return ResponseEntity.ok(xmlContent);
    }


    @Override
    public ResponseEntity<String> findInstanceByUuid(String uuid) throws RMeSException, ParseException {
        ResponseEntity<String> responseEntity = searchColecticaInstanceByUuid(uuid);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody =  responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        } else {
            return responseEntity;
        }
    }

    @Override
    public String findFragmentByUuidWithChildren(String uuid) throws Exception {
        ResponseEntity<?> responseEntity = searchColecticaInstanceByUuid(uuid);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();
            Set<String> resultIntermediaire = extractUniqueIdentifiers(responseBody);
            JSONArray resultArray = processUuidsAndFetchData(resultIntermediaire);
            return resultArray.toString(4);
        } else {
            return "L'objet n'existe pas";
        }
    }

    public Set<String> extractUniqueIdentifiers(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        xpath.setNamespaceContext(new NamespaceContextMap(
                "r", "ddi:reusable:3_3",
                "ddi", "ddi:instance:3_3"
        ));

        Set<String> identifiers = new HashSet<>();

        // Extraire tous les UUIDs
        NodeList idNodes = (NodeList) xpath.evaluate("//r:ID", document, XPathConstants.NODESET);
        for (int i = 0; i < idNodes.getLength(); i++) {
            identifiers.add(idNodes.item(i).getTextContent());
        }

        return identifiers;
    }

    public JSONArray processUuidsAndFetchData(Set<String> uuids) throws ExceptionColecticaUnreachable, IOException {
        JSONArray dataArray = new JSONArray();

        for (String uuid : uuids) {
            ResponseEntity<?> responseEntity = searchColecticaFragmentByUuid(uuid);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String fragmentXml = (String) responseEntity.getBody();
                if (fragmentXml != null) {
                    JSONObject fragmentData = extractDataFromFragment(fragmentXml);
                        dataArray.put(fragmentData);
                }
            } else {
                String message = "Erreur ou élément non trouvé pour UUID:" + uuid;
                logger.log( Level.ERROR,message);
            }
        }

        return dataArray;
    }

    private JSONObject extractDataFromFragment(String fragmentXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(fragmentXml.getBytes(StandardCharsets.UTF_8)));
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            xpath.setNamespaceContext(new NamespaceContextMap("r", "ddi:reusable:3_3", "ddi", "ddi:instance:3_3"));

            Node firstElement = (Node) xpath.evaluate("/*/*[1]", document, XPathConstants.NODE);
            String itemType = firstElement.getLocalName();
            String id = xpath.evaluate("//r:ID", firstElement);
            String agencyFragment = xpath.evaluate("//r:Agency", firstElement);
            String version = xpath.evaluate("//r:Version", firstElement);

            JSONObject fragmentData = new JSONObject();
            fragmentData.put("Identifier", id);
            fragmentData.put("AgencyId", agencyFragment);
            fragmentData.put("Version", version);
            fragmentData.put("ItemType", itemType);

            return fragmentData;
        }  catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            // Log the exception
            logger.error("Error extracting data from fragment XML: {}", e.getMessage());
            // Return an appropriate response to the client
            return new JSONObject();
        }
    }


    private ResponseEntity<String> searchColecticaInstanceByUuid(String uuid) throws RMeSException, ParseException {
        // Configuration pour ignorer les cookies invalides
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build()) {

            return getStringResponseEntity(uuid, httpClient);
        } catch (IOException e) {
            throw new RMeSException(400, "Erreur lors de la communication avec le service : " + e.getMessage(), "");
        } catch (ExceptionColecticaUnreachable e) {
            throw new RMeSException(400, "Le service Colectica est injoignable : " + e.getMessage(), "");
        }
    }

    protected ResponseEntity<String> getStringResponseEntity(String uuid, CloseableHttpClient httpClient) throws ExceptionColecticaUnreachable, JsonProcessingException, RMeSException, ParseException {
        if (!uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89ABab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")) {
            throw new IllegalArgumentException("UUID invalide");
        }
        HttpGet httpGet = getHttpGet(uuid);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return getStringResponseEntity(response);
        } catch (IOException e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors de la requête vers Colectica.");
        }
    }

    private HttpGet getHttpGet(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RMeSException, ParseException {
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
        return httpGet;
    }

    private static ResponseEntity<String> getStringResponseEntity(CloseableHttpResponse response) throws IOException {
        byte[] bytes = EntityUtils.toByteArray(response.getEntity());
        String responseBody = new String(bytes, StandardCharsets.UTF_8);
        return ResponseEntity.ok(responseBody);
    }

    @Override
    public ResponseEntity<String> filteredSearchText(String index, String texte) {
        ResponseEntity<String> responseEntity = searchText(index, texte);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody =  responseEntity.getBody();
            String filteredResponse = filterAndTransformResponse(responseBody);
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;
        }
    }

    @Override
    public ResponseEntity<String> searchByType(String index, DDIItemType type) {
        ResponseEntity<String> responseEntity = searchType(index, String.valueOf(type.getUUID()).toLowerCase());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody =  responseEntity.getBody();
            String filteredResponse = filterAndTransformResponse(responseBody);
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;
        }
    }

    @Override
    public ResponseEntity<String> searchTexteByType(String index, String texte, DDIItemType type) {
        ResponseEntity<String> responseEntity = searchTextByType(index, texte, String.valueOf(type.getUUID()).toLowerCase());
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
             String responseBody = responseEntity.getBody();
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

            httpPost = getHttpPostToElastic(index, entity);


            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ERREUR_ELASTICSEARCH);
        }
    }

    private HttpPost getHttpPostToElastic(String index, StringEntity entity) {
        HttpPost httpPost;
        if (elasticHost.contains("kube")) {
            httpPost = new HttpPost("https://" + elasticHost + ":" + elasticHostPort + "/" + index + SEARCH);
            httpPost.setEntity(entity);
        }
        else {
            httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + SEARCH);
            httpPost.setHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
            httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpPost.setEntity(entity);
        }
        return httpPost;
    }


    private ResponseEntity<String> searchText(String index, String texte) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String encodedTexte = URLEncoder.encode(texte, StandardCharsets.UTF_8.toString());
            HttpGet httpGet;

            httpGet = getHttpGetToElastic(index, encodedTexte);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ERREUR_ELASTICSEARCH);
        }
    }

    private HttpGet getHttpGetToElastic(String index, String encodedTexte) {
        HttpGet httpGet;
        if (elasticHost.contains("kube")) {
            httpGet = new HttpGet("https://" + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q=" + encodedTexte);
        } else {
            httpGet = new HttpGet(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q=*" + encodedTexte + "*");
            httpGet.addHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
        }
        return httpGet;
    }

    private ResponseEntity<String> searchTextByType(String index, String texte, String type) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(HTTP + elasticHost + ":" + elasticHostPort + "/" + index + SEARCH);

            // Ajouter l'en-tête pour l'authentification et le type de contenu si nécessaire
            if (!elasticHost.contains("kube")) {
                httpPost.setHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
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
        } catch (HttpClientErrorException|HttpServerErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public String convertXmlToJson(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RMeSException, ParseException {

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
            } catch (RestClientException e) {
                // Log the exception
                logger.error("Error while calling the REST API: {}", e.getMessage());
                // Rethrow the exception or handle it appropriately
                throw new ExceptionColecticaUnreachable("Error while calling the REST API", e);
            }
        }
        return result;
    }

    @Override
    public String replaceXmlParameters(String inputXml, DDIItemType type, String label, int version, String name, String idepUtilisateur) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(inputXml)));

            String typeName = type.getName();
            NodeList typeNodes = document.getElementsByTagNameNS("ddi:logicalproduct:3_3", typeName);
            if (typeNodes.getLength() == 0) {
                return "Erreur : Aucun élément correspondant trouvé pour le type " + typeName;
            }

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

            Node nameNode = document2.getElementsByTagName("r:String").item(0);
            nameNode.setTextContent(name);

            Node labelNode = document2.getElementsByTagName("r:Content").item(0);
            labelNode.setTextContent(label);

            NodeList urnNodes = document2.getElementsByTagName("r:URN");
            for (int i = 0; i < urnNodes.getLength(); i++) {
                Node urnNode = urnNodes.item(i);
                if (urnNode instanceof Element) {
                    Element urnElement = (Element) urnNode;
                    String urnCode = urnElement.getTextContent();
                    urnElement.setTextContent(urnCode.substring(0, urnCode.lastIndexOf(":")) + ":" + version);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document2), new StreamResult(writer));

            // Ajout de la transformation XML vers JSON avec XSLT
            InputStream xsltStream2 = getClass().getResourceAsStream("/DDIxmltojsonForOneObject.xsl");
            String jsonResult = transformToJson(new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8)), xsltStream2, idepUtilisateur, version);

            return jsonResult;
        } catch (Exception e) {
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
        return xmlWriter.toString();
    }

    @Override
    public ResponseEntity<String> getByType(DDIItemType type) throws IOException, ExceptionColecticaUnreachable, ParseException {

        if (!serviceUrl.contains("kube")){
            token = getFreshToken();
        } else {
            String token2 = getAuthToken();
            token = extractAccessToken(token2);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, BEARER + token);

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
                return ResponseEntity.status(500).body(ERREUR_COLECTICA);
            }

            return ResponseEntity.ok(responseBody);
        }
    }

    @Override
    public ResponseEntity<String> sendUpdateColectica(String ddiUpdatingInJson, TransactionType transactionType) {
        try {
            RestTemplate restTemplate=new RestTemplate();
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
            if (authentToken != null && !authentToken.isEmpty()) {
                initTransactionHeaders.setBearerAuth(authentToken);
            }
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
                if (authentToken != null && !authentToken.isEmpty()) {
                    populateTransactionHeaders.setBearerAuth(authentToken);
                }
                // Créez un objet de demande pour peupler la transaction ici avec transactionId et DdiUpdatingInJson
                String updatedJson = ddiUpdatingInJson.replaceFirst("\\{", TRANSACTIONID + transactionId + ",");
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
                    if (authentToken != null && !authentToken.isEmpty()) {
                        commitTransactionHeaders.setBearerAuth(authentToken);
                    }
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite.");
        }
    }

    private int extractTransactionId(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("TransactionId").asInt();
        } catch (Exception e) {
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
    public ResponseEntity<String> transformFile(
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
            logger.log( Level.ERROR,"Le fichier a été modifié avec succès (ajout balise data) !");

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

            String resultFileName2 = UUID.randomUUID() + ".json";

            MultipartFile outputFile = processFile(file);
            logger.log(Level.getLevel(ERROR),"Le fichier a été modifié avec succès (ajout balise data) !");

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

    public static MultipartFile processFile(MultipartFile inputFile) throws IOException {
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
        return xmlWriter.toString();
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
        return xmlWriter.toString();
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
        return  xmlWriter.toString();
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite.");
        }
    }

    private boolean sendFileToApi(String fileContent, String token, String url) {


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

    private String getAuthToken() throws JsonProcessingException, RMeSException {
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
        }  else {
            throw new RMeSException(401, "Impossible d'obtenir le token d'authentification.", "toto");
        }
    }
    public static String extractAccessToken(String token) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(token);
            return (String) json.get("access_token");
        } catch (ParseException e) {
            // Log the exception
            logger.error("Error parsing JSON token: {}", e.getMessage());
            // Rethrow the exception
            throw e;
        } catch (ClassCastException e) {
            // Log the exception
            logger.error("Error casting JSON token: {}", e.getMessage());
            // Convert the exception to a ParseException
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, e);
        }
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
