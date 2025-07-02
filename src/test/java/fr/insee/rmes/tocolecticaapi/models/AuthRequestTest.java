package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    @Test
    void shouldReturnAttributesValuesWhenAuthRequest(){

        AuthRequest authRequest = new AuthRequest("mockedUsername","mockedPassword");
        AuthRequest authRequestOther = new AuthRequest("username","password");
        authRequestOther.setPassword("mockedPassword");
        authRequestOther.setUsername("mockedUsername");

        boolean isSameUsername = Objects.equals(authRequestOther.getUsername(), authRequest.getUsername());
        boolean isSamePassWord = Objects.equals(authRequestOther.getPassword(), authRequest.getPassword());

        assertTrue(isSameUsername && isSamePassWord);

    }

}