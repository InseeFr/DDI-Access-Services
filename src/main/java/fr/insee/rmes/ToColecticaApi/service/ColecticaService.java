package fr.insee.rmes.tocolecticaapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import fr.insee.rmes.webservice.rest.RMeSException;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ColecticaService {
    ResponseEntity<String> findFragmentByUuid(String uuid) throws ExceptionColecticaUnreachable, IOException;

    String sendDeleteColectica(String uuid, TransactionType transactionType) throws JsonProcessingException, ExceptionColecticaUnreachable, RMeSException, ParseException;
    ResponseEntity<String> findInstanceByUuid(String uuid) throws RMeSException, ParseException;
    String findFragmentByUuidWithChildren(String uuid) throws Exception;
    ResponseEntity<String> filteredSearchText(String index, String texte);


    ResponseEntity<String> searchTexteByType(String index, String texte, DDIItemType type);
    ResponseEntity<String> searchByType(String index, DDIItemType type);
    ResponseEntity<List<Map<String,String>>> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws Exception;

    String convertXmlToJson(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RMeSException, ParseException;
    String replaceXmlParameters(String inputXml, DDIItemType type, String label, int version, String name, String idepUtilisateur);

    ResponseEntity<String> getByType(DDIItemType type) throws IOException, ExceptionColecticaUnreachable, ParseException;

    ResponseEntity<String> sendUpdateColectica(String ddiUpdatingInJson, TransactionType transactionType) throws IOException;

    ResponseEntity<String> transformFile(
            MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre
    );
    ResponseEntity<String> transformFileForComplexList(
            MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre,
            String principale,
            List <String> secondaire,
            List <String> labelSecondaire
    );

    ResponseEntity<Resource> getCodeBookExport(String ddiFile, File dicoVar, String acceptHeader) throws RmesException;

    ResponseEntity<Resource> getCodeBookExportV2(String ddiFile, String xslPatternFile) throws RmesException, Exception;

    ResponseEntity<?> getCodeBookCheck(MultipartFile isCodeBook) throws RmesException, Exception;

    ResponseEntity<String> uploadItem(MultipartFile file) throws IOException, ExceptionColecticaUnreachable;
}
