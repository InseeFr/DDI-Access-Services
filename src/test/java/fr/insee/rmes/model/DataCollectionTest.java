package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataCollectionTest {

    @Test
    void testToString() {
        Question question = new Question();
        question.setVersion("version2025");
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setLabel("myQuestionnaire");
        ControlConstructScheme controlConstructScheme = new ControlConstructScheme();
        controlConstructScheme.setIdentifier("myControlConstructScheme");

        DataCollection dataCollection = new DataCollection();
        dataCollection.setControlConstructScheme(controlConstructScheme);
        dataCollection.setQuestionnaire(questionnaire);
        dataCollection.setQuestion(question);
        dataCollection.setLabel("label");
        dataCollection.setAgencyId("agencyId");
        dataCollection.setVersion("version");
        dataCollection.setIdentifier("identifier");

        assertTrue(dataCollection.toString().contains(dataCollection.getLabel()) &&
                dataCollection.toString().contains(String.valueOf(dataCollection.getQuestion())) &&
                dataCollection.toString().contains(String.valueOf(dataCollection.getQuestionnaire())) &&
                dataCollection.toString().contains(String.valueOf(dataCollection.getControlConstructScheme())) &&
                dataCollection.toString().contains(dataCollection.getAgencyId()) &&
                dataCollection.toString().contains(dataCollection.getVersion()) &&
                dataCollection.toString().contains(dataCollection.getIdentifier())
        );

    }

}