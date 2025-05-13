package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class RmesExceptionHandlerTest {

    RmesExceptionHandler rmesExceptionHandler = new RmesExceptionHandler();

    @ParameterizedTest
    @ValueSource(ints = { 200, 400,500 })
    void shouldThrowHandleRmesException(int code) {
        RmesException exception = new RmesException(code,"message","details");
        ResponseEntity<String> responseHandleRmesException = rmesExceptionHandler.handleRmesException(exception);
        assertTrue(responseHandleRmesException.toString().startsWith("<"+ code) && responseHandleRmesException.toString().endsWith(">"));
    }


    @ParameterizedTest
    @ValueSource(ints = { 200, 400,500 })
    void shouldThrowHandleRmesExceptionIO(int code) {
        RmesExceptionIO exception = new RmesExceptionIO(code,"message","details");
        ResponseEntity<String>  response = rmesExceptionHandler.handleRmesExceptionIO(exception);
        assertTrue(response.toString().startsWith("<"+ code) && response.toString().endsWith(">"));
    }
}