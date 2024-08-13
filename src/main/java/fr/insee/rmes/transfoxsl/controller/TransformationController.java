package fr.insee.rmes.transfoxsl.controller;

import fr.insee.rmes.transfoxsl.service.FileService;
import fr.insee.rmes.transfoxsl.service.XsltTransformationService;
import fr.insee.rmes.transfoxsl.utils.TemporaryFileCleanupFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/xsl")
@Tag(name = "TransformationController", description = "API pour lancer des transformations XSLT")
public class TransformationController {

    private final XsltTransformationService xsltTransformationService;
    private final FileService fileService;

    @Autowired
    public TransformationController(XsltTransformationService xsltTransformationService, FileService fileService) {
        this.xsltTransformationService = xsltTransformationService;
        this.fileService = fileService;
    }

/*    @Operation(summary = "Lancer la transformation de déréréférencement")
    @PostMapping(value="/dereference", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InputStreamResource> dereference(@RequestParam("file") MultipartFile file) throws Exception {
        File outputFile = xsltTransformationService.transform(file, "dereference.xsl");
        TemporaryFileCleanupFilter.addTempFile(outputFile);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(outputFile));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dereferenced.xml");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputFile.length())
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }*/

    @Operation(summary = "Générer un fichier texte contenant les règles VTL à partir d'une physicalInstance")
    @PostMapping(value = "/ddi2vtl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InputStreamResource> ddi2vtl(@RequestParam("file") MultipartFile file) throws Exception {
        // Conversion du MultipartFile en File
        File tempInputFile = FileService.convertToFile(file);
        TemporaryFileCleanupFilter.addTempFile(tempInputFile);

        // Première transformation - XML en sortie
        File intermediateFile = xsltTransformationService.transform(tempInputFile, "dereference.xsl", false);
        TemporaryFileCleanupFilter.addTempFile(intermediateFile);

        // Deuxième transformation - Texte en sortie
        File outputFile = xsltTransformationService.transform(intermediateFile, "ddi2vtl.xsl", true);
        TemporaryFileCleanupFilter.addTempFile(outputFile);

        // Préparation de la réponse avec le fichier texte
        InputStreamResource resource = new InputStreamResource(new FileInputStream(outputFile));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vtl.txt");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputFile.length())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }
}

