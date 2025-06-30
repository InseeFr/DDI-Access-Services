package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ColecticaPostRefDisplayedTest {

    @Test
    void shouldReturnAttributeWhenGetItem() {
        ColecticaPostRefDisplayed colecticaPostRefDisplayed = new ColecticaPostRefDisplayed();
        colecticaPostRefDisplayed.setItem("mockedItem");
        assertEquals("mockedItem",colecticaPostRefDisplayed.getItem());

    }
}