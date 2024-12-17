package fr.insee.rmes.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testStringContainsItemFromList_WhenTokenContainsAnItem_ShouldReturnTrue() {
        String token = "hello world";
        String[] list = {"hello", "test", "example"};

        boolean result = StringUtils.stringContainsItemFromList(token, list);

        assertTrue(result, "The token contains an item from the list, so it should return true");
    }

    @Test
    void testStringContainsItemFromList_WhenTokenDoesNotContainAnyItem_ShouldReturnFalse() {
        String token = "goodbye world";
        String[] list = {"hello", "test", "example"};

        boolean result = StringUtils.stringContainsItemFromList(token, list);

        assertFalse(result, "The token does not contain any item from the list, so it should return false");
    }

    @Test
    void testStringContainsItemFromList_WhenListIsEmpty_ShouldReturnFalse() {
        String token = "hello world";
        String[] list = {};

        boolean result = StringUtils.stringContainsItemFromList(token, list);

        assertFalse(result, "An empty list should always return false");
    }

    @Test
    void testStringContainsItemFromList_WhenTokenIsEmpty_ShouldReturnFalse() {
        String token = "";
        String[] list = {"hello", "test"};

        boolean result = StringUtils.stringContainsItemFromList(token, list);

        assertFalse(result, "An empty token should return false");
    }

    @Test
    void testStringContainsItemFromList_WhenBothTokenAndListAreEmpty_ShouldReturnFalse() {
        String token = "";
        String[] list = {};

        boolean result = StringUtils.stringContainsItemFromList(token, list);

        assertFalse(result, "An empty token and an empty list should return false");
    }
}