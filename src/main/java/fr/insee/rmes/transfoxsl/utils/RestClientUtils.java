package fr.insee.rmes.transfoxsl.utils;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestClientUtils {

    private RestClientUtils() {}

    public static String readBodySafely(ClientHttpResponse response) {
        if (response ==null){
            return "null response";
        };
        try {
            return new String(response.getBody().readAllBytes());
        } catch (IOException e) {
            return "Error "+e.getMessage()+" while reading error response body";
        }
    }

}
