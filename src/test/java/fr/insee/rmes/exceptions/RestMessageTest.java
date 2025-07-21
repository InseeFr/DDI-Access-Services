package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class RestMessageTest {

    @Test
    void shouldCreateRestMessage(){
        RestMessage restMessage = new RestMessage(0,"mockedMessage","mockedString");
        int mockedStatus = 1;
        String mockedMessage="newMockedMessage";
        String mockedDetails = "newMockedDetails";
        restMessage.setStatus(mockedStatus);
        restMessage.setMessage(mockedMessage);
        restMessage.setDetails(mockedDetails);
        assertTrue(restMessage.getStatus()==mockedStatus && Objects.equals(restMessage.getMessage(), mockedMessage) && Objects.equals(restMessage.getDetails(), mockedDetails));
    }
}