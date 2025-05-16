package fr.insee.rmes.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaItemPostRefListTest {

    @Test
    void shouldTestToString() throws JSONException {

        ColecticaItemPostRef colecticaItemPostRefFirst = new ColecticaItemPostRef("identifierFirst","versionFirst");
        ColecticaItemPostRef colecticaItemPostRefLast = new ColecticaItemPostRef("identifierLast","versionLast");
        List<ColecticaItemPostRef> items = List.of(colecticaItemPostRefFirst,colecticaItemPostRefLast);
        JSONObject options = new JSONObject().put("color","blue");

        ColecticaItemPostRefList colecticaItemPostRefList = new ColecticaItemPostRefList();
        colecticaItemPostRefList.setItems(items);
        colecticaItemPostRefList.setOptions(options);

        assertTrue(colecticaItemPostRefList.toString().contains(String.valueOf(colecticaItemPostRefList.getItems())) &&
                colecticaItemPostRefList.toString().contains(String.valueOf(colecticaItemPostRefList.getOptions()))
        );

    }
}