package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    void shouldTestIfObjectsHaveBom() throws IOException{
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        List<byte[]> bytes = List.of("project".getBytes(StandardCharsets.ISO_8859_1),"Léopold".getBytes(StandardCharsets.UTF_8),bom);
        List<Boolean> hasBom = new ArrayList<>();
        for(byte[] byteExample : bytes){
            hasBom.add(byteExample.length > 2
                    && byteExample[0] == (byte) 0xEF
                    && byteExample[1] == (byte) 0xBB
                    && byteExample[2] == (byte) 0xBF);
        }
        assertEquals(List.of(false,false,true),hasBom);
    }
}