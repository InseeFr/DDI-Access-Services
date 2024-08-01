package fr.insee.rmes.metadata.service;

import java.util.List;
import fr.insee.rmes.model.Unit;


public interface MetadataService {

    List<Unit> getUnits() throws Exception;
}
