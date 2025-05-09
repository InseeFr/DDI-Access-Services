package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CodeTest {

    @Test
    void shouldTestToString() {

        Code code = new Code();

        code.setLevelNumber(2025);
        code.setValue("valueExample");
        code.setDiscrete(true);

        CategoryReference categoryReference = new CategoryReference();
        categoryReference.setTypeOfObject("typeOfObjectExample");
        code.setCategoryReference(categoryReference);

        Category category = new Category();
        category.setLabel("labelExample");
        code.setCategory(category);


        assertTrue(code.toString().contains(String.valueOf(code.getLevelNumber())) &&
                code.toString().contains(String.valueOf(code.isDiscrete())) &&
                code.toString().contains(code.getValue()) &&
                code.toString().contains(String.valueOf(code.getCategoryReference())) &&
                code.toString().contains(String.valueOf(code.getCategory()))
        );

    }
}