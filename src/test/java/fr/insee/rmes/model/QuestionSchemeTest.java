package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionSchemeTest {

    @Test
    void shouldTestToString() {

        QuestionScheme questionScheme = new QuestionScheme();
        questionScheme.setTypeOfobject("typeOfObject");
        questionScheme.setAgencyId("agenceId");
        questionScheme.setVersion("version");
        questionScheme.setIdentifier("identifier");

        assertTrue(questionScheme.toString().contains(questionScheme.getTypeOfobject()) &&
                questionScheme.toString().contains(questionScheme.getAgencyId()) &&
                questionScheme.toString().contains(questionScheme.getVersion()) &&
                questionScheme.toString().contains(questionScheme.getIdentifier()));
    }
}