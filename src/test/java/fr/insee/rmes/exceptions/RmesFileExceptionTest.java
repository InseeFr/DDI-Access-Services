package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesFileExceptionTest {

    @Test
    void shouldReturnNonNullRmesFileException(){
        RmesFileException rmesFileException = new RmesFileException("mockedMessage",new Throwable("mockedThrowable"));
        assertNotNull(rmesFileException);
    }
}