package fr.insee.rmes.tocolecticaapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.rmes.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.model.DDIItemType;
import fr.insee.rmes.tocolecticaapi.models.TransactionType;
import org.json.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ColecticaService {
    String findFragmentByUuid(String uuid);

    void sendDeleteColectica(String uuid, TransactionType transactionType) throws JsonProcessingException, ExceptionColecticaUnreachable, RmesExceptionIO, ParseException, RmesException;
    String searchColecticaInstanceByUuid(String uuid) throws RmesException;
    JSONArray findFragmentByUuidWithChildren(String uuid) throws RmesException;
    String filteredSearchText(String index, String texte) throws RmesException;


    String searchTexteByType(String index, String texte, DDIItemType type) throws RmesException;
    String searchByType(String index, DDIItemType type) throws RmesException;
    List<Map<String,String>> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws Exception;

    String getRessourcePackage(String uuid) throws ExceptionColecticaUnreachable, JsonProcessingException, RmesExceptionIO, ParseException;

    String getByType(DDIItemType type) throws IOException, ExceptionColecticaUnreachable, ParseException;

    void sendUpdateColectica(String ddiUpdatingInJson, TransactionType transactionType) throws RmesException;

}
