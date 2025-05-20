package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SubGroupTest {

    @Test
    void shouldTestToString() {

        Citation citation = new Citation();
        citation.setTitle("exampleOfTitle");

        StudyUnit studyUnitFirst = new StudyUnit();
        studyUnitFirst.setIdentifier("studyUnitFirst");

        StudyUnit studyUnitLast = new StudyUnit();
        studyUnitLast.setIdentifier("studyUnitLast");
        List<StudyUnit> studyUnits= List.of(studyUnitFirst,studyUnitLast);

        SubGroup subGroup = new SubGroup();
        subGroup.setStudyUnits(studyUnits);
        subGroup.setCitation(citation);
        subGroup.setAgencyId("agendaId");
        subGroup.setVersion("version");
        subGroup.setIdentifier("identifier");

        assertTrue( subGroup.toString().contains(String.valueOf(studyUnits)) &&
                subGroup.toString().contains(String.valueOf(subGroup.getCitation())) &&
                subGroup.toString().contains(subGroup.getAgencyId())&&
                subGroup.toString().contains(subGroup.getVersion())&&
                subGroup.toString().contains(subGroup.getIdentifier())
        );

    }
}