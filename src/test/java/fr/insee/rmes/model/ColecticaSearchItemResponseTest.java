package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaSearchItemResponseTest {

    @Test
    void shouldReturnAttributesWhenColecticaSearchItemResponse(){
        ColecticaSearchItemResponse colecticaSearchItemResponse = new ColecticaSearchItemResponse();
        List<ColecticaSearchItemResult> results = List.of(new ColecticaSearchItemResult(),new ColecticaSearchItemResult());
        colecticaSearchItemResponse.setResults(results);
        colecticaSearchItemResponse.setTotalResults(2025);
        colecticaSearchItemResponse.setReturnedResults(2025);
        colecticaSearchItemResponse.setDatabaseTime("mockedDatabaseTime");
        colecticaSearchItemResponse.setRepositoryTime("mockedRepositoryTime");

        assertTrue(colecticaSearchItemResponse.getResults().toString().contains("null") &&
                        colecticaSearchItemResponse.getTotalResults()==2025 &&
                colecticaSearchItemResponse.getReturnedResults()==2025 &&
                Objects.equals(colecticaSearchItemResponse.getDatabaseTime(), "mockedDatabaseTime") &&
                Objects.equals(colecticaSearchItemResponse.getRepositoryTime(), "mockedRepositoryTime"));

    }

}