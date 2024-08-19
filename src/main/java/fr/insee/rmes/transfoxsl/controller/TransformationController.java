package fr.insee.rmes.transfoxsl.controller;

import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.utils.MultipartFileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/xsl")
@Tag(name = "TransformationController", description = "API pour lancer des transformations XSLT")
public class TransformationController {

    private final XsltTransformationService xsltTransformationService;
    private final MultipartFileUtils multipartFileUtils;

    @Autowired
    public TransformationController(XsltTransformationService xsltTransformationService, MultipartFileUtils multipartFileUtils) {
        this.xsltTransformationService = xsltTransformationService;
        this.multipartFileUtils = multipartFileUtils;
    }

    @Operation(summary = "Générer un fichier texte contenant les règles VTL à partir d'une physicalInstance")
    @PostMapping(value = "/ddi2vtl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InputStreamResource> ddi2vtl(@RequestParam("file") MultipartFile file) throws Exception {
        // Conversion du MultipartFile en InputStream
        InputStream inputStream = MultipartFileUtils.convertToInputStream(file);

        // Première transformation - XML en sortie (on récupère la sortie en tant que chaîne)
        List<String> intermediateOutput = xsltTransformationService.transform(inputStream, "dereference.xsl", false);

        // Conversion de la sortie intermédiaire en InputStream pour la deuxième transformation
        InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));

        // Deuxième transformation - Texte en sortie (on récupère directement une liste de lignes de texte)
        List<String> outputText = xsltTransformationService.transform(intermediateInputStream, "ddi2vtl.xsl", true);

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
    }

    @Operation(summary = "Générer les règles VTL à partir d'une physicalInstance et renvoyer sous forme de JSON")
    @PostMapping(value = "/ddi2vtlJson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> ddi2vtlJson(@RequestParam("file") MultipartFile file) throws Exception {
        // Conversion du MultipartFile en InputStream
        InputStream inputStream = MultipartFileUtils.convertToInputStream(file);

        // Première transformation - XML en sortie
        List<String> intermediateOutput = xsltTransformationService.transform(inputStream, "dereference.xsl", false);

        // Deuxième transformation - Texte en sortie
        InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));
        List<String> outputText = xsltTransformationService.transform(intermediateInputStream, "ddi2vtl.xsl", true);

        // Retourner la liste sous forme de JSON
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(outputText);
    }

    @Operation(summary = "Générer les règles VTL à partir d'une physicalInstance et renvoyer sous forme de texte brut")
    @PostMapping(value = "/ddi2vtlBrut", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ddi2vtlBrut(@RequestParam("file") MultipartFile file) throws Exception {
        // Conversion du MultipartFile en InputStream
        InputStream inputStream = MultipartFileUtils.convertToInputStream(file);

        // Première transformation - XML en sortie
        List<String> intermediateOutput = xsltTransformationService.transform(inputStream, "dereference.xsl", false);

        // Deuxième transformation - Texte en sortie
        InputStream intermediateInputStream = new ByteArrayInputStream(String.join("\n", intermediateOutput).getBytes(StandardCharsets.UTF_8));
        List<String> outputText = xsltTransformationService.transform(intermediateInputStream, "ddi2vtl.xsl", true);

        // Retourner le texte directement dans la réponse HTTP
        String finalOutput = String.join("\n", outputText);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(finalOutput);
    }


}

