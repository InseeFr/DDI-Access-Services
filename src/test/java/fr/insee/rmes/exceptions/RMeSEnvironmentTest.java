package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import static org.junit.jupiter.api.Assertions.*;

class RMeSEnvironmentTest {

    @Test
    void shouldReturnEmptyStringWhenGetEnvironment() {
        MockEnvironment mockEnvironment = new MockEnvironment();
        RMeSEnvironment rMeSEnvironment = new RMeSEnvironment(mockEnvironment);
        assertEquals("{}", rMeSEnvironment.getEnvironment());
    }
}