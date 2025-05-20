package fr.insee.rmes.exceptions;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RmesExceptionTest {

    @Test
    void shouldReturnToRestMessageWhenErrorCodeIsNull() {

        JSONArray jsonArray = new JSONArray().put(1).put(2).put(3);
        List<RmesException> listOfRmesException = new ArrayList<>();

        listOfRmesException.add(new RmesException(500,"message","details"));
        listOfRmesException.add(new RmesException(500,"","details"));
        listOfRmesException.add(new RmesException(500,"message",""));
        listOfRmesException.add(new RmesException(500,"",""));

        listOfRmesException.add(new RmesException(HttpStatusCode.valueOf(500),"message","details"));
        listOfRmesException.add(new RmesException(HttpStatusCode.valueOf(500),"","details"));
        listOfRmesException.add(new RmesException(HttpStatusCode.valueOf(500),"message",""));
        listOfRmesException.add(new RmesException(HttpStatusCode.valueOf(500),"",""));

        listOfRmesException.add(new RmesException(500,"message",jsonArray));
        listOfRmesException.add(new RmesException(500,"",jsonArray));
        listOfRmesException.add(new RmesException(500,"message",new JSONArray()));
        listOfRmesException.add(new RmesException(500,"",new JSONArray()));

        List<Integer> keyValueNumber= new ArrayList<>();

        for (RmesException exception : listOfRmesException){
            keyValueNumber.add(exception.getDetails().split(":").length-1);
        }
        assertEquals(List.of(2, 1, 1, 0, 2, 1, 1, 0, 2, 1, 2, 1),keyValueNumber);

    }
}