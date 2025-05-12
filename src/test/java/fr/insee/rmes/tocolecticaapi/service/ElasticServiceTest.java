package fr.insee.rmes.tocolecticaapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElasticServiceTest {

    @Test
    void shouldSearchReturnAnException() {
        ElasticService elasticService = new ElasticService("elasticUrl","elasticApiKey");
        ResourceAccessException exception = assertThrows((ResourceAccessException.class),() -> elasticService.search("indexExample","texte"));
        assertEquals("I/O error on GET request for \"elasticUrl/elasticUrl/indexExample/_search\": Target host is not specified", exception.getMessage());
    }

}