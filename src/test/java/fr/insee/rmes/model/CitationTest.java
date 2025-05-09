package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CitationTest {

    @Test
    void shouldTestToString() {
        Citation citation = new Citation();
        citation.setTitle("titleExample");
        assertTrue(citation.toString().contains(citation.getTitle()));
    }
}