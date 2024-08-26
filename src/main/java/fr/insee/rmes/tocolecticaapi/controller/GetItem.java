package fr.insee.rmes.tocolecticaapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/Item")
@Tag(name= "DEMO-Colectica",description = "Services for upgrade Colectica-API")
public class GetItem {
    static final Logger logger = LogManager.getLogger(GetItem.class);
    @Autowired
    private final ColecticaService colecticaService;

    public GetItem(ColecticaService colecticaService) {
        this.colecticaService = colecticaService;
    }

    @GetMapping("fragmentInstance/uuid")
    @Operation(summary = "Get FragmentInstance by uuid", description = "Get an XML document for a Fragment:Instance from Colectica repository.")
    @Produces(MediaType.APPLICATION_XML)
    public ResponseEntity<String> findInstanceByUuidColectica(
            @Parameter(
                    description = "id de l'objet colectica sous la forme uuid/version",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")) String uuid) throws RmesExceptionIO, ParseException {
        return colecticaService.findInstanceByUuid(uuid);

    }

    @GetMapping("ddiFragment/uuid")
    @Operation(summary = "Get Fragment by uuid", description = "Get an XML document for a ddi:Fragment from Colectica repository.")
    @Produces(MediaType.APPLICATION_XML)
    public ResponseEntity<String> findFragmentByUuidColectica(
            @Parameter(
                    description = "id de l'objet colectica sous la forme uuid/version",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")) String uuid) throws ExceptionColecticaUnreachable, IOException {
        return colecticaService.findFragmentByUuid(uuid);

    }

    @GetMapping("FragmentInstance/uuid/withChildren")
    @Operation(summary = "Get Fragments by uuid", description = "Get an XML document for a Fragment:Instance from Colectica repository.")
    @Produces(MediaType.APPLICATION_XML)
    public String findFragmentByUuidWithChildrenColectica(
            @Parameter(
                    description = "id de l'objet colectica sous la forme uuid/version",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93/2")) String uuid) throws Exception {
        return colecticaService.findFragmentByUuidWithChildren(uuid);

    }

        @GetMapping("/filtered-search/texte")
        @Operation(summary = "Get list of match in elasticsearch database", description = "Get a JSON ")
        public ResponseEntity<String> filteredSearchText(
                @Parameter(
                        description = "nom par défaut de l'index colectica",
                        required = true,
                        schema = @Schema(
                                type = "string", example="portal*"))
             String index ,
                @Parameter(
                        description = "texte à rechercher. le * sert de wildcard",
                        required = true,
                        schema = @Schema(
                                type = "string", example="sugg*")) String texte) {
            return colecticaService.filteredSearchText(index, texte);
    }

    @GetMapping("/filtered-search/texteByType")
    @Operation(summary = "Get list of match in elasticsearch database", description = "Get a JSON ")
    public ResponseEntity<String> filteredSearchTextByType(
            @Parameter(
                    description = "nom par défaut de l'index colectica",
                    required = true,
                    schema = @Schema(
                            type = "string", example="portal*"))
            @RequestParam String index,
            @Parameter(
                    description = "texte à rechercher. le * sert de wildcard",
                    required = true,
                    schema = @Schema(
                            type = "string", example="sugg*"))
            @RequestParam String texte,
            @Parameter(
                    description = "type à selectionner",
                    required = true)
            @RequestParam DDIItemType ddiItemType
    ) throws UnsupportedEncodingException {

        // Validation des entrées
        if (!index.matches("^[a-zA-Z0-9_*]+$") || !texte.matches("^[a-zA-Z0-9_*]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
        }

        // Encodage des entrées
        String encodedIndex = URLEncoder.encode(index, StandardCharsets.UTF_8.toString());
        String encodedText = URLEncoder.encode(texte, StandardCharsets.UTF_8.toString());

        // Appel au service avec les valeurs encodées
        return colecticaService.searchTexteByType(encodedIndex, encodedText, ddiItemType);
    }


    @GetMapping("/filtered-search/type/")
    @Operation(summary = "Get list of match by type in elasticsearch database", description = "Get a JSON ")
    public ResponseEntity<String> searchByType(
            @Parameter(
                    description = "nom par défaut de l'index colectica",
                    required = true,
                    schema = @Schema(
                            type = "string", example="portal_registered_item"))
            String index ,
            @Parameter(
                    description = "type à selectionner",
                    required = true)  @RequestParam DDIItemType ddiItemType) {
        return colecticaService.searchByType(index, ddiItemType);
    }



    @GetMapping("suggesters/jsonWithChild")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get JSON for Suggester/codelist simple (id,label)", description = "Get a JSON document for suggester or codelist from Colectica repository including an item with childs.")
       public Object getJsonWithChild(
            @Parameter(
            description = "id de l'objet colectica",
            required = true,
            schema = @Schema(
                    type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93"))  String identifier,
            @RequestParam(value = "fieldIdName",defaultValue = "id") String outputField,
            @RequestParam(value="fieldLabelName",defaultValue = "label") String fieldLabelName) throws Exception {
        return colecticaService.getJsonWithChild(identifier, outputField, fieldLabelName);
    }

    @GetMapping("/RessourcePackageToJson")
    public String convertXmlToJson(
            @RequestParam(name = "uuid", required = true) String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RmesExceptionIO, ParseException {
        return colecticaService.convertXmlToJson(uuid);
    }

    @PutMapping ("/replace-xml-parameters")
    @PreAuthorize("hasRole('Administrateur_RMESGOPS')")
    @Operation(summary = "Modify a fragment DDI", description = "Modify a fragment DDI. All field need to be filled with the same data if there are no changes, except for the version number, which takes a plus 1.")
    public String replaceXmlParameters(@RequestBody String inputXml,
                                       @RequestParam ("Type") DDIItemType type,
                                       @RequestParam ("Label") String label,
                                       @RequestParam ("Version") int version,
                                       @RequestParam ("Name") String name,
                                       @RequestParam ("VersionResponsibility") String idepUtilisateur) {
        return colecticaService.replaceXmlParameters(inputXml, type, label, version, name, idepUtilisateur);
    }


}
