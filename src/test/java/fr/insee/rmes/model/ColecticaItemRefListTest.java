package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaItemRefListTest {

    @Test
    void shouldReturnValuesWhenToString()  {

        ColecticaItemRefList firstColecticaItemRefList = new ColecticaItemRefList();

        ColecticaItemRef firstColecticaItemRef = new ColecticaItemRef("mockedIdenfifier",5,"mockedAgency");
        ColecticaItemRef secondColecticaItemRef = new ColecticaItemRef("mockedIdenfifier",7,"mockedAgency");
        List<ColecticaItemRef> list = List.of(firstColecticaItemRef,secondColecticaItemRef);

        ColecticaItemRefList secondColecticaItemRefList = new ColecticaItemRefList(list);

        boolean isFirstColecticaIdentifierNull = firstColecticaItemRefList.identifiers==null;
        boolean secondColecticaSizeValue = secondColecticaItemRefList.toString().contains(String.valueOf(list.size()));

        assertTrue(isFirstColecticaIdentifierNull && secondColecticaSizeValue);

    }
}