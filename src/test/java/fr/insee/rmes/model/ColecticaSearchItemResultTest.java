package fr.insee.rmes.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaSearchItemResultTest {

    @Test
    void shouldTestToString() throws JSONException {

        ColecticaSearchItemResult colecticaSearchItemResult = new ColecticaSearchItemResult();

        colecticaSearchItemResult.setItem("item");
        colecticaSearchItemResult.setSummary(new JSONObject().put("summary","example"));
        colecticaSearchItemResult.setLabel(new JSONObject().put("label","example"));
        colecticaSearchItemResult.setDescription(new JSONObject().put("description","example"));
        colecticaSearchItemResult.setRepositoryName(new JSONObject().put("repositoryName","example"));
        colecticaSearchItemResult.setVersionResponsibility(new JSONObject().put("versionResponsibility","example"));
        colecticaSearchItemResult.setVersionRationale(new JSONObject().put("versionRationale","example"));

        colecticaSearchItemResult.setMetadataRank(2025);
        colecticaSearchItemResult.setVersion(25);
        colecticaSearchItemResult.setIsPublished(true);
        colecticaSearchItemResult.setIsAuthoritative(true);
        colecticaSearchItemResult.setIsDeprecated(true);
        colecticaSearchItemResult.setIsProvisional(true);

        colecticaSearchItemResult.setItemType("itemType");
        colecticaSearchItemResult.setAgencyId("agencyId");
        colecticaSearchItemResult.setIdentifier("identifier");
        colecticaSearchItemResult.setItemFormat("itemFormat");
        colecticaSearchItemResult.setNotes("notes");
        colecticaSearchItemResult.setVersionDate("versionDate");
        colecticaSearchItemResult.setTags(List.of("tagFirst","tagLast"));

        Map<String, String> itemName = new HashMap<>();
        itemName.put("firstItem","itemOne");
        itemName.put("lastItem","itemTwo");

        colecticaSearchItemResult.setItemName(itemName);

        assertTrue(colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getMetadataRank())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getVersion())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getIsPublished())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getIsAuthoritative())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getIsDeprecated())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getIsProvisional())) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getItemType()) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getAgencyId()) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getIdentifier()) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getItemFormat()) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getNotes()) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getVersionDate()) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getTags())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getItemName())) &&
                colecticaSearchItemResult.toString().contains(colecticaSearchItemResult.getItem()) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getSummary())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getLabel())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getDescription())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getRepositoryName())) &&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getVersionResponsibility()))&&
                colecticaSearchItemResult.toString().contains(String.valueOf(colecticaSearchItemResult.getVersionRationale())));





    }
}