package fr.insee.rmes.tocolecticaapi.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControllerUtilsTest {

    @Test
    void shouldReturnXmlToResponseEntity() {
        String sentence = "This\\\"is an \"ï»¿\"example.";
        String actual = ControllerUtils.xmltoResponseEntity(sentence).toString();
        assertEquals("<200 OK OK,This\"is an \"\"example.,[]>",actual);
    }

    @Test
    void shouldReturnNullPointerExceptionWhenNullStringForXmlToResponseEntity() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> ControllerUtils.xmltoResponseEntity(null));
        assertTrue(exception.getMessage().contains("Cannot invoke \"String.replace(java.lang.CharSequence, java.lang.CharSequence)\" because \"xmlContent\" is null"));
    }


}