package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SequenceTest {

    @Test
    void shouldTestToString() {
        Sequence sequence = new Sequence();
        sequence.setLabel("label");
        sequence.setAgencyId("agenceId");
        sequence.setVersion("version");
        sequence.setIdentifier("identifier");

        assertTrue( sequence.toString().contains(sequence.getLabel()) &&
                sequence.toString().contains(sequence.getAgencyId())&&
                sequence.toString().contains(sequence.getVersion()) &&
                sequence.toString().contains(sequence.getIdentifier())
        );

    }

}