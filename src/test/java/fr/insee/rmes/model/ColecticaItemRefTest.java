package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaItemRefTest {

    @Test
    void shouldReturnObjectsWhenColecticaItemRefAndUnformatted() {

        ColecticaItemRef firstColecticaItemRef = new ColecticaItemRef();
        ColecticaItemRef secondColecticaItemRef = new ColecticaItemRef("mockedIdentifier",22,"mockedAgencyID");
        secondColecticaItemRef.unformat();

        boolean isDifferentUnformat = firstColecticaItemRef.unformat()==secondColecticaItemRef.unformat();

        ColecticaItemRef.Unformatted firstUnformatted = new ColecticaItemRef.Unformatted("item1",2,"item3");
        ColecticaItemRef.Unformatted secondUnformatted = new ColecticaItemRef.Unformatted();

        boolean isDifferentFormat = firstUnformatted.format()==secondUnformatted.format();

        assertEquals(List.of(false,false),List.of(isDifferentUnformat,isDifferentFormat));
    }
}