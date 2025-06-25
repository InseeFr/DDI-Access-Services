package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesNotAcceptableExceptionTest {

    @Test

    void shouldReturnNotNullDetailsWhenRmesNotAcceptableException(){
        boolean a = new RmesNotAcceptableException("mockedMessage","mockedDetails").getDetails()!=null;
        boolean b= new RmesNotAcceptableException("mockedMessage",new JSONArray().put("details")).getDetails()!=null;
        boolean c= new RmesNotAcceptableException(2025,"mockedMessage","mockedDetails").getDetails()!=null;
        boolean d = new RmesNotAcceptableException(2025,"mockedMessage",new JSONArray().put("details")).getDetails()!=null;
        assertTrue(a && b && c && d);
    }
}