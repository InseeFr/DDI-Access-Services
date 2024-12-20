package fr.insee.rmes.tocolecticaapi.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.FragmentData;
import fr.insee.rmes.tocolecticaapi.models.NamespaceContextMap;
import fr.insee.rmes.tocolecticaapi.models.RessourcePackage;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.transfoxsl.service.internal.DDIDerefencer;
import fr.insee.rmes.utils.ExportUtils;
import fr.insee.rmes.utils.XsltUtils;
import fr.insee.rmes.utils.export.XDocReport;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static fr.insee.rmes.transfoxsl.utils.RestClientUtils.readBodySafely;

@Service
@Slf4j
public record ColecticaServiceImpl(@NonNull KeycloakServices kc,
                                   ElasticService elasticService,
                                   RestClient restClient,
                                   XDocReport xdr,
                                   ExportUtils exportUtils,
                                   DDIDerefencer ddiDerefencer,
                                   @Value("${fr.insee.rmes.api.remote.metadata.url}")
                                   String serviceUrl,
                                   @Value("${fr.insee.rmes.api.remote.metadata.agency}")
                                   String agency

) implements ColecticaService {

    private static final String BEARER = "Bearer ";
    private static final String TRANSACTIONID = "{\"TransactionId\":";

    private RestClient initRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(100_000); // Temps de connexion en millisecondes
        factory.setReadTimeout(100_000); // Temps de lecture en millisecondes

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(URI.create(serviceUrl + "/").resolve("api/v1/"))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, BEARER)
                .defaultHeaders(headers -> headers.setBearerAuth(kc.getFreshToken()))
                .build();
    }

    @Override
    public String findFragmentByUuid(String uuid) {
        String response = getWithRestClient(URI.create("item/" + agency + "/" + uuid), MediaType.APPLICATION_JSON);
        return (new JSONObject(response)).getString("Item");

    }


    private String getWithRestClient(URI relativeUri, MediaType acceptedMediaType) {
        return restClient.get().uri(relativeUri)
                .accept(acceptedMediaType)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Bad request or inexisting resource", readBodySafely(response));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new RmesExceptionIO(response.getStatusCode().value(), "Error while calling Colectica", readBodySafely(response));
                })
                .body(String.class);
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
        } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing XML from Colectica", e.getMessage());
        }
    }

    static class XmlProcessing {

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

        if (!uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89ABab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}(\\/\\d+)?$")) {
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
    public List<Map<String, String>> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws Exception {
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

        int transactionId = extractTransactionId(postWithRestClient(URI.create("transaction"), Optional.empty(), MediaType.APPLICATION_JSON));
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
    public String sendDeleteColectica(String uuid, TransactionType transactionType) {
        RestTemplate restTemplate = new RestTemplate();
        String initTransactionUrl = serviceUrl + "/api/v1/transaction";
        String authentToken = kc.getFreshToken();

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
    public Resource exportCodeBookAsOdt(String ddiFile, File dicoVar) throws RmesException {
        //Prepare file
        byte[] odt = xdr.exportVariableBookInOdt(ddiFile, dicoVar);

        return new ByteArrayResource(odt);
    }

    @Override
    public ResponseEntity<Resource> getCodeBookExportV2(String ddi, String xslPatternFile) throws Exception {

        InputStream xslRemoveNameSpaces = getClass().getResourceAsStream("/xslTransformerFiles/remove-namespaces.xsl");
        InputStream xslCheckReference = getClass().getResourceAsStream("/xslTransformerFiles/check-references.xsl");
        String dicoCode = "/xslTransformerFiles/dico-codes.xsl";
        String zipRmes = "/xslTransformerFiles/dicoCodes/toZipForDicoCodes.zip";

        File ddiRemoveNameSpaces = File.createTempFile("ddiRemoveNameSpaces", ".xml");
        ddiRemoveNameSpaces.deleteOnExit();
        XsltUtils.transformerStringWithXsl(ddi, xslRemoveNameSpaces, ddiRemoveNameSpaces);

        File control = File.createTempFile("control", ".xml");
        control.deleteOnExit();
        XsltUtils.transformerFileWithXsl(ddiRemoveNameSpaces, xslCheckReference, control);

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

        return exportUtils.exportAsODT(Path.of("export.odt"), contentXML, dicoCode, xslPatternFile, zipRmes, "dicoVariable");
    }


    @Override
    public ResponseEntity<?> getCodeBookCheck(MultipartFile isCodeBook) throws Exception {
        if (isCodeBook == null)
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't generate codebook", "Codebook is null");
        InputStream codeBook = new BufferedInputStream(isCodeBook.getInputStream());
        InputStream xslRemoveNameSpaces = getClass().getResourceAsStream("/xslTransformerFiles/remove-namespaces.xsl");
        File ddiRemoveNameSpaces = File.createTempFile("ddiRemoveNameSpaces", ".xml");
        ddiRemoveNameSpaces.deleteOnExit();
        XsltUtils.transformerInputStreamWithXsl(codeBook, xslRemoveNameSpaces, ddiRemoveNameSpaces);

        InputStream xslCodeBookCheck = getClass().getResourceAsStream("/xslTransformerFiles/dico-codes-test-ddi-content.xsl");
        File codeBookCheck = File.createTempFile("codeBookCheck", ".xml");
        codeBookCheck.deleteOnExit();
        XsltUtils.transformerFileWithXsl(ddiRemoveNameSpaces, xslCodeBookCheck, codeBookCheck);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + codeBookCheck.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ByteArrayResource resourceByte = new ByteArrayResource(Files.readAllBytes(codeBookCheck.toPath()));

        if (resourceByte.contentLength() == 0) {
            return ResponseEntity.ok("Nothing is missing in the DDI file " + isCodeBook.getOriginalFilename());
        }
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(codeBookCheck.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resourceByte);
    }


}
