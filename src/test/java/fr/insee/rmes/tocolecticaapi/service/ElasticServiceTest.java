package fr.insee.rmes.tocolecticaapi.service;

import fr.insee.rmes.model.DDIItemType;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElasticServiceTest {


    ElasticService elasticService = new ElasticService("elasticUrl","elasticApiKey");

    @Test
    void shouldSearchReturnAnException() {
        ResourceAccessException exception = assertThrows((ResourceAccessException.class),() -> elasticService.search("indexExample","texte"));
        assertEquals("I/O error on GET request for \"elasticUrl/elasticUrl/indexExample/_search\": Target host is not specified", exception.getMessage());
    }

    @Test
    void shouldSearchTextByTypeReturnAnException() {
        ResourceAccessException exception = assertThrows((ResourceAccessException.class),() -> elasticService.searchTextByType("index","texte",DDIItemType.CODE_LIST));
        assertEquals("I/O error on POST request for \"elasticUrl/elasticUrl/index/_search\": Target host is not specified", exception.getMessage());

    }


}