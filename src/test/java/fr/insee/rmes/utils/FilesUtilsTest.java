package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FilesUtilsTest {

    @Test
    void shouldReduceFileNameSize() {
        String fileName = "DDI_ACCESS_SERVICES_PROJECT.java";
        int maxLength = 10;
        String response = FilesUtils.reduceFileNameSize(fileName,maxLength);
        assertEquals("DDI_ACCESS",response);

    }

    @Test
    void removeAsciiCharacters() {
        List<String> fileNames =  List.of("nœud","nŒud","ÀöBÎCčDžpEFsàGíHáIýJdK","Ddi access-SERVICE!?");
        List<String> actual = new ArrayList<>();

        for (String element : fileNames){
            actual.add(FilesUtils.removeAsciiCharacters(element));
        }
        
        List<String> expected = List.of("noeud", "nOEud", "AoBICcDzpEFsaGiHaIyJdK", "Ddi accessSERVICE");
        assertEquals(expected,actual);

    }

}