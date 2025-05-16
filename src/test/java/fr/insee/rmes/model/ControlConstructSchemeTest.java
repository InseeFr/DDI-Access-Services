package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControlConstructSchemeTest {

    @Test
    void shouldTestToString() {

        ControlConstructScheme controlConstructScheme = new ControlConstructScheme();
        controlConstructScheme.setIdentifier("id2025");
        controlConstructScheme.setAgencyId("agency2025");

        Sequence sequence = new Sequence();
        sequence.setLabel("label");

        assertTrue(controlConstructScheme.toString().contains(controlConstructScheme.getIdentifier()) &&
                controlConstructScheme.toString().contains(controlConstructScheme.getAgencyId()) &&
                controlConstructScheme.toString().contains(String.valueOf(controlConstructScheme.getSequence()))
        );

    }
}