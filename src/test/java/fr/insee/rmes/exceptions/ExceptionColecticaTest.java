package fr.insee.rmes.exceptions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionColecticaTest {

    @ParameterizedTest
    @ValueSource(strings = { "a","b","c","d","e","f","g","h" })
    void shouldReturnValuesOfExceptionColecticaMethods(String s) {
        ExceptionColectica firstExceptionColectica = new ExceptionColectica(s);
        ExceptionColectica secondExceptionColectica = new ExceptionColectica(s,new Throwable());
        boolean isExceptionKnown = firstExceptionColectica.isExceptionKnown() && secondExceptionColectica.isExceptionKnown();
        boolean isMessageKnown = Objects.equals(firstExceptionColectica.getMessage(), s) && Objects.equals(secondExceptionColectica.getMessage(), s);
        assertTrue(isExceptionKnown && isMessageKnown);
    }
}