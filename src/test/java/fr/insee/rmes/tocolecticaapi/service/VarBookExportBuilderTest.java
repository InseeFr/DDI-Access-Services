package fr.insee.rmes.tocolecticaapi.service;

import fr.insee.rmes.exceptions.RmesException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VarBookExportBuilderTest {

    @ParameterizedTest
    @ValueSource(strings = { " ", "---","<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +"<!DOCTYPE article PUBLIC>\n" +"<article lang=\"\">\n" + "  <para>This text is an example of what can be done with XML. It's truly amazing.</para>\n" +"</article>" })
    void getData(String exampleOfString) {
        VarBookExportBuilder varBookExportBuilder = new VarBookExportBuilder();
        RmesException exception =assertThrows(RmesException.class, () -> varBookExportBuilder.getData(exampleOfString));
        assertTrue(exception.getDetails().contains("Can't parse xml"));
    }

}