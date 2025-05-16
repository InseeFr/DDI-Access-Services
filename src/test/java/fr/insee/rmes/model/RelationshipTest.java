package fr.insee.rmes.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RelationshipTest {

    @Test
    void shouldTestToString() {

        identifierTriple identifierTriple = new identifierTriple();
        identifierTriple.setIdentifier("identifierTriple");

        Relationship relationship = new Relationship();
        relationship.setTypeItem("typeItem");
        relationship.setIdentifierTriple(identifierTriple);

        assertTrue( relationship.toString().contains(relationship.getTypeItem()) &&
                relationship.toString().contains(String.valueOf(relationship.getIdentifierTriple())));
    }


    @Test
    void shouldTestToJson() throws JsonProcessingException {

        identifierTriple identifierTriple = new identifierTriple();
        identifierTriple.setIdentifier("identifierTriple");

        Relationship relationship = new Relationship();
        relationship.setTypeItem("typeItem");
        relationship.setIdentifierTriple(identifierTriple);

        String expected = "{\"Item1\":{\"Item1\":\"identifierTriple\",\"Item2\":null,\"Item3\":null},\"Item2\":\"typeItem\"}";

        assertEquals(expected,relationship.toJson());
    }

}