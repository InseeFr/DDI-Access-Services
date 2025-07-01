package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class DDIQueryTest {

    @Test
    void  shouldReturnAttributesWhenDDIQuery() {

        DDIQuery firstDDIQuery = new DDIQuery();
        DDIQuery secondDDIQuery = new DDIQuery("mockedLabel");
        secondDDIQuery.setType("mockedType");
        firstDDIQuery.setLabel("mockedLabel");

        boolean sameType = Objects.equals(firstDDIQuery.getType(), secondDDIQuery.getType());
        boolean sameLabel = Objects.equals(firstDDIQuery.getLabel(), secondDDIQuery.getLabel());

        assertTrue(!sameType && sameLabel);

    }

}