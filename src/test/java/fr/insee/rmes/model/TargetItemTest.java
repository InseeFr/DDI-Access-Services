package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TargetItemTest {

    @Test
    void shouldTestToString() {
        TargetItem targetItem = new TargetItem();
        targetItem.setAgencyId("agencyId");
        targetItem.setIdentifier("identifier");
        targetItem.setVersion(205);

        assertTrue(targetItem.toString().contains(targetItem.getAgencyId()) &&
                targetItem.toString().contains(targetItem.getIdentifier()) &&
                targetItem.toString().contains(String.valueOf(targetItem.getVersion()))
        );
    }
}