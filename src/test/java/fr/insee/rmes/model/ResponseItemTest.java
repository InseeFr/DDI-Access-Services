package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ResponseItemTest {

    @Test
    void shouldReturnAttributesWhenResponseItem(){

        ResponseItem firstResponseItem = new ResponseItem();
        ResponseItem secondResponseItem = new ResponseItem("mockedID","mockedParent","mockedLabel");

        firstResponseItem.setId("mockedID");
        firstResponseItem.setParent("mockedParent");
        firstResponseItem.setLabel("mockedLabel");
        firstResponseItem.setGroupId("mockedGroupID");
        firstResponseItem.setSubGroupId("mockedSubGroupID");
        firstResponseItem.setStudyUnitId("mockedStudyUnitId");
        firstResponseItem.setDataCollectionId("mockedDataCollectionId");
        firstResponseItem.setResourcePackageId("mockedResourcePackageId");
        firstResponseItem.setName("mockedResourceName");
        firstResponseItem.setChildren(List.of(new ResponseItem(),new ResponseItem()));

        boolean isSameName = !Objects.equals(firstResponseItem.getName(), secondResponseItem.getName());
        boolean isSameID = Objects.equals(firstResponseItem.getId(), secondResponseItem.getId());
        boolean isSameLabel = Objects.equals(firstResponseItem.getLabel(), secondResponseItem.getLabel());
        boolean isSameParent = Objects.equals(firstResponseItem.getParent(), secondResponseItem.getParent());
        boolean isSameChildren = !Objects.equals(firstResponseItem.getChildren(), secondResponseItem.getChildren());
        boolean isSameGroupID= !Objects.equals(firstResponseItem.getGroupId(), secondResponseItem.getGroupId());
        boolean isSameSubGroupId= !Objects.equals(firstResponseItem.getSubGroupId(), secondResponseItem.getSubGroupId());
        boolean isSameStudyUnitId= !Objects.equals(firstResponseItem.getStudyUnitId(), secondResponseItem.getStudyUnitId());
        boolean isSameDataCollectionId= !Objects.equals(firstResponseItem.getDataCollectionId(), secondResponseItem.getDataCollectionId());
        boolean isSameResourcePackageId= !Objects.equals(firstResponseItem.getResourcePackageId(), secondResponseItem.getResourcePackageId());
        boolean isToStringExpected = Objects.equals(firstResponseItem.toString(), "mockedID-mockedLabel-");

        assertTrue(isSameName && isSameID && isSameLabel && isSameParent && isSameChildren && isSameGroupID && isSameSubGroupId && isSameStudyUnitId && isSameDataCollectionId && isSameResourcePackageId && isToStringExpected);

    }

}