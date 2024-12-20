package fr.insee.rmes.transfoxsl.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.VtlTransformationException;
import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.service.internal.DDIDerefencer;
import fr.insee.rmes.transfoxsl.service.internal.DDITransformerToVtl;
import fr.insee.rmes.transfoxsl.utils.MultipartFileUtils;
import fr.insee.rmes.utils.FileExtension;
import fr.insee.rmes.utils.export.XDocReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static fr.insee.rmes.tocolecticaapi.controller.ControllerUtils.xmltoResponseEntity;
import static fr.insee.rmes.transfoxsl.service.internal.DDIDerefencer.DEREFERENCE_XSL;

@Controller
@RequestMapping("/xsl")
@Tag(name = "TransformationController", description = "API pour lancer des transformations XSLT")
@Slf4j
public class TransformationController {

    public static final String FAILED_TO_PROCESS_THE_INPUT_FILE = "Failed to process the input file";
    public static final String TRANSFORMATION_FAILED_DURING_THE_XSLT_PROCESSING = "Transformation failed during the XSLT processing";
    public static final MediaType APPLICATION_ODT = MediaType.valueOf("application/vnd.oasis.opendocument.text");
    public static final String REL_TO_JSON_XSL = "dataRelToJson.xsl";
    private final XsltTransformationService xsltTransformationService;
    private final MultipartFileUtils multipartFileUtils;
    private final XDocReport xDocReport;

    @Autowired
    public TransformationController(XsltTransformationService xsltTransformationService, MultipartFileUtils multipartFileUtils, XDocReport xDocReport) {
        this.xsltTransformationService = xsltTransformationService;
        this.multipartFileUtils = multipartFileUtils;
        this.xDocReport = xDocReport;
    }

    @Operation(summary = "Déréférencer un objet DDI")
    @PostMapping(value = "/Derefddi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> dereferenceDDI(@RequestParam("file") MultipartFile file)  {
        DDIDerefencer ddiDerefencer = new DDIDerefencer(xsltTransformationService);

        try {
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);
            String finalOutput = ddiDerefencer.dereferenceToString(inputStream);
            // Retourner la liste sous forme de JSON
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(finalOutput);
        } catch (IOException e) {
            log.error("Error while processing dereferenceDDI", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (XsltTransformationException e) {
            log.info("Exception with user input", e.getCause());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TRANSFORMATION_FAILED_DURING_THE_XSLT_PROCESSING+" : "+e.getXmlErrorMessage());
        }
    }


    @Operation(summary = "Générer un fichier texte contenant les règles VTL à partir d'une physicalInstance")
    @PostMapping(value = "/ddi2vtl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> ddi2vtl(@RequestParam("file") MultipartFile file)  {

        DDITransformerToVtl ddiTransformerToVtl = new DDITransformerToVtl(xsltTransformationService);

        try {
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);

            // Première transformation - XML en sortie (on récupère la sortie en tant que chaîne)
            List<String> intermediateOutput = xsltTransformationService.transformToXml(inputStream, DEREFERENCE_XSL, false);

            // Conversion de la sortie intermédiaire en InputStream pour la deuxième transformation
            InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));




            // Préparation de la réponse avec le fichier texte
            Resource resource = new ByteArrayResource(ddiTransformerToVtl.transform(inputStream));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vtl.txt");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);

        } catch (IOException e) {
            throw new VtlTransformationException(FAILED_TO_PROCESS_THE_INPUT_FILE + ".", e);
        } catch (XsltTransformationException e) {
            throw new VtlTransformationException(TRANSFORMATION_FAILED_DURING_THE_XSLT_PROCESSING + ".", e);
        } catch (VtlTransformationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Transformer un objet dataRelationShip en JSON")
    @PostMapping(value = "/dataRelationShiptoJson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ddiDataRelationShiptoJson(@RequestParam("file") MultipartFile file)  {
        try {
            // Conversion du MultipartFile en InputStream
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);

            // Première transformation - XML en sortie
            List<String> outputText = xsltTransformationService.transform(inputStream, REL_TO_JSON_XSL, true);
            // Joindre les éléments de la liste en une seule chaîne JSON propre
            String jsonOutput = String.join("", outputText);

            // Utiliser Jackson pour vérifier la validité du JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonOutput); // Désérialisation pour vérifier

            // Sérialiser à nouveau le JSON proprement
            String cleanJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

            // Retourner la liste sous forme de JSON
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cleanJson);
        } catch (IOException e) {
            throw new VtlTransformationException(FAILED_TO_PROCESS_THE_INPUT_FILE + ".", e);
        } catch (XsltTransformationException e) {
            throw new VtlTransformationException(TRANSFORMATION_FAILED_DURING_THE_XSLT_PROCESSING + ".", e);
        } catch (VtlTransformationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Générer les règles VTL à partir d'une physicalInstance et renvoyer sous forme de texte brut")
    @PostMapping(value = "/ddi2vtlBrut", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ddi2vtlBrut(@RequestParam("file") MultipartFile file)  {
        try {
            // Conversion du MultipartFile en InputStream
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);

            // Première transformation - XML en sortie
            List<String> intermediateOutput = xsltTransformationService.transform(inputStream, DEREFERENCE_XSL, false);

            // Deuxième transformation - Texte en sortie
            InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));
            List<String> outputText = xsltTransformationService.transform(intermediateInputStream, DDI_2_VTL_XSL, true);

            // Retourner le texte directement dans la réponse HTTP
            String finalOutput = String.join("\n", outputText);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(finalOutput);
        } catch (IOException e) {
            throw new VtlTransformationException(FAILED_TO_PROCESS_THE_INPUT_FILE + ".", e);
        } catch (XsltTransformationException e) {
            throw new VtlTransformationException(TRANSFORMATION_FAILED_DURING_THE_XSLT_PROCESSING + ".", e);
        } catch (VtlTransformationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/replace-xml-parameters")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(summary = "Modify a fragment DDI", description = "Modify a fragment DDI. All field need to be filled with the same data if there are no changes, except for the version number, which takes a plus 1.")
    public String replaceXmlParameters(@RequestBody String inputXml,
                                       @RequestParam ("Type") DDIItemType type,
                                       @RequestParam ("Label") String label,
                                       @RequestParam ("Version") int version,
                                       @RequestParam ("Name") String name,
                                       @RequestParam ("VersionResponsibility") String idepUtilisateur) {
        return xsltTransformationService.replaceXmlParameters(inputXml, type, label, version, name, idepUtilisateur);
    }

    @GetMapping(value = "/transformJsonToJsonForAPI", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "transform an JSON Codelist (Id,Label) to an another json for Colectica API ",
            description = "tranform a codeList in json to another json with DDI item inside")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> transformFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("nom metier") String idValue,
                                                @RequestParam("label") String nomenclatureName,
                                                @RequestParam("description") String suggesterDescription,
                                                @RequestParam(value = "version", defaultValue = "1") String version,
                                                @RequestParam("idepUtilisateur") String idepUtilisateur,
                                                // peut-être lire le jeton pour recup le timbre directement
                                                @RequestParam("timbre") String timbre) throws RmesException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", UriUtils.encode(UUID.randomUUID() + ".json", StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .headers(headers)
                .body(xsltTransformationService.transformFile(file, idValue, nomenclatureName, suggesterDescription, version, idepUtilisateur, timbre));
    }

    @GetMapping(value = "/transformJsonToJsonForApiForComplexCodeList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "transform an JSON Codelist with multiple field for one id to an another json for Colectica API ",
            description = "tranform a codeList in json to another json with DDI item inside")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> transformFileForComplexCodeList(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam("nom metier") String idValue,
                                                                  @RequestParam("label") String nomenclatureName,
                                                                  @RequestParam("description") String suggesterDescription,
                                                                  @RequestParam(value = "version", defaultValue = "1") String version,
                                                                  @RequestParam("idepUtilisateur") String idepUtilisateur,
                                                                  // peut-être lire le jeton pour recup le timbre directement et l'idep
                                                                  @RequestParam("timbre") String timbre,
                                                                  @RequestParam("principale") String principale,
                                                                  @RequestParam("secondaire") List<String> secondaire,
                                                                  @RequestParam("labelSecondaire") List<String> labelSecondaire) throws RmesException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", UriUtils.encode(UUID.randomUUID() + ".json", StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .headers(headers)
                .body(xsltTransformationService.transformFileForComplexList(file, idValue, nomenclatureName, suggesterDescription, version, idepUtilisateur, timbre, principale, secondaire, labelSecondaire);
    }


    @GetMapping(value = "/transform/ddi-to-codebook",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"}
    )
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(operationId = "getCodeBook", summary = "Produce a codebook from a DDI")

    public ResponseEntity<Resource> ddiToCodeBook(

            @Parameter(schema = @Schema(type = "string", format = "String", description = "Accept"))
            @RequestHeader(required = false) String accept,

            @Parameter(schema = @Schema(type = "string", format = "binary", description = "file in DDI"))
            @RequestParam(value = "ddiFile") MultipartFile ddiFile, // InputStream isDDI,

            @Parameter(schema = @Schema(type = "string", format = "binary", description = "file for structure"))
            @RequestParam(value = "codeBookFile") MultipartFile codeBookFile //InputStream isCodeBook

    )
            throws RmesException {
        log.info("Generate CodeBook from DDI {}, {}", ddiFile.getOriginalFilename(), codeBookFile.getOriginalFilename());
        try {
            String ddi = new String(ddiFile.getBytes(), StandardCharsets.UTF_8);
            Resource response = new ByteArrayResource(xDocReport.exportVariableBookInOdt(ddi, codeBookFile.getBytes()));
            log.debug("Codebook is generated");
            return xmltoResponseEntity(response, "Codebook" + FileExtension.forHeader(accept).extension(), APPLICATION_ODT);
        } catch (IOException e) {
            throw new RmesException(HttpStatus.BAD_REQUEST, e.getMessage(), "Files can't be read");
        }
    }


    @GetMapping(value = "/transform/ddi-to-codebook/V2",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"}
    )
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(operationId = "getCodeBookV2", summary = "Produce a codebook from a DDI")
    public ResponseEntity<?> getCodeBookV2(
            @Parameter(schema = @Schema(type = "string", allowableValues = {"concis", "concis avec expression", "scindable", "non scindable"}))
            @RequestParam(value = "dicoVar") String isCodeBook, //InputStream isCodeBook,
            @RequestParam(value = "ddiFile") MultipartFile ddiFile // InputStream isDDI,
    ) throws Exception {

        String ddi = new String(ddiFile.getBytes(), StandardCharsets.UTF_8);
        String xslPatternFile = null;
        switch (isCodeBook) {
            case "concis":
                String xmlFileConcis = "/xslTransformerFiles/dicoCodes/dicoConcisPatternContent.xml";
                xslPatternFile = xmlFileConcis;
                break;
            case "concis avec expression":
                String xmlFileConcisAvecExpression = "/xslTransformerFiles/dicoCodes/dicoConcisDescrPatternContent.xml";
                xslPatternFile = xmlFileConcisAvecExpression;
                break;
            case "scindable":
                String xmlFileScindable = "/xslTransformerFiles/dicoCodes/dicoScindablePatternContent.xml";
                xslPatternFile = xmlFileScindable;
                break;
            case "non scindable":
                String xmlFileNonScindable = "/xslTransformerFiles/dicoCodes/dicoNonScindablePatternContent.xml";
                xslPatternFile = xmlFileNonScindable;
                break;
            default:
                log.error("Choix incorrect");
                break;
        }

        return xDocReport.getCodeBookExportV2(ddi, xslPatternFile);
    }

    @GetMapping(value = "/check/is-codebook",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"}
    )
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(operationId = "getCodeBookCheck", summary = "Check the DDI before made the codebook export")
    public ResponseEntity<?> getCodeBookCheck(
            @RequestParam(value = "ddiFile") MultipartFile ddiFile)throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + codeBookCheck.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return xDocReport.getCodeBookCheck(ddiFile.getBytes());
        ResponseEntity.ok()
                .headers(headers)
                .contentLength(codeBookCheck.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resourceByte);
    }

}

