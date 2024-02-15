package fr.insee.rmes.ToColecticaApi.controller;


import fr.insee.rmes.ToColecticaApi.models.TransactionType;
import fr.insee.rmes.ToColecticaApi.service.ColecticaService;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/postItem")
@Tag(name= "DEMO-Colectica",description = "Services for upgrade Colectica-API")
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

    static final Logger log = LogManager.getLogger(PostItem.class);
    private final ResourceLoader resourceLoader;

    private final ColecticaService colecticaService;
    @Autowired
    public PostItem(ResourceLoader resourceLoader, ColecticaService colecticaService) {
        this.colecticaService = colecticaService;
        this.resourceLoader = resourceLoader;
    }


    @PostMapping(value = "/transformJsonToJsonForAPi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "transform an JSON Codelist (Id,Label) to an another json for Colectica API ",
            description = "tranform a codeList in json to another json with DDI item inside")
    public ResponseEntity<?> transformFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("nom metier") String idValue,
                                           @RequestParam("label") String nomenclatureName,
                                           @RequestParam("description") String suggesterDescription,
                                           @RequestParam(value = "version",defaultValue = "1") String version,
                                           @RequestParam("idepUtilisateur") String idepUtilisateur,
                                           // peut-être lire le jeton pour recup le timbre directement
                                           @RequestParam("timbre") String timbre) {

            return colecticaService.transformFile(file, idValue, nomenclatureName, suggesterDescription, version, idepUtilisateur, timbre);
        }

    @PostMapping(value = "/transformJsonToJsonForApiForComplexCodeList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "transform an JSON Codelist (Id,Label) to an another json for Colectica API ",
            description = "tranform a codeList in json to another json with DDI item inside")
    public ResponseEntity<?> transformFileForComplexCodeList(@RequestParam("file") MultipartFile file,
                                           @RequestParam("nom metier") String idValue,
                                           @RequestParam("label") String nomenclatureName,
                                           @RequestParam("description") String suggesterDescription,
                                           @RequestParam(value = "version",defaultValue = "1") String version,
                                           @RequestParam("idepUtilisateur") String idepUtilisateur,
                                           // peut-être lire le jeton pour recup le timbre directement et l'idep
                                           @RequestParam("timbre") String timbre,
                                           @RequestParam("principale") String principale,
                                           @RequestParam("secondaire") List <String> secondaire,
                                           @RequestParam("labelSecondaire") List <String> labelSecondaire)  {

        return colecticaService.transformFileForComplexList(file, idValue, nomenclatureName, suggesterDescription, version, idepUtilisateur, timbre,principale,secondaire,labelSecondaire);
    }


    @PostMapping("/UpdateToColecticaRepository/{transactionType}")
    @Operation(summary = "Send an update to Colectica Repository via Colectica API ",
            description = "Send a json make with /replace-xml-parameters/{Type}/{Label}/{Version}/{Name}/{VersionResponsibility} COPYCOMMIT is for Upload a new object not for update")
    public ResponseEntity<String> sendUpdateColectica(
            @RequestBody String DdiUpdatingInJson,
            @RequestParam("transactionType") TransactionType transactionType
    ) throws IOException {
        return colecticaService.sendUpdateColectica(DdiUpdatingInJson, transactionType);

    }



    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Send suggester JSON to Colectica Repository via Colectica API",
            description = "Send a suggester JSON to /api/v1/item. This suggester must be simple, a list of Id,Label transform with transformJsonToJsonForAPi")
    public ResponseEntity<String> uploadItem(@RequestParam("file") MultipartFile file)
            throws IOException, ExceptionColecticaUnreachable {
        String fileContent = new String(file.getBytes());
        return colecticaService.sendUpdateColectica(fileContent, TransactionType.COPYCOMMIT);
         }


    @Hidden
    @PostMapping("{type}/json")
    @Operation(summary = "Get JSON for a type of DDI item", description = "Get a JSON list of item for a type of DDI items .")
    public ResponseEntity<?> byType(
            @PathVariable("type") DDIItemType type)
            throws IOException, ExceptionColecticaUnreachable {

        return colecticaService.getByType(type);
    }

    }
