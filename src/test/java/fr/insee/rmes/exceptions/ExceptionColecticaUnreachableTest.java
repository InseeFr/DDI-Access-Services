package fr.insee.rmes.exceptions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionColecticaUnreachableTest {

    @ParameterizedTest
    @ValueSource(strings = { "223","creator","publisher","id","identifiant","version","lg1","lg2" })
    void shouldReturnValuesOfExceptionColecticaUnreachable(String s) {
        ExceptionColecticaUnreachable firstExceptionUnreachable = new ExceptionColecticaUnreachable(s);
        ExceptionColecticaUnreachable secondExceptionUnreachable = new ExceptionColecticaUnreachable(s,new Throwable());
        boolean isMessageKnown = Objects.equals(firstExceptionUnreachable.getMessage(), s) && Objects.equals(secondExceptionUnreachable.getMessage(), s);
        assertTrue(isMessageKnown);
    }

}