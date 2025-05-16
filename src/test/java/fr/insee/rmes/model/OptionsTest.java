package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OptionsTest {

    @Test
    void shouldTestToString() {

        Options options = new Options();
        options.setSetName("name");
        options.setVersionRationale(new Object());

        assertTrue(options.toString().contains(options.getSetName()) &&
                options.toString().contains(String.valueOf(options.getVersionRationale())));



    }
}