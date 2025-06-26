package fr.insee.rmes.metadata.controller;

import fr.insee.rmes.metadata.service.MetadataServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetadataControllerTest {

    @Test
    void shouldReturnAtLeastOneUrlWhenGetUnits() throws Exception {
        MetadataController a = new MetadataController(new MetadataServiceImpl());
        assertTrue(a.getUnits().toString().contains("http://id.insee.fr/unit/"));

    }
}