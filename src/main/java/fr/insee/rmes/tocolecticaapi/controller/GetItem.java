package fr.insee.rmes.tocolecticaapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.fragments.DdiFragmentService;
import fr.insee.rmes.tocolecticaapi.service.ColecticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/Item")
@Tag(name= "DEMO-Colectica",description = "Services for upgrade Colectica-API")
@Slf4j
@RequiredArgsConstructor
public class GetItem {

    private final ColecticaService colecticaService;
    private final DdiFragmentService ddiFragmentService;

    @GetMapping(value = "fragmentInstance/uuid", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Get FragmentInstance by uuid", description = "Get an XML document for a Fragment:Instance from Colectica repository.")
    public ResponseEntity<String> findInstanceByUuidColectica(
            @Parameter(
                    description = "id de l'objet colectica sous la forme uuid/version",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")) String uuid) throws RmesException {
        return ResponseEntity.ok(colecticaService.searchColecticaInstanceByUuid(uuid));
    }

    @GetMapping(value = "ddiFragment/{uuid}/{version}/dataRelationship", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fournir une représentation JSON de l'objet dataRelationShip du DDI Fragment dont l'uuid est en paramètre")
    public ResponseEntity<String> extractDataRelationshipFromFragment(@Parameter(
            description = "id du fragment DDI sous la forme uuid",
            required = true,
            schema = @Schema(type = "string", example="16a35b68-4479-4282-95ed-ff7d151746e4"))
                                                                      @PathVariable String uuid, @PathVariable int version) throws RmesException {

        return ResponseEntity.ok(this.ddiFragmentService.extractDataRelationship(uuid+"/"+version));
    }


    @GetMapping(value = "ddiFragment/{uuid}/dataRelationship", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fournir une représentation JSON de l'objet dataRelationShip du DDI Fragment dont l'uuid est en paramètre")
    public ResponseEntity<String> extractDataRelationshipFromFragment(@Parameter(
            description = "id du fragment DDI sous la forme uuid",
            required = true,
            schema = @Schema(type = "string", example="16a35b68-4479-4282-95ed-ff7d151746e4"))
                                                                      @PathVariable String uuid) throws RmesException {
        return ResponseEntity.ok(this.ddiFragmentService.extractDataRelationship(uuid));
    }


    @GetMapping(value = "ddiFragment/uuid", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Get Fragment by uuid", description = "Get an XML document for a ddi:Fragment from Colectica repository.")
    public ResponseEntity<String> findFragmentByUuidColectica(
            @Parameter(
                    description = "id de l'objet colectica sous la forme uuid/version",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93")) String uuid) {
        return ResponseEntity.ok(colecticaService.findFragmentByUuid(uuid));

    }



    @GetMapping(value = "FragmentInstance/uuid/withChildren", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Fragments by uuid", description = "Get an XML document for a Fragment:Instance from Colectica repository.")
    public ResponseEntity<String> findFragmentByUuidWithChildrenColectica(
            @Parameter(
                    description = "id de l'objet colectica sous la forme uuid/version",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93/2")) String uuid) throws RmesException {
        return ResponseEntity.ok(colecticaService.findFragmentByUuidWithChildren(uuid).toString());

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
                            type = "string", example="sugg*")) String texte) throws RmesException {
        return ResponseEntity.ok(colecticaService.filteredSearchText(index, texte));
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
    ) throws RmesException {

        // Validation des entrées
        if (!index.matches("^[a-zA-Z0-9_*]+$") || !texte.matches("^[a-zA-Z0-9_*]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
        }

        // Encodage des entrées
        String encodedIndex = URLEncoder.encode(index, StandardCharsets.UTF_8);
        String encodedText = URLEncoder.encode(texte, StandardCharsets.UTF_8);

        // Appel au service avec les valeurs encodées
        return ResponseEntity.ok(colecticaService.searchTexteByType(encodedIndex, encodedText, ddiItemType));
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
                    required = true)  @RequestParam DDIItemType ddiItemType) throws RmesException {
        return ResponseEntity.ok(colecticaService.searchByType(index, ddiItemType));
    }



    @GetMapping(value = "suggesters/jsonWithChild", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get JSON for Suggester/codelist simple (id,label)", description = "Get a JSON document for suggester or codelist from Colectica repository including an item with childs.")
    public Object getJsonWithChild(
            @Parameter(
                    description = "id de l'objet colectica",
                    required = true,
                    schema = @Schema(
                            type = "string", example="d6c08ec1-c4d2-4b9a-b358-b23aa4e0af93"))  String identifier,
            @RequestParam(value = "fieldIdName",defaultValue = "id") String outputField,
            @RequestParam(value="fieldLabelName",defaultValue = "label") String fieldLabelName) throws JsonProcessingException {
        return colecticaService.getJsonWithChild(identifier, outputField, fieldLabelName);
    }

    @GetMapping(value = "/RessourcePackageToJson", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRessourcePackage(
            @RequestParam(name = "uuid", required = true) String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException {
        return colecticaService.getRessourcePackage(uuid);
    }


}
