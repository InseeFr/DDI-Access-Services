package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class FilesUtilsTest {

    @ParameterizedTest
    @ValueSource(ints = { 0,1,2,3,4,5,6,7,8 })
    void shouldReduceFileNameSize(int maxLength) {
        String fileName ="Filename";
        String actual = FilesUtils.reduceFileNameSize(fileName,maxLength);

        String expected =  switch (maxLength) {
            case 0 -> "";
            case 1 -> "F";
            case 2 -> "Fi";
            case 3 -> "Fil";
            case 4 -> "File";
            case 5 -> "Filen";
            case 6 -> "Filena";
            case 7 -> "Filenam";
            case 8 -> "Filename";
            default -> "Bad request !";
        };

        assertEquals(expected,actual);
    }

    @Test
    void shouldRemoveAsciiCharacters() {
        String stringExample = "œ"+"-"+"Œ"+"-"+"\\p{M}+"+"-"+"\\p{Punct}";
        String actual = FilesUtils.removeAsciiCharacters(stringExample);
        String expected = "oeOEpMpPunct";
        assertEquals(expected,actual);
    }



}