package fr.insee.rmes.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaItemRefListTest {

    @Test
    void shouldTestToString() throws JSONException {

        ColecticaItemPostRef elementA = new ColecticaItemPostRef();
        elementA.setIdentifier("identifierA");

        ColecticaItemPostRef elementB = new ColecticaItemPostRef();
        elementB.setIdentifier("identifierB");

        List<ColecticaItemPostRef> items = List.of(elementA,elementB);

        JSONObject jsonObject = new JSONObject().put("creator","unknown");

        ColecticaItemPostRefList colecticaItemPostRefList = new ColecticaItemPostRefList();
        colecticaItemPostRefList.setOptions(jsonObject);
        colecticaItemPostRefList.setItems(items);

        assertTrue(colecticaItemPostRefList.toString().contains(String.valueOf(colecticaItemPostRefList.getItems())) &&
                colecticaItemPostRefList.toString().contains(String.valueOf(colecticaItemPostRefList.getOptions()))
        );
    }
}