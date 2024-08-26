package fr.insee.rmes.transfoxsl.controller;

import fr.insee.rmes.exceptions.VtlTransformationException;
import fr.insee.rmes.exceptions.XsltTransformationException;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.utils.MultipartFileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/xsl")
@Tag(name = "TransformationController", description = "API pour lancer des transformations XSLT")
public class TransformationController {

    public static final String DDI_2_VTL_XSL = "ddi2vtl.xsl";
    public static final String DEREFERENCE_XSL = "dereference.xsl";
    public static final String FAILED_TO_PROCESS_THE_INPUT_FILE = "Failed to process the input file";
    public static final String TRANSFORMATION_FAILED_DURING_THE_XSLT_PROCESSING = "Transformation failed during the XSLT processing";
    private final XsltTransformationService xsltTransformationService;
    private final MultipartFileUtils multipartFileUtils;

    @Autowired
    public TransformationController(XsltTransformationService xsltTransformationService, MultipartFileUtils multipartFileUtils) {
        this.xsltTransformationService = xsltTransformationService;
        this.multipartFileUtils = multipartFileUtils;
    }

    @Operation(summary = "Déréférencer un objet DDI")
    @PostMapping(value = "/Derefddi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> Derefddi(@RequestParam("file") MultipartFile file)  {
        try {
            // Conversion du MultipartFile en InputStream
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);

            // Première transformation - XML en sortie
            List<String> outputText = xsltTransformationService.transform(inputStream, DEREFERENCE_XSL, false);
            String finalOutput = String.join("\n", outputText);
            // Retourner la liste sous forme de JSON
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


    @Operation(summary = "Générer un fichier texte contenant les règles VTL à partir d'une physicalInstance")
    @PostMapping(value = "/ddi2vtl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InputStreamResource> ddi2vtl(@RequestParam("file") MultipartFile file)  {
        try {
            // Conversion du MultipartFile en InputStream
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);

            // Première transformation - XML en sortie (on récupère la sortie en tant que chaîne)
            List<String> intermediateOutput = xsltTransformationService.transform(inputStream, DEREFERENCE_XSL, false);

            // Conversion de la sortie intermédiaire en InputStream pour la deuxième transformation
            InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));

            // Deuxième transformation - Texte en sortie (on récupère directement une liste de lignes de texte)
            List<String> outputText = xsltTransformationService.transform(intermediateInputStream, DDI_2_VTL_XSL, true);

            // Préparation du contenu à retourner sous forme d'InputStream
            String finalOutput = String.join("\n", outputText);
            ByteArrayInputStream finalInputStream = new ByteArrayInputStream(finalOutput.getBytes(StandardCharsets.UTF_8));

            // Préparation de la réponse avec le fichier texte
            InputStreamResource resource = new InputStreamResource(finalInputStream);
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

    @Operation(summary = "Générer les règles VTL à partir d'une physicalInstance et renvoyer sous forme de JSON")
    @PostMapping(value = "/ddi2vtlJson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> ddi2vtlJson(@RequestParam("file") MultipartFile file)  {
        try {
            // Conversion du MultipartFile en InputStream
            InputStream inputStream = multipartFileUtils.convertToInputStream(file);

            // Première transformation - XML en sortie
            List<String> intermediateOutput = xsltTransformationService.transform(inputStream, DEREFERENCE_XSL, false);

            // Deuxième transformation - Texte en sortie
            InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));
            List<String> outputText = xsltTransformationService.transform(intermediateInputStream, DDI_2_VTL_XSL, true);

            // Retourner la liste sous forme de JSON
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(outputText);
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
}

