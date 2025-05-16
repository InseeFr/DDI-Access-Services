package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RelationshipOutTest {

    @Test
    void shouldTestToString() {
        ColecticaItemRef colecticaItemRef = new ColecticaItemRef("identifier",2025,"version");
        ColecticaItemRef colecticaItemRefOther = new ColecticaItemRef("identifierOther",2026,"versionOther");

        RelationshipOut relationship = new RelationshipOut(colecticaItemRef,"itemType");
        relationship.setItemType("itemTypeOther");
        relationship.setRef(colecticaItemRefOther);

        assertTrue(relationship.toString().contains(relationship.getItemType())&&
                relationship.toString().contains(String.valueOf(relationship.getRef()))

        );


    }





}