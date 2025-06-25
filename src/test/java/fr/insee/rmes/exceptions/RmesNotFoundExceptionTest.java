package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesNotFoundExceptionTest {

    @Test
    void shouldReturnNotNullDetailsWhenRmesNotFoundException(){
        boolean a = new RmesNotFoundException("mockedMessage","mockedDetails").getDetails()!=null;
        boolean b= new RmesNotFoundException("mockedMessage").getDetails()!=null;
        boolean c= new RmesNotFoundException(2025,"mockedMessage","mockedDetails").getDetails()!=null;
        assertTrue(a && b && c );
    }
}