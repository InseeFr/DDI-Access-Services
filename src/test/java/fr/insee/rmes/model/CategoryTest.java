package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldTestToString() {
        Category category = new Category();
        category.setLabel("labelExample");
        assertTrue(category.toString().contains(category.getLabel()));
    }
}