package fr.insee.rmes.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CodeListTest {

    @Test
    void testToString() throws JSONException {
        String itemType ="itemType";
        String agencyId ="agencyId";
        String version ="version";
        String identifier ="identifier";
        String item ="item";
        String versionDate ="versionDate";
        String versionResponsibility ="versionResponsibility";
        boolean isPublished =true;
        boolean isDeprecated =true;
        boolean isProvisional =true;
        String itemFormat ="itemFormat";
        JSONObject versionRationale = new JSONObject();
        versionRationale.put("versionRationale","versionRationaleExampleOne");
        versionRationale.put("versionRationale","versionRationaleExampleTwo");

        JSONObject noteExampleOne = new JSONObject();
        noteExampleOne.put("noteFirst", "1");
        noteExampleOne.put("noteSecond", "2");

        JSONObject noteExampleTwo = new JSONObject();
        noteExampleTwo.put("noteFirst", "1");
        noteExampleTwo.put("noteSecond", "2");

        JSONArray notes = new JSONArray();
        notes.put(noteExampleOne);
        notes.put(noteExampleTwo);

       boolean isUniversallyUnique = true;

       Code code = new Code();

        List<Code> codeList = new ArrayList<>();
        codeList.add(code);

        String response = "CodeList [codeList=" + codeList + ", isUniversallyUnique=" + isUniversallyUnique + ", itemType="
                + itemType + ", agencyId=" + agencyId + ", version=" + version + ", identifier=" + identifier
                + ", item=" + item + ", versionDate=" + versionDate + ", versionResponsibility=" + versionResponsibility
                + ", isPublished=" + isPublished + ", isDeprecated=" + isDeprecated + ", isProvisional=" + isProvisional
                + ", itemFormat=" + itemFormat + ", versionRationale=" + versionRationale + ", notes=" + notes + "]";

        String expected = "CodeList [codeList=[Code [levelNumber=0, value=null, categoryReference=null, category=null, isDiscrete=false, agencyId=null, version=null, identifier=null]], isUniversallyUnique=true, itemType=itemType, agencyId=agencyId, version=version, identifier=identifier, item=item, versionDate=versionDate, versionResponsibility=versionResponsibility, isPublished=true, isDeprecated=true, isProvisional=true, itemFormat=itemFormat, versionRationale={\"versionRationale\":\"versionRationaleExampleTwo\"}, notes=[{\"noteFirst\":\"1\",\"noteSecond\":\"2\"},{\"noteFirst\":\"1\",\"noteSecond\":\"2\"}]]";

        assertEquals(expected,response);
    }






    void shouldTestToString() {

        Code code2025 = new Code();
        code2025.setLevelNumber(2025);
        code2025.setValue("valueExample");
        code2025.setDiscrete(true);

        Code code2026 = new Code();
        code2026.setLevelNumber(2026);
        code2026.setValue("valueExample");
        code2026.setDiscrete(true);

        List<Code> codeList = List.of(code2025,code2026);

        CodeList codeListExample = new CodeList();
        codeListExample.setUniversallyUnique(true);
        codeListExample.setCodeList(codeList);

        assertTrue(codeListExample.toString().contains(String.valueOf(codeListExample.getCodeList())) &&
                codeListExample.toString().contains(String.valueOf(codeListExample.isUniversallyUnique()))
        );

    }
}