package fr.insee.rmes.transfoxsl.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import java.util.HexFormat;
import static org.junit.jupiter.api.Assertions.*;

class RestClientUtilsTest {

    @Test
    void shouldReturnNullWhenReadBodySafely() {
        String actual =RestClientUtils.readBodySafely(null);
        assertEquals("null response",actual);
    }

    @Test
    void shouldReturnStringWhenReadBodySafely() {
        byte[] mockBytes= HexFormat.of().parseHex("e0dee0");
        MockClientHttpResponse mockClientHttpResponse = new MockClientHttpResponse(mockBytes,HttpStatus.CONTINUE);
        String actual =RestClientUtils.readBodySafely(mockClientHttpResponse);
        System.out.println(actual);
        assertEquals("���",actual);
    }

}