package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class DataCollectionContextTest {

    @Test
    public void shouldReturnAttributesWhenDataCollectionContext(){
        DataCollectionContext dataCollectionContext = new DataCollectionContext();
        dataCollectionContext.setDataCollectionId("mockedDataCollectionId");
        dataCollectionContext.setSerieId("mockedSerieId");
        dataCollectionContext.setOperationId("mockedOperationId");

        boolean isDataCollectionIdCorrect= Objects.equals(dataCollectionContext.getDataCollectionId(), "mockedDataCollectionId");
        boolean isSerieIdCorrect= Objects.equals(dataCollectionContext.getSerieId(), "mockedSerieId");
        boolean isOperationIdCorrect= Objects.equals(dataCollectionContext.getOperationId(), "mockedOperationId");

        assertTrue(isDataCollectionIdCorrect &&  isSerieIdCorrect && isOperationIdCorrect );
    }

}