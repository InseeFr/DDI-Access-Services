package fr.insee.rmes.ToColecticaApi.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.ToColecticaApi.models.AuthRequest;
import fr.insee.rmes.ToColecticaApi.models.CustomMultipartFile;
import fr.insee.rmes.ToColecticaApi.models.Items;
import fr.insee.rmes.ToColecticaApi.randomUUID;
import fr.insee.rmes.config.keycloak.KeycloakServices;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.metadata.model.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import lombok.NonNull;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import static jakarta.xml.bind.JAXB.unmarshal;

@Controller
@RequestMapping("/postItem")
@Tag(name= "Colectica-suggesters ",description = "Service pour gerer les suggesters dans Colectica")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "406", description = "Not Acceptable"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
public class PostItem {

    final static Logger log = LogManager.getLogger(PostItem.class);
    private final ResourceLoader resourceLoader;

    @NonNull
    @Autowired
    private KeycloakServices kc;

    private static String token;

    @Value("${auth.api.url}")
    private String authApiUrl;

    @Value("${item.api.url}")
    private String itemApiUrl;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Value("${fr.insee.rmes.api.remote.metadata.url}")
    private String urlColectica;


    @Autowired
    public PostItem(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostMapping(value = "/transformJsonToJsonForAPi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "transform an JSON Codelist (Id,Label) to an another json for Colectica API ",
            description = "tranform a codeList in json to another json with DDI item inside")
    public ResponseEntity<?> transformFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("nom metier") String idValue,
                                           @RequestParam("label") String nomenclatureName,
                                           @RequestParam("description") String suggesterDescription,
                                           @RequestParam("version") String version,
                                           @RequestParam("idepUtilisateur") String idepUtilisateur,
                                           @RequestParam("timbre") String timbre) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String resultFileName2 = UUID.randomUUID().toString() + ".json";

            MultipartFile outputFile = processFile(file);
            System.out.println("Le fichier a été modifié avec succès (ajout balise data) !");

            InputStream xsltStream1 = getClass().getResourceAsStream("/jsontoDDIXML.xsl");
            String xmlContent = transformToXml(outputFile, xsltStream1, idValue, nomenclatureName, suggesterDescription, version, timbre);

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


    @PutMapping (value = "/transformDDIToJsonForAPi/{VersionResponsibility}")
    @Operation(summary = "Update an DDI object on Colectica Repository ",
            description = "tranform an DDI object to a json with DDI item inside")
    public String transformFileXml(@RequestBody String  ddiXml, @PathVariable("VersionResponsibility") String idepUtilisateur) throws IOException, TransformerException {

        //String escapedXmlString = ddiXml.replace("\"", "\\\"");
        InputStream xsltStream2 = getClass().getResourceAsStream("/DDIxmltojsonForOneObject.xsl");
        String jsonContent = transformToJson(new ByteArrayResource(ddiXml.getBytes(StandardCharsets.UTF_8)), xsltStream2, idepUtilisateur);
        return jsonContent;

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
                                      String suggesterDescription, String version, String timbre)
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
            transformer.setParameter("version", version);
            transformer.setParameter("timbre", timbre);
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

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Send suggester JSON to Colectica Repository via Colectica API",
            description = "Send a suggester JSON to /api/v1/item. This suggester must be simple, a list of Id,Label transform with transformJsonToJsonForAPi")
    public ResponseEntity<String> uploadItem(@RequestParam("file") MultipartFile file)
            throws IOException, ExceptionColecticaUnreachable {

        String authentToken;
        if (urlColectica.contains("kube")) {
            String token2 = getAuthToken();
            authentToken = extractAccessToken(token2);
        } else {
            authentToken = getFreshToken();
        }
        String fileContent = new String(file.getBytes());

        boolean success = sendFileToApi(fileContent, authentToken);

        if (success) {
            // Vous pouvez effectuer d'autres opérations ici si nécessaire

            return ResponseEntity.ok("Le fichier a été envoyé avec succès à l'API.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur s'est produite lors de l'envoi du fichier à l'API.");
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

    private boolean sendFileToApi(String fileContent, String token) {
        RestTemplate restTemplate = new RestTemplate();

           SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
           factory.setConnectTimeout(100000); // Temps de connexion en millisecondes
           factory.setReadTimeout(100000); // Temps de lecture en millisecondes

           restTemplate.setRequestFactory(factory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(fileContent, headers);

            ResponseEntity<String> response = restTemplate.exchange(itemApiUrl, HttpMethod.POST,request, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        }

    }
