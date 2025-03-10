package fr.insee.rmes.tocolecticaapi.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.exceptions.RmesBadRequestException;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.NamespaceContextMap;
import fr.insee.rmes.tocolecticaapi.models.RessourcePackage;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.transfoxsl.service.internal.DDIDerefencer;
import fr.insee.rmes.utils.ExportUtils;
import fr.insee.rmes.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static fr.insee.rmes.transfoxsl.service.XsltTransformationService.extractIdentifiersFromDiiInstance;
import static fr.insee.rmes.transfoxsl.utils.RestClientUtils.readBodySafely;

@Service
@Slf4j
public record ColecticaServiceImpl(ElasticService elasticService,
                                   RestClient restClient,
                                   ExportUtils exportUtils,
                                   DDIDerefencer ddiDerefencer,
                                   String agency

) implements ColecticaService {

    private static final String TRANSACTIONID = "{\"TransactionId\":";

    @Autowired
    public ColecticaServiceImpl(KeycloakServices keycloakServices,
                                ElasticService elasticService,
                                ExportUtils exportUtils,
                                DDIDerefencer ddiDerefencer,
                                @Value("${fr.insee.rmes.api.remote.metadata.url}")
                                    String serviceUrl,
                                @Value("${fr.insee.rmes.api.remote.metadata.agency}")
                                    String agency){
        this(elasticService, initRestClient(keycloakServices, serviceUrl), exportUtils, ddiDerefencer, agency);
    }

    private static RestClient initRestClient(KeycloakServices keycloakServices, String serviceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(100_000); // Temps de connexion en millisecondes
        factory.setReadTimeout(100_000); // Temps de lecture en millisecondes

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(URI.create(serviceUrl + "/").resolve("api/v1/"))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .apply(keycloakServices.configureOidcClientAutoAuthentication())
                .build();
    }

    @Override
    public String findFragmentByUuid(String uuid) {
        String response = getWithRestClient(URI.create("item/" + agency + "/" + uuid), MediaType.APPLICATION_JSON);
        return (new JSONObject(response)).getString("Item");

    }

    String getWithRestClient(URI relativeUri, MediaType acceptedMediaType) {
        log.debug("Start processing GET {} as {}" ,relativeUri, acceptedMediaType);
        RestClient.ResponseSpec response = restClient.get().uri(relativeUri)
                .accept(acceptedMediaType)
                .retrieve();
        byte[] rawResponseContent = response
                .onStatus(HttpStatusCode::is4xxClientError, (request, httpResponse) -> {
                    throw new RmesExceptionIO(httpResponse.getStatusCode().value(), "Bad request or inexisting resource", readBodySafely(httpResponse));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, httpResponse) -> {
                    throw new RmesExceptionIO(httpResponse.getStatusCode().value(), "Error while calling Colectica", readBodySafely(httpResponse));
                })
                .body(byte[].class);
        String filteredResponse = HttpUtils.filterBOM(rawResponseContent);
        log.trace("Filtered response: {}", filteredResponse);
        return filteredResponse;
    }

    private String postWithRestClient(URI relativeUri, Optional<String> requestBody, MediaType contentType) {
        RestClient.RequestBodySpec postRequest = restClient.post()
                .uri(relativeUri)
                .contentType(contentType);
        requestBody.ifPresent(postRequest::body);
        return postRequest
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Bad request or inexisting resource", readBodySafely(response));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Error while calling Colectica", readBodySafely(response));
                })
                .body(String.class);
    }

    @Override
    public JSONArray findFragmentByUuidWithChildren(String uuid) throws RmesException {
        String instance = searchColecticaInstanceByUuid(uuid);
        try {
            return XmlProcessing.parseDereferencedXmlWithEnum(this.ddiDerefencer.intermediateDereference(instance));
        } catch (IOException | XsltTransformationException | ParserConfigurationException | SAXException |
                 XPathExpressionException e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing XML from Colectica", e.getMessage());
        }
    }

    static class XmlProcessing {

        private XmlProcessing(){}

        private static JSONArray parseDereferencedXmlWithEnum(byte[] input) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
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
            Document document = builder.parse(new ByteArrayInputStream(input));

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
    }

    @Override
    public String searchColecticaInstanceByUuid(String uuid) throws RmesException {

        if (!uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89ABab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}(/\\d+)?$")) {
            throw new RmesException(HttpStatus.BAD_REQUEST, "UUID invalide", uuid + " ne respecte pas le pattern d'un uuid");
        }
        return getWithRestClient(URI.create("ddiset/" + agency + "/" + uuid), MediaType.APPLICATION_XML);
    }


    @Override
    public String filteredSearchText(String index, String texte) throws RmesException {
        String filteredResponse = filterAndTransformResponse(
                elasticService.search(index, URLEncoder.encode(texte, StandardCharsets.UTF_8))
        );
        log.info("Réponse filtrée avec succès pour searchTexte");
        return filteredResponse;
    }

    @Override
    public String searchByType(String index, DDIItemType type) throws RmesException {
        String filteredResponse = filterAndTransformResponse(elasticService.searchType(index, type));
        log.info("Réponse filtrée avec succès pour searchByType");
        return filteredResponse;

    }

    @Override
    public String searchTexteByType(String index, String texte, DDIItemType type) throws RmesException {
        String filteredResponse = filterAndTransformResponse(elasticService.searchTextByType(index, texte, type));
        log.info("Réponse filtrée avec succès pour searchTexteByType");
        return filteredResponse;
    }

    private static String filterAndTransformResponse(String json) throws RmesException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(json);
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
        } catch (JsonProcessingException e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Error `" + e.getMessage() + "` while processing json", json);
        }
    }

    @Override
    public List<Map<String, String>> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonNode jsonNode = objectMapper.readTree(findJsonsetByIdentifier(identifier));
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
        return idLabelPairs;
    }

    private String findJsonsetByIdentifier(String identifier) {
        return getWithRestClient(URI.create("jsonset/fr.insee/" + identifier), MediaType.APPLICATION_JSON);
    }

    @Override
    public String getRessourcePackage(String uuid) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        RessourcePackage ressourcePackage = mapper.readValue(findJsonsetByIdentifier(uuid),
                RessourcePackage.class);
        List<Map<String, String>> countriesWithCodesAndLabels = ressourcePackage.mapCodesToLabels();
        ObjectMapper mapperFinal = new ObjectMapper();
        return mapperFinal.writeValueAsString(countriesWithCodesAndLabels);
    }

    @Override
    public String getByType(DDIItemType type) {
        return postWithRestClient(URI.create("_query"),
                Optional.of("{ \"ItemTypes\": [\"" + type.getUUID().toLowerCase() + "\"] }"),
                MediaType.valueOf("application/json-patch+json"));
    }

    @Override
    public void sendUpdateColectica(String ddiUpdatingInJson, TransactionType transactionType) throws RmesException {

        int transactionId = createTransaction();
        if (populateTransaction(ddiUpdatingInJson, transactionId)) {
            if (!commitTransaction(transactionType, transactionId)) {
                cancelTransaction(transactionId);
                throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction failed", "Transaction id : " + transactionId);
            }
        } else {
            cancelTransaction(transactionId);
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Fail to populate transaction", "Transaction id : " + transactionId);
        }
    }

    private int createTransaction() throws RmesException {
        return extractTransactionId(postWithRestClient(URI.create("transaction"), Optional.empty(), MediaType.APPLICATION_JSON));
    }

    private boolean commitTransaction(TransactionType transactionType, int transactionId) {
        String commitTransactionRequestBody = TRANSACTIONID + transactionId + ",\"TransactionType\":\"" + transactionType + "\"}";
        try {
            postWithRestClient(URI.create("transaction/_commitTransaction"), Optional.of(commitTransactionRequestBody), MediaType.APPLICATION_JSON);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    private boolean populateTransaction(String ddiUpdatingInJson, int transactionId) {
        String updatedJson = ddiUpdatingInJson.replaceFirst("\\{", TRANSACTIONID + transactionId + ",");
        try {
            postWithRestClient(URI.create("transaction/_addItemsToTransaction"), Optional.of(updatedJson), MediaType.APPLICATION_JSON);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    private int extractTransactionId(String json) throws RmesException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.get("TransactionId").asInt();
        } catch (Exception e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Fail transacation initialisation", e.getMessage());
        }
    }

    private void cancelTransaction(int transactionId) throws RmesException {
        String cancelTransactionRequestBody = TRANSACTIONID + transactionId + "}";
        try {
            postWithRestClient(URI.create("transaction/_cancelTransaction"), Optional.of(cancelTransactionRequestBody), MediaType.APPLICATION_JSON);
        }catch (Exception e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "GRAVE : fail to rollback transaction : database may be inconsistent", "transaction id "+transactionId);
        }
    }

    @Override
    public void sendDeleteColectica(String uuid, TransactionType transactionType) throws RmesException {
        int transactionId;
        try{
            transactionId = createTransaction();
        }catch (RmesException e){
            log.error("Error while creating delete transaction : "+e.getMessage(), e);
            throw new RmesBadRequestException("error no TransactionId");
        }

        String deleteTransactionRequestBody = bodyForDeleteWithPostRequest(uuid, transactionId);

        log.debug("deleteTransactionRequestBody: {}", deleteTransactionRequestBody);
        // lancement de la requete à destination de l'api colectica
        postWithRestClient(URI.create("item/_delete"), Optional.of(deleteTransactionRequestBody), MediaType.APPLICATION_JSON);
    }

    private String bodyForDeleteWithPostRequest(String uuid, int transactionId) throws RmesException {
        //appel au service DDI instance par uuuid
        String instance = searchColecticaInstanceByUuid(uuid);
        // transformation de la reponse à la requete précédente grâce à une transformation xslt: on obtient une String
        // Transformation de la réponse en JSON
        JSONArray result = extractIdentifiersFromDiiInstance(instance);
        // Créer l'objet JSON pour la requête
        JSONObject deleteTransactionBody = new JSONObject();
        deleteTransactionBody.put("identifiers", result); // Ajouter les identifiants extraits
        deleteTransactionBody.put("setIdentifiers", new JSONArray()); // Tableau vide si nécessaire
        deleteTransactionBody.put("transactionIds", new JSONArray(List.of(transactionId))); // Ajouter l'ID de transaction
        deleteTransactionBody.put("deleteType", 0); // Ajouter le type de suppression
        // Convertir en chaîne JSON pour la requête
        return deleteTransactionBody.toString();
    }


}
