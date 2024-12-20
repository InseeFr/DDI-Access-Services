package fr.insee.rmes.tocolecticaapi.controller;


import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.utils.FileExtension;
import fr.insee.rmes.utils.FilesUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static fr.insee.rmes.tocolecticaapi.controller.ControllerUtils.xmltoResponseEntity;

@Controller
@RequestMapping("/postItem")
@Tag(name = "DEMO-Colectica", description = "Services for upgrade Colectica-API")
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

    static final Logger logger = LogManager.getLogger(PostItem.class);
    public static final MediaType APPLICATION_ODT = MediaType.valueOf("application/vnd.oasis.opendocument.text");
    private final ColecticaService colecticaService;

    @Autowired
    public PostItem(ColecticaService colecticaService) {
        this.colecticaService = colecticaService;
    }


    @PostMapping("/UpdateToColecticaRepository/{transactionType}")
    @Operation(summary = "Send an update to Colectica Repository via Colectica API ",
            description = "Send a json make with /replace-xml-parameters/{Type}/{Label}/{Version}/{Name}/{VersionResponsibility} COPYCOMMIT is for Upload a new object not for update")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> sendUpdateColectica(
            @RequestBody String ddiUpdatingInJson,
            @RequestParam("transactionType") TransactionType transactionType) throws RmesException {
        colecticaService.sendUpdateColectica(ddiUpdatingInJson, transactionType);
        return ResponseEntity.ok("Transaction success");
    }


    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Send suggester JSON to Colectica Repository via Colectica API",
            description = "Send a suggester JSON to /api/v1/item. This suggester must be simple, a list of Id,Label transform with transformJsonToJsonForAPi")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> uploadItem(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal
    OidcUser principal)
            throws IOException, ExceptionColecticaUnreachable {
        String fileContent = new String(file.getBytes());
        return colecticaService.sendUpdateColectica(fileContent, TransactionType.COPYCOMMIT);
    }


    @Hidden
    @PostMapping("{type}/json")
    @Operation(summary = "Get JSON for a type of DDI item", description = "Get a JSON list of item for a type of DDI items .")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    public ResponseEntity<String> byType(
            @PathVariable("type") DDIItemType type, @AuthenticationPrincipal
            OidcUser principal)
            throws IOException, ExceptionColecticaUnreachable, ParseException {

        return colecticaService.getByType(type);
    }


    @PostMapping(value = "/operation/codebook",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"}
    )
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(operationId = "getCodeBook", summary = "Produce a codebook from a DDI")

    public ResponseEntity<Resource> getCodeBook(

            @Parameter(schema = @Schema(type = "string", format = "String", description = "Accept"))
            @RequestHeader(required = false) String accept,

            @Parameter(schema = @Schema(type = "string", format = "binary", description = "file in DDI"))
            @RequestParam(value = "file") MultipartFile isDDI, // InputStream isDDI,

            @Parameter(schema = @Schema(type = "string", format = "binary", description = "file for structure"))
            @RequestParam(value = "dicoVar") MultipartFile isCodeBook //InputStream isCodeBook

    )
            throws RmesException {
        logger.info("Generate CodeBook from DDI {}, {}", isDDI.getOriginalFilename(), isCodeBook.getOriginalFilename());
        String ddi;
        File codeBookFile;
        try {
            ddi = new String(isDDI.getBytes(), StandardCharsets.UTF_8);
            codeBookFile = FilesUtils.streamToFile(isCodeBook.getInputStream(), "dicoVar", ".odt");
            Resource response = colecticaService.exportCodeBookAsOdt(ddi, codeBookFile);
            logger.debug("Codebook is generated");
            return xmltoResponseEntity(response, "Codebook" + FileExtension.forHeader(accept).extension(), APPLICATION_ODT);
        } catch (IOException e) {
            throw new RmesException(HttpStatus.BAD_REQUEST, e.getMessage(), "Files can't be read");
        }
    }

    @PostMapping(value = "/operation/codebook/V2",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"}
    )
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(operationId = "getCodeBookV2", summary = "Produce a codebook from a DDI")
    public ResponseEntity<?> getCodeBookV2(

            @Parameter(schema = @Schema(type = "string", format = "String", description = "Accept"))
            @RequestHeader(required = false) String accept,

            @Parameter(schema = @Schema(type = "string", allowableValues = {"concis", "concis avec expression", "scindable", "non scindable"}))
            @RequestParam(value = "dicoVar") String isCodeBook, //InputStream isCodeBook,

            @RequestParam(value = "file") MultipartFile isDDI // InputStream isDDI,

    )
            throws Exception {
        if (isDDI == null)
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't generate codebook", "Stream is null");
        InputStream ddiInputStream = new BufferedInputStream(isDDI.getInputStream());
        String ddi = new String(ddiInputStream.readAllBytes(), StandardCharsets.UTF_8);
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
                logger.error("Choix incorrect");
                break;
        }

        return colecticaService.getCodeBookExportV2(ddi, xslPatternFile);
    }

    @PostMapping(value = "/operation/codebook/checkCodeBookContent",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, "application/vnd.oasis.opendocument.text"}
    )
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(operationId = "getCodeBookCheck", summary = "Check the DDI before made the codebook export")

    public ResponseEntity<?> getCodeBookCheck(

            @RequestParam(value = "file") MultipartFile isCodeBook // InputStream isDDI,

    )
            throws Exception {

        return colecticaService.getCodeBookCheck(isCodeBook);
    }

    protected ResponseEntity<Object> returnRmesException(RmesException e) {
        logger.error(e.getMessageAndDetails(), e);
        return ResponseEntity.status(e.getStatus()).contentType(MediaType.TEXT_PLAIN).body(e.getMessage());
    }


}
