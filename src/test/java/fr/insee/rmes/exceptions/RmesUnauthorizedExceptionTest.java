package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesUnauthorizedExceptionTest {

    @Test
    void shouldReturnNotNullDetailsWhenRmesUnauthorizedException(){
        boolean a = new RmesUnauthorizedException().getDetails()!=null;
        boolean b = new RmesUnauthorizedException("mockedMessage","mockedDetails").getDetails()!=null;
        boolean c= new RmesUnauthorizedException("mockedMessage",new JSONArray().put("details")).getDetails()!=null;
        boolean d= new RmesUnauthorizedException(2025,"mockedDetails").getDetails()!=null;
        boolean e= new RmesUnauthorizedException(2025,new JSONArray().put("details")).getDetails()!=null;
        boolean f= new RmesUnauthorizedException(2025,"mockedMessage",new JSONArray().put("details")).getDetails()!=null;
        boolean g = new RmesUnauthorizedException(2025,"mockedMessage","mockedDetails").getDetails()!=null;
        boolean h = new RmesUnauthorizedException(2025,"mockedMessage",new JSONObject()).getDetails()!=null;
        assertTrue(a && b && c && d && e && f && g && h);
    }

}