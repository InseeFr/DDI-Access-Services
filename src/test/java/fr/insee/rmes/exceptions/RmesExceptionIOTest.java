package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RmesExceptionIOTest {

    @Test
    void shouldReturnRestMessageObject() {
        int status =401;
        String message= "Absent rights";
        String details = "Invalid password or tocken";
        RmesExceptionIO rmesExceptionIO= new RmesExceptionIO(status,message,details);
        RestMessage restMessage= rmesExceptionIO.toRestMessage();
        assertNotNull(restMessage.toString());
    }

}
