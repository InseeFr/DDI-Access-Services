package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class identifierTripleTest {

    @Test
    void shouldTestToString() {

        identifierTriple identifierTriple = new identifierTriple();
        identifierTriple.setIdentifier("identifier");
        identifierTriple.setVersion(2025);
        identifierTriple.setAgencyId("agencyId");

        assertTrue(identifierTriple.toString().contains(identifierTriple.getIdentifier())&&
                identifierTriple.toString().contains(String.valueOf(identifierTriple.getVersion()))&&
                identifierTriple.toString().contains(String.valueOf(identifierTriple.getAgencyId()))

        );

    }
}