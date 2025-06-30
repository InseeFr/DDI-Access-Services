package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaSearchItemRequestTest {

    List<String> list = List.of("firstMocked","secondMocked");

    @Test
    void shouldReturnValuesWhenColecticaSearchItemRequest() {

        ColecticaSearchItemRequest colecticaSearchItemRequest = new ColecticaSearchItemRequest();
        colecticaSearchItemRequest.setCultures(list);
        colecticaSearchItemRequest.setItemTypes(list);
        colecticaSearchItemRequest.setLanguageSortOrder(list);
        colecticaSearchItemRequest.setMaxResults(2025);
        colecticaSearchItemRequest.setRankResults(true);
        colecticaSearchItemRequest.setResultOffset(2025);
        colecticaSearchItemRequest.setResultOrdering("mockedResultOrdering");
        colecticaSearchItemRequest.setSearchDepricatedItems(true);
        colecticaSearchItemRequest.setSearchLatestVersion(true);
        colecticaSearchItemRequest.setSearchTerms(list);

        assertTrue(Objects.equals(colecticaSearchItemRequest.getCultures().toString(), list.toString()) &&
                Objects.equals(colecticaSearchItemRequest.getItemTypes().toString(), list.toString()) &&
                Objects.equals(colecticaSearchItemRequest.getLanguageSortOrder().toString(), list.toString()) &&
                colecticaSearchItemRequest.getMaxResults() == 2025 &&
                colecticaSearchItemRequest.getRankResults() &&
                colecticaSearchItemRequest.getResultOffset() == 2025 &&
                Objects.equals(colecticaSearchItemRequest.getResultOrdering(), "mockedResultOrdering") &&
                colecticaSearchItemRequest.getSearchDepricatedItems() &&
                colecticaSearchItemRequest.getSearchLatestVersion() &&
                Objects.equals(colecticaSearchItemRequest.getSearchTerms().toString(), list.toString())

        );
    }
}

