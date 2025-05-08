package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RmesExceptionIOTest {


    @Test
    void shouldReturnRestMessageObjectParametersPossibilities() {

        ArrayList<Integer> status = new ArrayList<>(Arrays.asList(ErrorCodes.LINK_CREATION_RIGHTS_DENIED,-1));
        ArrayList<String> messages = new ArrayList<>(Arrays.asList("Unauthorized",null));
        ArrayList<String> details = new ArrayList<>(Arrays.asList("Invalid password or tocken",null));

        RmesExceptionIO rmesExceptionIOExampleOne= new RmesExceptionIO(status.getFirst(), messages.getFirst(), details.getFirst());
        RmesExceptionIO rmesExceptionIOExampleTwo= new RmesExceptionIO(status.getLast(),messages.getLast(),details.getLast());

        assertTrue(rmesExceptionIOExampleOne.getMessage()!=null && rmesExceptionIOExampleTwo.getMessage()==null);
    }

}
