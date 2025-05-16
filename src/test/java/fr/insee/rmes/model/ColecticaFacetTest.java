package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaFacetTest {


    @Test
    void shouldTestToString() {

        ColecticaFacet colecticaFacet = new ColecticaFacet();
        colecticaFacet.setItemTypes(List.of("firstValue","lastValue"));
        colecticaFacet.setReverseTraversal(true);

        ColecticaFacet colecticaFacetExample = new ColecticaFacet(List.of("firstValue","lastValue"),true);

        assertTrue(colecticaFacet.toString().contains(String.valueOf(colecticaFacet.getReverseTraversal())) &&
                colecticaFacetExample.toString().contains(String.valueOf(colecticaFacetExample.getItemTypes()))
        );

    }
}