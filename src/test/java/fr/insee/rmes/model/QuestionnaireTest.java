package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireTest {

    @Test
    void shouldTestToString() {

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setLabel("Label");
        questionnaire.setTypeOfInstrument("TypeOfInstrument");

        ControlConstructReference controlConstructReference = new ControlConstructReference();
        controlConstructReference.setVersion("version2025");
        questionnaire.setControlConstructReference(controlConstructReference);

        questionnaire.setAgencyId("agenceId");
        questionnaire.setVersion("version");
        questionnaire.setIdentifier("identifier");

        assertTrue(
                questionnaire.toString().contains(questionnaire.getLabel()) &&
                        questionnaire.toString().contains(questionnaire.getTypeOfInstrument()) &&
                        questionnaire.toString().contains(String.valueOf(questionnaire.getControlConstructReference())) &&
                        questionnaire.toString().contains(questionnaire.getAgencyId()) &&
                        questionnaire.toString().contains(questionnaire.getVersion()) &&
                        questionnaire.toString().contains(questionnaire.getIdentifier()));
    }

}