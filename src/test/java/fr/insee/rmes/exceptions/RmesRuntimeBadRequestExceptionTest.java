package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesRuntimeBadRequestExceptionTest {

    @Test
    void shouldReturnNotNullMessageWhenRmesRuntimeBadRequestException(){
        RmesRuntimeBadRequestException rmesRuntimeBadRequestException = new RmesRuntimeBadRequestException("mockedMessage");
        assertNotNull(rmesRuntimeBadRequestException.getMessage());
    }
}