package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ResponseSearchItemTest {

    @Test
    void shouldReturnAttributesWhenResponseSearchItem(){

        ResponseSearchItem responseSearchItem = new ResponseSearchItem("mockedID","mockedLabel");
        responseSearchItem.setType("mockedType");
        responseSearchItem.setId("mockedID");
        responseSearchItem.setLabel("mockedLabel");
        responseSearchItem.setDescription("mockedDescription");
        responseSearchItem.setVersion(22);
        responseSearchItem.setModalities(List.of("firstMockedModality","secondMockedModality"));
        responseSearchItem.setSubGroups(List.of("firstMockedGroup","secondMockedGroup"));
        responseSearchItem.setSubGroupLabels(List.of("firstMockedSubGroupLabels","secondMockedSubGroupLabels"));
        responseSearchItem.setDataCollections(List.of("firstMockedDataCollections","secondMockedDataCollections"));
        responseSearchItem.setStudyUnits(List.of("firstMockedStudyUnits","secondMockedStudyUnits"));

        boolean isCorrectType = Objects.equals(responseSearchItem.getType(), "mockedType");
        boolean isCorrectID = Objects.equals(responseSearchItem.getId(), "mockedID");
        boolean isCorrectLabel = Objects.equals(responseSearchItem.getLabel(), "mockedLabel");
        boolean isCorrectVersion = responseSearchItem.getVersion()==22;
        boolean isCorrectDescription = Objects.equals(responseSearchItem.getDescription(), "mockedDescription");
        boolean isCorrectModalities = Objects.equals(responseSearchItem.getModalities(), List.of("firstMockedModality","secondMockedModality"));
        boolean isCorrectSubGroups = Objects.equals(responseSearchItem.getSubGroups(), List.of("firstMockedGroup","secondMockedGroup"));
        boolean isCorrectSubGroupsLabels = Objects.equals(responseSearchItem.getSubGroupLabels(), List.of("firstMockedSubGroupLabels","secondMockedSubGroupLabels"));
        boolean isCorrectDataCollections = Objects.equals(responseSearchItem.getDataCollections(), List.of("firstMockedDataCollections","secondMockedDataCollections"));
        boolean isCorrectStudyUnits = Objects.equals(responseSearchItem.getStudyUnits(),List.of("firstMockedStudyUnits","secondMockedStudyUnits"));
        boolean containsWord = responseSearchItem.toString().contains(responseSearchItem.getId());

        assertTrue(isCorrectType &&
                isCorrectID &&
                isCorrectLabel &&
                isCorrectVersion &&
                isCorrectDescription &&
                isCorrectModalities &&
                isCorrectSubGroups &&
                isCorrectSubGroupsLabels &&
                isCorrectDataCollections &&
                isCorrectStudyUnits &&
                containsWord);

    }

}




