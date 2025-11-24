package fr.insee.rmes.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaItemTest {

    @Test
    void shouldReturnAttributesOfColecticaItem() throws Exception {
        ColecticaItem colecticaItem = new ColecticaItem();
        colecticaItem.setIdentifier("mockedIdentifier");
        colecticaItem.setItem("mockedItem");
        colecticaItem.setItemType("mockedItemType");
        colecticaItem.setItemFormat("mockedItemFormat");
        colecticaItem.setDeprecated(true);
        colecticaItem.setNotes(new JSONArray());
        colecticaItem.setProvisional(true);
        colecticaItem.setVersion("mockedVersion");
        colecticaItem.setPublished(true);
        colecticaItem.setAgencyId("mockedAgencyId");
        colecticaItem.setVersionDate("mockedVersionDate");
        colecticaItem.setVersionRationale(new JSONObject().put("id","idExample"));
        colecticaItem.setVersionResponsibility("mockedVersionResponsibility");

        boolean verifyIdentifier = "mockedIdentifier".equals(colecticaItem.getIdentifier());
        boolean verifyItem = "mockedItem".equals(colecticaItem.getItem());
        boolean verifyType = "mockedItemType".equals(colecticaItem.getItemType());
        boolean verifyItemFormat = "mockedItemFormat".equals(colecticaItem.getItemFormat());
        boolean verifyDeprecated = colecticaItem.isDeprecated();
        boolean verifyProvisional = colecticaItem.isProvisional();
        boolean verifyNotes = colecticaItem.getNotes().equals(new JSONArray());
        boolean verifyVersion = "mockedVersion".equals(colecticaItem.getVersion());
        boolean verifyPublished = colecticaItem.isPublished();
        boolean verifyAgencyId = "mockedAgencyId".equals(colecticaItem.getAgencyId());
        boolean verifyVersionDate = "mockedVersionDate".equals(colecticaItem.getVersionDate());
        boolean verifyVersionRationale= colecticaItem.getVersionRationale().toString().equals(new JSONObject().put("id","idExample").toString());
        boolean verifyVersionResponsibility = "mockedVersionResponsibility".equals(colecticaItem.getVersionResponsibility());

        assertTrue(verifyIdentifier &&
                verifyItem &&
                verifyType &&
                verifyItemFormat &&
                verifyDeprecated &&
                verifyNotes &&
                verifyProvisional &&
                verifyVersion &&
                verifyPublished &&
                verifyAgencyId &&
                verifyVersionDate &&
                verifyVersionRationale &&
                verifyVersionResponsibility
        );
    }
}