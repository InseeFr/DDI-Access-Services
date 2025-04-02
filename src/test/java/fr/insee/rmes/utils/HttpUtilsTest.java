package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class HttpUtilsTest {


    @Test
    void filterBOM() throws IOException {
        var filteredContentWithBom=HttpUtils.filterBOM(HttpUtilsTest.class.getResourceAsStream("/utf8-bom/fichierAvecBom.xml").readAllBytes());
        var filteredContentWithoutBom=HttpUtils.filterBOM(HttpUtilsTest.class.getResourceAsStream("/utf8-bom/fichierSansBom.xml").readAllBytes());
        var diff=DiffBuilder.compare(Input.fromString(filteredContentWithBom))
                .withTest(Input.fromString(filteredContentWithoutBom))
                .ignoreWhitespace()
                .build();
        assertFalse(diff.hasDifferences(), diff.toString());
    }


    @Test
    void shouldTestIfObjectsHaveBom() throws IOException {
        byte[] filteredContentWithBom= HttpUtilsTest.class.getResourceAsStream("/utf8-bom/fichierAvecBom.xml").readAllBytes();
        byte[] filteredContentWithoutBom= HttpUtilsTest.class.getResourceAsStream("/utf8-bom/fichierSansBom.xml").readAllBytes();
        boolean hasBomFilteredContentWithBom = filteredContentWithBom.length > 2 && filteredContentWithBom[0] == (byte) 0xEF && filteredContentWithBom[1] == (byte) 0xBB && filteredContentWithBom[2] == (byte) 0xBF;
        boolean hasBomFilteredContentWithoutBom = filteredContentWithoutBom.length > 2 && filteredContentWithoutBom[0] == (byte) 0xEF && filteredContentWithoutBom[1] == (byte) 0xBB && filteredContentWithoutBom[2] == (byte) 0xBF;
        assertTrue(hasBomFilteredContentWithBom & !hasBomFilteredContentWithoutBom );
    }
}