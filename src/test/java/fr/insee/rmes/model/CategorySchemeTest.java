package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CategorySchemeTest {

    @Test
    void shouldTestToString() {
        CategoryScheme categoryScheme = new CategoryScheme();

        Category rabbit = new Category();
        rabbit.setLabel("rabbit");
        Category cat= new Category();
        rabbit.setLabel("cat");
        List<Category> categories = List.of(rabbit,cat);

        categoryScheme.setCategories(categories);
        categoryScheme.setLabel("LabelExample");

        assertTrue(categoryScheme.toString().contains(categoryScheme.getLabel()) && categoryScheme.toString().contains(categoryScheme.getCategories().toString()));
    }
}