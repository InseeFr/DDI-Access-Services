package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudyUnitTest {

    @Test
    void shouldTestToString() {

        Citation citation = new Citation();
        citation.setTitle("title");

        DataCollection dataCollection = new DataCollection();
        dataCollection.setIdentifier("dataCollection");


        StudyUnit studyUnit = new StudyUnit();
        studyUnit.setCitation(citation);
        studyUnit.setDatacollection(dataCollection);
        studyUnit.setVersion("version");
        studyUnit.setAgencyId("agenceID");
        studyUnit.setIdentifier("identifier");

        assertTrue(studyUnit.toString().contains(String.valueOf(studyUnit.getCitation())) &&
                studyUnit.toString().contains(String.valueOf(studyUnit.getDatacollection())) &&
                studyUnit.toString().contains(studyUnit.getAgencyId()) &&
                studyUnit.toString().contains(studyUnit.getVersion()) &&
                studyUnit.toString().contains(studyUnit.getIdentifier())
        );

    }

}