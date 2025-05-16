package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryReferenceTest {

    @Test
    void shouldTestToString() {
        CategoryReference categoryReference = new CategoryReference();
        categoryReference.setTypeOfObject("TypeOfObjectExample");
        assertTrue(categoryReference.toString().contains(categoryReference.getTypeOfObject()));
    }
}