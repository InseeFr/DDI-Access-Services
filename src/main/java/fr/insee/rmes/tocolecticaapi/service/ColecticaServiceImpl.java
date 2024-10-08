package fr.insee.rmes.tocolecticaapi.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.*;
import fr.insee.rmes.tocolecticaapi.randomUUID;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.utils.ExportUtils;
import fr.insee.rmes.utils.FilesUtils;
import fr.insee.rmes.utils.XMLUtils;
import fr.insee.rmes.utils.export.XDocReport;
import lombok.NonNull;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
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
import javax.xml.namespace.NamespaceContext;
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
import java.nio.file.Files;
import java.util.*;

import static fr.insee.rmes.transfoxsl.controller.TransformationController.DEREFERENCE_XSL;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ERROR;

@Service
public class ColecticaServiceImpl implements ColecticaService {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_XML = "application/xml";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String VERSION = "version";
    private static final String SEARCH = "/_search";
    private static final String APIKEYHEADER = "apiKey ";
    private static final String ERREUR_COLECTICA = "Une erreur s'est produite lors de la requête vers Colectica.";
    private static final String ERREUR_ELASTICSEARCH = "Une erreur s'est produite lors de la requête Elasticsearch.";
    private static final String TRANSACTIONID = "{\"TransactionId\":";

    private static final String ATTACHMENT = "attachment";

    static final Logger logger = LogManager.getLogger(ColecticaServiceImpl.class);
    public static final String DISALLOW_DOCTYPE_DECL = "http://javax.xml.transform.TransformerFactory/feature/disallow-doctype-decl";
    public static final String DISALLOW_DOCTYPE_DECL1 = "http://apache.org/xml/features/disallow-doctype-decl";
    public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    public static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";


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
    @Value("${fr.insee.rmes.elasticsearch.url}")
    private String  elasticUrl;

    @Value("${fr.insee.rmes.elasticsearch.apiId}")
    private String apiId;

    @Value("${fr.insee.rmes.elasticsearch.apikey}")
    private String apiKey;

    @Autowired
    public ElasticsearchClient elasticsearchClient;
    @Autowired
    public  RestTemplate restTemplate;
    @Autowired
    XDocReport xdr;
    @Autowired
    ExportUtils exportUtils;

    public CloseableHttpClient httpClient;

    @Autowired
    private XsltTransformationService xsltTransformationService;
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
    @Autowired
    public ColecticaServiceImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
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
            throw new RmesExceptionIO(400,"erreur durant la lecture du jeton","");
        }
    }

    private HttpGet getGetSearchColecticaFragmentByUuid(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RmesExceptionIO, ParseException {
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
    public ResponseEntity<String> findInstanceByUuid(String uuid) throws RmesExceptionIO, ParseException {
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
        // Appel au service Colectica pour obtenir l'instance
        ResponseEntity<?> responseEntity = searchColecticaInstanceByUuid(uuid);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = (String) responseEntity.getBody();

            // Transformer la réponse XML avec le service de déréférencement
            String dereferencedXml = dereferenceXml(responseBody);

            // Convertir le XML transformé en InputStream pour l'analyse
            InputStream inputStream = new ByteArrayInputStream(dereferencedXml.getBytes(StandardCharsets.UTF_8));

            // Appeler la méthode parseDereferencedXmlWithEnum pour générer le tableau JSON
            JSONArray resultArray = parseDereferencedXmlWithEnum(inputStream);

            // Retourner le résultat final en JSON formaté
            return resultArray.toString(4);
        } else {
            return "L'objet n'existe pas";
        }
    }

    // Nouvelle méthode pour déréférencer le XML directement
    private String dereferenceXml(String xmlResponse) throws Exception {
        // Convertir le XML en InputStream
        InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8));

        // Appeler le service de transformation XSLT pour effectuer le déréférencement
        List<String> outputText = xsltTransformationService.transform(inputStream, DEREFERENCE_XSL, false);

        // Combiner le résultat en une seule chaîne de caractères
        return String.join("\n", outputText);
    }

    public JSONArray parseDereferencedXmlWithEnum(InputStream inputStream) throws Exception {
        // Créer et configurer le DocumentBuilderFactory de manière sécurisée
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Désactiver l'accès aux entités externes pour éviter les attaques XXE
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        // Construire le document XML
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);

        // Initialiser XPath avec le contexte de l'espace de noms
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        // Configurer les espaces de noms en utilisant NamespaceContextMap existant
        NamespaceContext nsContext = new NamespaceContextMap(
                "ddi", "ddi:instance:3_3",
                "r", "ddi:reusable:3_3"
        );
        xpath.setNamespaceContext(nsContext);

        JSONArray jsonArray = new JSONArray();

        // Parcourir toutes les balises dans le document XML
        NodeList allNodes = document.getElementsByTagName("*");

        for (int i = 0; i < allNodes.getLength(); i++) {
            Element element = (Element) allNodes.item(i);
            String tagName = element.getLocalName();  // Utiliser getLocalName pour ignorer l'espace de noms

            // Vérifier si le nom de la balise existe dans l'enum
            DDIItemType itemType = DDIItemType.searchByName(tagName);

            if (itemType != null) {
                // Extraire les informations r:ID, r:Agency, r:Version en tenant compte de l'espace de noms
                String identifier = xpath.evaluate(".//r:ID", element);
                String agencyId = xpath.evaluate(".//r:Agency", element);
                String version = xpath.evaluate(".//r:Version", element);

                // Si les valeurs sont trouvées, les ajouter à l'objet JSON
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Identifier", identifier != null ? identifier : "");
                jsonObject.put("AgencyId", agencyId != null ? agencyId : "");
                jsonObject.put("Version", version != null ? version : "");
                jsonObject.put("ItemType", itemType.getName());

                // Ajouter l'objet JSON au tableau
                jsonArray.put(jsonObject);
            }
        }

        return jsonArray;
    }



    public Set<String> extractUniqueIdentifiers(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Désactivation de l'accès aux entités externes pour la sécurité (prévention des attaques XXE)
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(DISALLOW_DOCTYPE_DECL1, true);
        factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
        factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

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

            // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(DISALLOW_DOCTYPE_DECL1, true);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

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
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            // Log the exception
            logger.error("Error extracting data from fragment XML: {}", e.getMessage());

            // Return an appropriate response to the client
            return new JSONObject();
        }
    }


    private ResponseEntity<String> searchColecticaInstanceByUuid(String uuid) throws RmesExceptionIO, ParseException {
        // Configuration pour ignorer les cookies invalides
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build()) {

            return getStringResponseEntity(uuid, httpClient);
        } catch (IOException e) {
            throw new RmesExceptionIO(400, "Erreur lors de la communication avec le service : " + e.getMessage(), "");
        } catch (ExceptionColecticaUnreachable e) {
            throw new RmesExceptionIO(400, "Le service Colectica est injoignable : " + e.getMessage(), "");
        }
    }

    protected ResponseEntity<String> getStringResponseEntity(String uuid, CloseableHttpClient httpClient) throws ExceptionColecticaUnreachable, JsonProcessingException, RmesExceptionIO, ParseException {
        if (!uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89ABab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}(\\/\\d+)?$")) {
            throw new IllegalArgumentException("UUID invalide");
        }
        HttpGet httpGet = getHttpGet(uuid);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            return getStringResponseEntity(response);
        } catch (IOException e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ERREUR_COLECTICA);
        }
    }

    private HttpGet getHttpGet(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RmesExceptionIO, ParseException {
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
            logger.info("Réponse filtrée avec succès pour searchTexte");
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
            logger.info("Réponse filtrée avec succès pour searchByType");
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
            logger.info("Réponse filtrée avec succès pour searchTexteByType");
            return ResponseEntity.ok(filteredResponse);
        } else {
            return responseEntity;
        }
    }


    private ResponseEntity<String> searchType(String index,String type) {
        try  {
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
            httpPost = new HttpPost(elasticUrl + "/" + index + SEARCH);
            httpPost.setHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
            httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpPost.setEntity(entity);
        }
        return httpPost;
    }

    private ResponseEntity<String> searchText(String index, String texte) {
        try {
            String encodedTexte = URLEncoder.encode(texte, StandardCharsets.UTF_8.toString());
            HttpGet httpGet = getHttpGetToElastic(index,encodedTexte);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return ResponseEntity.ok(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la connexion au service.");
        }
    }

    private HttpGet getHttpGetToElastic(String index, String encodedTexte) {
        HttpGet httpGet;
        if (elasticHost.contains("kube")) {
            httpGet = new HttpGet("https://" + elasticHost + ":" + elasticHostPort + "/" + index + "/_search?q=" + encodedTexte);
        } else {
            httpGet = new HttpGet(elasticUrl + "/" + index + "/_search?q=*" + encodedTexte + "*");
            httpGet.addHeader(AUTHORIZATION, APIKEYHEADER + apiKey);
        }
        return httpGet;
    }

    private ResponseEntity<String> searchTextByType(String index, String texte, String type) {
        try  {
            HttpPost httpPost = new HttpPost(elasticUrl + "/" + index + SEARCH);

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
                String version = source.path("itemVersion").asText();  // Récupérer la version

                // Filtrer les éléments avec name ou label non vide
                if (!name.isEmpty() || !label.isEmpty()) {
                    ObjectNode filteredSource = objectMapper.createObjectNode();
                    filteredSource.put("name", name);
                    filteredSource.put("label", label);
                    filteredSource.put("version", version);  // Ajouter la version à l'objet JSON

                    String versionlessId = source.path("versionlessId").asText();
                    if (versionlessId.startsWith("fr.insee:")) {
                        versionlessId = versionlessId.substring("fr.insee:".length());
                    }
                    filteredSource.put("Id", versionlessId);

                    filteredHitsArray.add(filteredSource);
                }
            }

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
    public String convertXmlToJson(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RmesExceptionIO, ParseException {

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

            // Désactivation des entités externes pour des raisons de sécurité (prévention des attaques XXE)
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(DISALLOW_DOCTYPE_DECL1, true);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(inputXml)));

            String typeName = type.getName();
            NodeList typeNodes = document.getElementsByTagNameNS("ddi:logicalproduct:3_3", typeName);

            if (typeNodes.getLength() == 0) {
                return "Erreur : Aucun élément correspondant trouvé pour le type " + typeName;
            }

            Element typeNameDocument = (Element) typeNodes.item(0);

            if (!typeName.equals(typeNameDocument.getNodeName())) {
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

            // Désactivation des entités externes dans TransformerFactory pour éviter les attaques XXE
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            transformerFactory.setFeature(DISALLOW_DOCTYPE_DECL, true);

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
        // Créer un transformateur XSLT sécurisé
        TransformerFactory factory = TransformerFactory.newInstance();

        // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(DISALLOW_DOCTYPE_DECL, true);

        Source xslt = new StreamSource(xsltFileJson);

        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("idepUtilisateur", idepUtilisateur);
        transformer.setParameter(VERSION, version);

        // Effectuer la transformation
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

        // Modifie le contenu en ajoutant des balises <data>
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

        // Création d'un fichier temporaire pour stocker le contenu modifié
        File tempFile = File.createTempFile("upload_", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(modifiedContent);
        }

        // Créer un MultipartFile personnalisé à partir du fichier temporaire
        return new CustomMultipartFile(tempFile, inputFile.getOriginalFilename(), inputFile.getContentType());
    }

    private String transformToXml(MultipartFile file, InputStream xsltFile, String idValue, String nomenclatureName,
                                  String suggesterDescription, String timbre, String version) throws IOException, TransformerException {

        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

        // Saxon ne supporte pas certaines fonctionnalités comme disallow-doctype-decl, il n'est pas nécessaire de définir cette propriété

        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;
        net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();
        Processor processor = (Processor) saxonConfig.getProcessor();

        ExtensionFunction randomUUID = new randomUUID();
        processor.registerExtensionFunction(randomUUID);

        Source xslt = new StreamSource(xsltFile);
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("idValue", idValue);
        transformer.setParameter("suggesterName", nomenclatureName);
        transformer.setParameter("suggesterDescription", suggesterDescription);
        transformer.setParameter("timbre", timbre);
        transformer.setParameter(VERSION, version);

        StreamSource text = new StreamSource(file.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);

        transformer.transform(text, xmlResult);

        return xmlWriter.toString();
    }

    private String transformToXmlForComplexList(MultipartFile file, InputStream xsltFile, String idValue, String nomenclatureName,
                                                String suggesterDescription, String timbre, String version, String principale,
                                                List<String> secondaire, List<String> labelSecondaire)
            throws IOException, TransformerException {

        // Créer un transformateur XSLT sécurisé
        TransformerFactory factory = TransformerFactory.newInstance();

        // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(DISALLOW_DOCTYPE_DECL, true);

        // Utiliser TransformerFactoryImpl pour permettre des personnalisations supplémentaires via Saxon
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) factory;

        // Obtenir la configuration Saxon
        net.sf.saxon.Configuration saxonConfig = tFactoryImpl.getConfiguration();

        // Obtenir le processeur Saxon
        Processor processor = (Processor) saxonConfig.getProcessor();

        // Enregistrer une fonction d'extension personnalisée
        ExtensionFunction randomUUID = new randomUUID();
        processor.registerExtensionFunction(randomUUID);

        // Préparer la transformation XSLT
        Source xslt = new StreamSource(xsltFile);
        Transformer transformer = factory.newTransformer(xslt);

        // Passer les paramètres à la transformation
        transformer.setParameter("idValue", idValue);
        transformer.setParameter("suggesterName", nomenclatureName);
        transformer.setParameter("suggesterDescription", suggesterDescription);
        transformer.setParameter("timbre", timbre);
        transformer.setParameter(VERSION, version);
        transformer.setParameter("principale", principale);
        transformer.setParameter("secondaire", secondaire);
        transformer.setParameter("labelSecondaire", labelSecondaire);

        // Exécuter la transformation
        StreamSource text = new StreamSource(file.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);

        transformer.transform(text, xmlResult);

        return xmlWriter.toString();
    }


    public String transformToJson(Resource resultResource, InputStream xsltFileJson, String idepUtilisateur) throws IOException, TransformerException {
        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
        Source xslt = new StreamSource(xsltFileJson);

        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("idepUtilisateur", idepUtilisateur);

        StreamSource text = new StreamSource(resultResource.getInputStream());
        StringWriter xmlWriter = new StringWriter();
        StreamResult xmlResult = new StreamResult(xmlWriter);

        transformer.transform(text, xmlResult);

        return xmlWriter.toString();
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

    private String getAuthToken() throws JsonProcessingException, RmesExceptionIO {
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
        }  else {
            throw new RmesExceptionIO(401, "Impossible d'obtenir le token d'authentification.", "toto");
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

    @Override
    public String sendDeleteColectica(String uuid, TransactionType transactionType) throws JsonProcessingException, ExceptionColecticaUnreachable, RmesExceptionIO, ParseException {
        RestTemplate restTemplate = new RestTemplate();
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
            String responseBodyInit = initTransactionResponse.getBody();
            int transactionId = extractTransactionId(responseBodyInit);

            //appel au service DDI instance par uuuid
            ResponseEntity<?> test = searchColecticaInstanceByUuid(uuid);
            // transformation de la reponse à la requete précédente grâce à une transformation xslt: on obtient une String
            // Transformation de la réponse en JSON
            String responseBody = (String) test.getBody();
            JSONArray result = extractIdentifiersFromDiiInstance(responseBody);
            // Créer l'objet JSON pour la requête
            JSONObject deleteTransactionBody = new JSONObject();
            deleteTransactionBody.put("identifiers", result); // Ajouter les identifiants extraits
            deleteTransactionBody.put("setIdentifiers", new JSONArray()); // Tableau vide si nécessaire
            deleteTransactionBody.put("transactionIds", new JSONArray(Arrays.asList(transactionId))); // Ajouter l'ID de transaction
            deleteTransactionBody.put("deleteType", 0); // Ajouter le type de suppression

            // Convertir en chaîne JSON pour la requête
            JsonMapper resultFinal = new JsonMapper();
            String deleteTransactionRequestBody = deleteTransactionBody.toString();
            System.out.println("deleteTransactionRequestBody: " + deleteTransactionRequestBody);
            // lancement de la requete à destination de l'api colectica
            String deleteTransactionUrl = serviceUrl + "/api/v1/item/_delete";
            // Configuration des en-têtes de la requête
            HttpHeaders deleteTransactionHeaders = new HttpHeaders();
            deleteTransactionHeaders.setContentType(MediaType.APPLICATION_JSON);
            deleteTransactionHeaders.setBearerAuth(authentToken);

            // Création et envoi de la requête
            HttpEntity<String> deleteTransactionRequest = new HttpEntity<>(deleteTransactionRequestBody, deleteTransactionHeaders);
            ResponseEntity<String> deleteTransactionResponse = restTemplate.exchange(
                    deleteTransactionUrl,
                    HttpMethod.POST,
                    deleteTransactionRequest,
                    String.class
            );
            return deleteTransactionResponse.getStatusCode().toString();
        }
        return ("error no TransactionId");
    }
    private JSONArray extractIdentifiersFromDiiInstance(String fragmentXml) {
        Set<FragmentData> uniqueData = new HashSet<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(DISALLOW_DOCTYPE_DECL1, true);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(fragmentXml.getBytes(StandardCharsets.UTF_8)));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            xpath.setNamespaceContext(new NamespaceContextMap("r", "ddi:reusable:3_3", "ddi", "ddi:instance:3_3"));

            // Sélectionner tous les nœuds ayant un ID, Agency ou Version
            NodeList nodes = (NodeList) xpath.evaluate("//*[local-name()='ID' or local-name()='Agency' or local-name()='Version']", document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node currentNode = nodes.item(i).getParentNode();

                String id = xpath.evaluate("r:ID", currentNode);
                String agency = xpath.evaluate("r:Agency", currentNode);
                String version = xpath.evaluate("r:Version", currentNode);

                FragmentData fragmentData = new FragmentData(id, agency, version);
                uniqueData.add(fragmentData);
            }

            // Convertir Set en JSONArray
            JSONArray allFragmentsData = new JSONArray();
            for (FragmentData data : uniqueData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Identifier", data.getIdentifier());
                jsonObject.put("AgencyId", data.getAgencyId());
                jsonObject.put("Version", data.getVersion());
                allFragmentsData.put(jsonObject);
            }

            return allFragmentsData;

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }



    @Override
    public ResponseEntity<Resource> getCodeBookExport(String ddiFile, File dicoVar,  String accept) throws RmesException {
        //Prepare file
        byte[] odt = xdr.exportVariableBookInOdt(ddiFile, dicoVar);

        ByteArrayResource resource = new ByteArrayResource(odt);

        //Prepare response headers
        String fileName = "Codebook"+ FilesUtils.getExtension(accept);
        ContentDisposition content = ContentDisposition.builder(ATTACHMENT).filename(fileName).build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setAccept(List.of(MediaType.ALL));
        responseHeaders.setContentDisposition(content);
        responseHeaders.setContentType(new MediaType("application","vnd.oasis.opendocument.text"));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    @Override
    public ResponseEntity<Resource> getCodeBookExportV2(String ddi, String xslPatternFile) throws Exception {

        InputStream xslRemoveNameSpaces = getClass().getResourceAsStream("/xslTransformerFiles/remove-namespaces.xsl");
        InputStream xslCheckReference = getClass().getResourceAsStream("/xslTransformerFiles/check-references.xsl");
        String dicoCode = "/xslTransformerFiles/dico-codes.xsl";
        String zipRmes = "/xslTransformerFiles/dicoCodes/toZipForDicoCodes.zip";

        File ddiRemoveNameSpaces = File.createTempFile("ddiRemoveNameSpaces", ".xml");
        ddiRemoveNameSpaces.deleteOnExit();
        transformerStringWithXsl(ddi, xslRemoveNameSpaces, ddiRemoveNameSpaces);

        File control = File.createTempFile("control", ".xml");
        control.deleteOnExit();
        transformerFileWithXsl(ddiRemoveNameSpaces, xslCheckReference, control);

        // Créer un DocumentBuilderFactory sécurisé
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature(DISALLOW_DOCTYPE_DECL1, true);
        dbf.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
        dbf.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(control);

        String checkResult = doc.getDocumentElement().getNodeName();

        if (!"OK".equals(checkResult)) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + control.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            ByteArrayResource resourceByte = new ByteArrayResource(Files.readAllBytes(control.toPath()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(control.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resourceByte);
        }

        HashMap<String, String> contentXML = new HashMap<>();
        contentXML.put("ddi-file", Files.readString(ddiRemoveNameSpaces.toPath()));

        return exportUtils.exportAsODT("export.odt", contentXML, dicoCode, xslPatternFile, zipRmes, "dicoVariable");
    }

    public static void transformerStringWithXsl(String ddi,InputStream xslRemoveNameSpaces, File output) throws Exception{
        Source stylesheetSource = new StreamSource(xslRemoveNameSpaces);
        Transformer transformer = XMLUtils.getTransformerFactory().newTransformer(stylesheetSource);
        Source inputSource = new StreamSource(new StringReader(ddi));
        Result outputResult = new StreamResult(output);
        transformer.transform(inputSource, outputResult);
    }

    public static void transformerFileWithXsl(File input,InputStream xslCheckReference, File output) throws Exception {
        Source stylesheetSource = new StreamSource(xslCheckReference);
        Transformer transformer = XMLUtils.getTransformerFactory().newTransformer(stylesheetSource);
        Source inputSource = new StreamSource(input);
        Result outputResult = new StreamResult(output);
        transformer.transform(inputSource, outputResult);
    }


    @Override
    public ResponseEntity<?> getCodeBookCheck(MultipartFile isCodeBook) throws Exception {
        if (isCodeBook == null) throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't generate codebook","Codebook is null");
        InputStream codeBook= new BufferedInputStream(isCodeBook.getInputStream());
        InputStream xslRemoveNameSpaces = getClass().getResourceAsStream("/xslTransformerFiles/remove-namespaces.xsl");
        File ddiRemoveNameSpaces = File.createTempFile("ddiRemoveNameSpaces", ".xml");
        ddiRemoveNameSpaces.deleteOnExit();
        transformerInputStreamWithXsl(codeBook,xslRemoveNameSpaces,ddiRemoveNameSpaces);

        InputStream xslCodeBookCheck = getClass().getResourceAsStream("/xslTransformerFiles/dico-codes-test-ddi-content.xsl");
        File codeBookCheck = File.createTempFile("codeBookCheck", ".xml");
        codeBookCheck.deleteOnExit();
        transformerFileWithXsl(ddiRemoveNameSpaces,xslCodeBookCheck,codeBookCheck);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + codeBookCheck.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ByteArrayResource resourceByte = new ByteArrayResource(Files.readAllBytes(codeBookCheck.toPath()));

        if (resourceByte.contentLength()==0) {
            return ResponseEntity.ok("Nothing is missing in the DDI file " + isCodeBook.getOriginalFilename());
        }
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(codeBookCheck.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resourceByte);
    }


    public static void transformerInputStreamWithXsl(InputStream input,InputStream xslCheckReference, File output) throws Exception {
        Source stylesheetSource = new StreamSource(xslCheckReference);
        Transformer transformer = XMLUtils.getTransformerFactory().newTransformer(stylesheetSource);
        Source inputSource = new StreamSource(input);
        Result outputResult = new StreamResult(output);
        transformer.transform(inputSource, outputResult);
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
