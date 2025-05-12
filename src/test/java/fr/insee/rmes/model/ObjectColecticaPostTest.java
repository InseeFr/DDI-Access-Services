package fr.insee.rmes.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ObjectColecticaPostTest {

    @Test
    void shouldTestToString() {

     TargetItem targetItem = new TargetItem();
     targetItem.setIdentifier("exampleOfTargetItem");

     ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
     objectColecticaPost.setItemTypes(List.of("cat","dog"));
     objectColecticaPost.setTargetItem(targetItem);
     objectColecticaPost.setUseDistinctResultItem(true);
     objectColecticaPost.setUseDistinctTargetItem(true);

     assertTrue(objectColecticaPost.toString().contains(String.valueOf(objectColecticaPost.getItemTypes())) &&
                objectColecticaPost.toString().contains(String.valueOf(objectColecticaPost.getTargetItem())) &&
                objectColecticaPost.toString().contains(String.valueOf(objectColecticaPost.getUseDistinctTargetItem())) &&
                objectColecticaPost.toString().contains(String.valueOf(objectColecticaPost.getUseDistinctResultItem()))
        );
    }


    @Test
    void shouldJsonToString() throws JsonProcessingException {

        TargetItem targetItem = new TargetItem();
        targetItem.setIdentifier("exampleOfTargetItem");

        ObjectColecticaPost objectColecticaPost = new ObjectColecticaPost();
        objectColecticaPost.setItemTypes(List.of("cat", "dog"));
        objectColecticaPost.setTargetItem(targetItem);
        objectColecticaPost.setUseDistinctResultItem(true);
        objectColecticaPost.setUseDistinctTargetItem(true);

        String expected="{\"ItemTypes\":[\"cat\",\"dog\"],\"TargetItem\":{\"AgencyId\":null,\"Identifier\":\"exampleOfTargetItem\",\"Version\":null},\"UseDistinctResultItem\":true,\"UseDistinctTargetItem\":true}";

        assertEquals(expected,objectColecticaPost.toJson().toString());
    }

}