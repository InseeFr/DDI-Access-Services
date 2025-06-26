package fr.insee.rmes.tocolecticaapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ControllerUtilsTest {

    @Test
    void shouldReturnResponsesForDifferentXMLEntities() {

        List<String> xmlContents = List.of("<para> It's truly ï»¿amazing.</para>","<para> It's \\\"\\\" trulyï»¿ amazing.</para>","<para> It's \" ï»¿truly amazing.</para>");
        List<ResponseEntity<String>> responseEntities = List.of(ControllerUtils.xmltoResponseEntity(xmlContents.getFirst()),ControllerUtils.xmltoResponseEntity(xmlContents.get(2)),ControllerUtils.xmltoResponseEntity(xmlContents.getLast()));

        List<String> prefixActual = List.of(responseEntities.getFirst().toString().substring(0,17),responseEntities.get(1).toString().substring(0,17),responseEntities.getLast().toString().substring(0,17));
        List<String> prefixExpected = List.of("<200 OK OK,<para>","<200 OK OK,<para>","<200 OK OK,<para>");

        List<Boolean> responseEntitiesContainsSymbol = List.of(responseEntities.getFirst().toString().contains("ï»¿"),responseEntities.get(1).toString().contains("ï»¿"),responseEntities.getLast().toString().contains("ï»¿"));
        List<Boolean> responseEntitiesContainsSlash= List.of(responseEntities.getFirst().toString().contains("\""),responseEntities.get(1).toString().contains("\""),responseEntities.getLast().toString().contains("\""));

        boolean identicalPrefix = Objects.equals(prefixActual.toString(), prefixExpected.toString());
        boolean containsSymbol = responseEntitiesContainsSymbol.equals(List.of(false, false, false));
        boolean containsSlash = responseEntitiesContainsSlash.equals(List.of(false,true,true));

        assertTrue(identicalPrefix && containsSymbol && containsSlash);
    }

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