package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void shouldReturnAttributesWhenItem() {
        Item item =  new Item();
        item.setItem("mockedItem");
        item.setAgencyId("mockedAgency");
        item.setVersion(2025);
        item.setIdentifier("mockedIdentifier");
        item.setVersionDate(new Date());
        item.setVersionResponsibility("mockedVersionResponsibility");
        item.setIsPublished(true);
        item.setIsDeprecated(true);
        item.setIsProvisional(true);
        item.setItemFormat("mockedItemFormat");

        boolean isNullItem = item.getItem()==null;
        boolean isNullAgencyId = item.getAgencyId()==null;
        boolean isDifferentTo2025 = item.getVersion()!=2025;
        boolean isNullIdentifier = item.getIdentifier()==null;
        boolean isNullVersionDate = item.getVersionDate()==null;
        boolean isNullVersionResponsibility = item.getVersionResponsibility()==null;
        boolean isFalseIsPublished = !item.getIsPublished();
        boolean isFalseIsDeprecated = !item.getIsDeprecated();
        boolean isFalseIsProvisional = !item.getIsProvisional();
        boolean isNullItemFormat = item.getItemFormat()==null;

        List<Boolean> actual = List.of(isNullItem,isNullAgencyId,isDifferentTo2025,isNullIdentifier,isNullVersionDate,isNullVersionResponsibility,isFalseIsPublished,isFalseIsDeprecated,isFalseIsProvisional,isNullItemFormat);
        List<Boolean> expected = List.of(false,false,false,false,false,false,false,false,false,false);

        assertEquals(expected,actual);
    }
}