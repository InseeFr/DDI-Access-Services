package fr.insee.rmes.model;

import fr.insee.rmes.utils.ddi.ItemFormat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaItemPostRefTest {

    @Test
    void shouldTestToString()  {

        ColecticaItemPostRef colecticaItemPostRef = new ColecticaItemPostRef();
        colecticaItemPostRef.setItem("item");
        colecticaItemPostRef.setItemType("itemType");
        colecticaItemPostRef.setDeprecated(true);
        colecticaItemPostRef.setItemFormat(ItemFormat.DDI_31);
        colecticaItemPostRef.setVersionResponsibility("versionResponsibility");
        colecticaItemPostRef.setPublished(true);
        colecticaItemPostRef.setAgencyId("agencyId");
        colecticaItemPostRef.setIdentifier("identifier");

        ColecticaItemPostRef colecticaItemPostRefOther = new ColecticaItemPostRef("identifier","version");

        assertTrue(colecticaItemPostRef.toString().contains(colecticaItemPostRef.getItem()) &&
                colecticaItemPostRef.toString().contains(colecticaItemPostRef.getItemType()) &&
                colecticaItemPostRef.toString().contains(colecticaItemPostRef.getVersionResponsibility()) &&
                colecticaItemPostRef.toString().contains(colecticaItemPostRef.getAgencyId()) &&
                colecticaItemPostRef.toString().contains(colecticaItemPostRef.getIdentifier()) &&
                colecticaItemPostRefOther.toString().contains(String.valueOf(colecticaItemPostRefOther.getItemFormat())) &&
                colecticaItemPostRefOther.toString().contains(String.valueOf(colecticaItemPostRefOther.isDeprecated())) &&
                colecticaItemPostRefOther.toString().contains(String.valueOf(colecticaItemPostRefOther.isPublished()))
                );
    }
}