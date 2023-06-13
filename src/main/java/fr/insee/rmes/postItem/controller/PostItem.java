package fr.insee.rmes.postItem.controller;


import fr.insee.rmes.postItem.models.CustomMultipartFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.postItem.models.AuthRequest;
import fr.insee.rmes.postItem.randomUUID;
import fr.insee.rmes.search.controller.DDISearch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Controller
@RequestMapping("/postItem")
@Tag(name = "Post to colectica")
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

    final static Logger log = LogManager.getLogger(DDISearch.class);
    private final ResourceLoader resourceLoader;

    @Value("${auth.api.url}")
    private String authApiUrl;

    @Value("${item.api.url}")
    private String itemApiUrl;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;


    @Autowired
    public PostItem(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostMapping(value = "/transformJsonToJsonForAPi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "transform an JSON Codelist to an another json for Colectica API ", description = "tranform a codeList in json to another json with DDI item inside")
    public ResponseEntity<?> transformFile(@RequestParam("file") MultipartFile file) {

        try {
            // Générer un nom de fichier unique pour le fichier résultant
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            //String resultFileName = UUID.randomUUID().toString() + ".xml";
            String resultFileName2 = UUID.randomUUID().toString() + ".json";

            //Modifier le fichier json en entrée en ajoutant <data> et </data>
            MultipartFile outputFile = null;
            try {
                outputFile = processFile(file);
                System.out.println("Le fichier a été traité avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
            }


            // Charger le fichier XSLT
            // TODO: 05/06/2023 vérifier que le classpath est correct en qf
            Resource xsltResource = resourceLoader.getResource("classpath:jsontoDDIXML.xsl");
            File xsltFile = xsltResource.getFile();

            // Transformer le fichier JSON en XML à l'aide de XSLT
            String xmlContent = transformToXml(outputFile, xsltFile);

            Path resultFilePath = Files.createTempFile("result_", ".xml");
            Files.writeString(resultFilePath, xmlContent);

            // Charger le fichier xml résultant
            Resource resultResource = resourceLoader.getResource("file:" + resultFilePath.toAbsolutePath().toString());

            // Charger le fichier XSLT
            // TODO: 05/06/2023 vérifier que le classpath est correct en qf
            Resource xsltResource2 = resourceLoader.getResource("classpath:DDIxmltojson.xsl");
            File xsltFile2 = xsltResource2.getFile();
            String JsonContent = transformToJson(new InputStreamResource(resultResource.getInputStream()), xsltFile2);

            // Enregistrer le contenu XML dans un fichier
            Path resultFilePath2 = Files.createTempFile("result_", ".json");
            Files.writeString(resultFilePath2, JsonContent);

            // Charger le fichier json de resultat si on veut l'exporter/le modifier au niveau de quelques clés/valeurs
            Resource resultResource2 = resourceLoader.getResource("file:" + resultFilePath2.toAbsolutePath().toString());

            //reprise du jsonContent pour mapping et mise à jour de certaines variables
            // ObjectMapper mapper= new ObjectMapper();
            // Items jsonAmodif = mapper.readValue(JsonContent,Items.class);

            // Supprimer le fichier XML temporaire
            // TODO: 05/06/2023 regarder pourquoi ça fonctionne pas bien, je pense qu'on l'appelle dans la réponse donc si on l'efface on a un null pointer
            //Files.deleteIfExists(resultFilePath);
            ////Files.deleteIfExists(resultFilePath2);
            // Retourner le fichier json résultant
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + UriUtils.encode(resultFileName2, StandardCharsets.UTF_8) + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(resultResource2.getInputStream()));
        } catch (IOException | TransformerException e) {
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


        private String transformToXml(MultipartFile file, File xsltFile) throws IOException, TransformerException {

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

            // on lance la transfo
            StreamSource text = new StreamSource(file.getInputStream());
            StringWriter xmlWriter = new StringWriter();
            StreamResult xmlResult = new StreamResult(xmlWriter);
            transformer.transform(text, xmlResult);
            String xmlContent = xmlWriter.toString();
            return xmlContent;
        }

        private String transformToJson(Resource resultResource, File xsltFileJson) throws IOException, TransformerException {

            // Créer un transformateur XSLT
            TransformerFactory factory = TransformerFactory.newInstance();

            Source xslt = new StreamSource(xsltFileJson);
            Transformer transformer = factory.newTransformer(xslt);

            // on lance la transfo
            StreamSource text = new StreamSource(resultResource.getInputStream());
            StringWriter xmlWriter = new StringWriter();
            StreamResult xmlResult = new StreamResult(xmlWriter);
            transformer.transform(text, xmlResult);
            String jsonContent = xmlWriter.toString();
            return jsonContent;
        }

        @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
        @Operation(summary = "Send json to Colectica Repository via colectica API", description = "send a json to /api/item/v1")
        public ResponseEntity<String> uploadItem(@RequestParam("file") MultipartFile file) throws IOException {
            // Obtenir le token d'authentification
            String token = getAuthToken();
            String accessToken = extractAccessToken(token);
            // Convertir le contenu du fichier en chaîne JSON
            String fileContent = new String(file.getBytes());

            // Envoi du fichier à l'API avec le token d'authentification
            boolean success = sendFileToApi(fileContent, accessToken);

            if (success) {
                return ResponseEntity.ok("Le fichier a été envoyé avec succès à l'API.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Une erreur s'est produite lors de l'envoi du fichier à l'API.");
            }
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(fileContent, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(itemApiUrl, request, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        }

    }
