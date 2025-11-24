package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DDIItemTest {
    @Test
    void shouldReturnAttributesWhenDDIItem(){
        DDIItem firstDdiItem = new DDIItem();
        DDIItem secondDdiItem = new DDIItem("mockedID","mockedLable","mockedParent","mockedType");
        firstDdiItem.setType("mockedType");
        assertEquals(firstDdiItem.getType(),secondDdiItem.getType());
    }

}