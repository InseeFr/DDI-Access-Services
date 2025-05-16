package fr.insee.rmes.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControlConstructReferenceTest {

    @Test
    void shouldTestToString() throws JSONException {

        ControlConstructReference  constructReference = new ControlConstructReference();

        constructReference.setItemType("itemType");
        constructReference.setAgencyId("agencyId");
        constructReference.setVersion("version");
        constructReference.setIdentifier("identifier");
        constructReference.setItem("item");
        constructReference.setVersionDate("versionDate");
        constructReference.setVersionResponsibility("versionResponsibility");
        constructReference.setPublished(true);
        constructReference.setDeprecated(true);
        constructReference.setProvisional(true);
        constructReference.setItemFormat("itemFormat");
        constructReference.setNotes(new JSONArray().put("a").put("b"));
        constructReference.setVersionRationale(new JSONObject().put("creator","example"));

        assertTrue(
                constructReference.toString().contains(constructReference.getAgencyId()) &&
                constructReference.toString().contains(constructReference.getVersion()) &&
                constructReference.toString().contains(constructReference.getIdentifier())
        );



    }
}