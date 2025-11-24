package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class IdLabelPairTest {

    @Test
    void shouldReturnAttributesWhenIdLabelPairTest(){
        IdLabelPair idLabelPair = new IdLabelPair("mockedID","mockedLabel");
        idLabelPair.setId("ID");
        assertTrue(Objects.equals(idLabelPair.getId(), "ID") && Objects.equals(idLabelPair.getLabel(), "mockedLabel"));
    }
}