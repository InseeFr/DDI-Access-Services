package fr.insee.rmes.metadata.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import fr.insee.rmes.model.Unit;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Service
public class MetadataServiceImpl implements MetadataService {
    @Override
    public List<Unit> getUnits() throws Exception {
            ObjectMapper objectMapper = JsonMapper.builder().build();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("measure-units.json");
            if (inputStream == null) {
                throw new FileNotFoundException("Resource 'measure-units.json' is not found");
            }
            return objectMapper.readValue(inputStream, new TypeReference<List<Unit>>() {});
        }

}
