package fr.insee.rmes.ToColecticaApi.service;

import fr.insee.rmes.ToColecticaApi.models.TransactionType;
import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ColecticaService {
    ResponseEntity<?> findFragmentByUuid(String uuid);

    ResponseEntity<?> findInstanceByUuid(String uuid);

    ResponseEntity<?> filteredSearchText(String index, String texte);
    ResponseEntity<?> SearchByType(String index, DDIItemType type);
    ResponseEntity<?> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws Exception;

    String replaceXmlParameters(String inputXml, DDIItemType type, String label, int version, String name, String idepUtilisateur);

    ResponseEntity<?> getByType(DDIItemType type) throws IOException, ExceptionColecticaUnreachable;

    ResponseEntity<String> sendUpdateColectica(String DdiUpdatingInJson, TransactionType transactionType) throws IOException;

    ResponseEntity<?> transformFile(
            MultipartFile file,
            String idValue,
            String nomenclatureName,
            String suggesterDescription,
            String version,
            String idepUtilisateur,
            String timbre
    );
    ResponseEntity<?> transformFileForComplexList(
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

    ResponseEntity<String> uploadItem(MultipartFile file) throws IOException, ExceptionColecticaUnreachable;
}
