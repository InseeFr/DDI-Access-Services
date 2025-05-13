package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    @Test
    void shouldTestToString() {

        Unit unit = new Unit();
        unit.setUri("uriExample");
        unit.setLabel("Label");

        assertTrue(unit.toString().contains(unit.getUri()) &&
                unit.toString().contains((unit.getLabel()))
        );
    }
}