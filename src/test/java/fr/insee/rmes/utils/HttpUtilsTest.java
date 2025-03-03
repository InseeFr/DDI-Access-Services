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
}