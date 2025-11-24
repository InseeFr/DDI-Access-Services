package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesBadRequestExceptionTest {

    @Test
    void shouldReturnNonNullDetailsWhenGetDetailsForAllConstructors(){
        boolean a = new RmesBadRequestException("mockedMessage").getDetails() !=null;
        boolean b = new RmesBadRequestException("mockedMessage","mockedDetails").getDetails() !=null;
        boolean c = new RmesBadRequestException("mockedMessage",new JSONArray().put("mockedDetails")).getDetails() !=null;
        boolean d = new RmesBadRequestException(2025,"mockedMessage","mockedDetails").getDetails() !=null;
        boolean e = new RmesBadRequestException("mockedMessage",new JSONArray().put("mockedDetails")).getDetails() !=null;
        boolean f = new RmesBadRequestException(10,"mockedMessage").getDetails() !=null;
        boolean g = new RmesBadRequestException(10,"mockedMessage",new JSONObject()).getDetails() !=null;
        boolean h = new RmesBadRequestException(43,"mockedMessage",new JSONArray().put("mockedDetails")).getDetails() !=null;
        assertTrue(a && b && c && d && e && f && g && h);
    }
}
