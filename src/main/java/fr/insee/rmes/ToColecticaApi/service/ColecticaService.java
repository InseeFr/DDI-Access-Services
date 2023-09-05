package fr.insee.rmes.ToColecticaApi.service;

import fr.insee.rmes.metadata.exceptions.ExceptionColecticaUnreachable;
import fr.insee.rmes.search.model.DDIItemType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ColecticaService {
    ResponseEntity<?> findFragmentByUuid(String uuid);

    ResponseEntity<?> findInstanceByUuid(String uuid);

    ResponseEntity<?> filteredSearchText(String index, String texte);
    ResponseEntity<?> getJsonWithChild(String identifier, String outputField, String fieldLabelName) throws Exception;

    String replaceXmlParameters(String inputXml, DDIItemType type, String label, int version, String name, String idepUtilisateur);

    ResponseEntity<?> getByType(DDIItemType type) throws IOException, ExceptionColecticaUnreachable;
}
