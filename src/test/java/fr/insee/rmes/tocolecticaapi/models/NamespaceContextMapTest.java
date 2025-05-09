package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NamespaceContextMapTest {

    NamespaceContextMap namespaceContextMap = new NamespaceContextMap();

    @Test
    void shouldThrowSupportedOperationExceptionWhenGetPrefix() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> namespaceContextMap.getPrefix("namespaceURI"));
        assertNull(exception.getMessage());
    }

    @Test
    void shouldThrowSupportedOperationExceptionWhenGetPrefixes() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> namespaceContextMap.getPrefixes("namespaceURI"));
        assertNull(exception.getMessage());
    }
}