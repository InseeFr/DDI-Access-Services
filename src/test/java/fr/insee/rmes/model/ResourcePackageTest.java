package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.util.Map;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class ResourcePackageTest {

    @Mock
    Map<String, String> references;

    @Test
    void shouldReturnAttributesWhenResourcePackage(){

        ResourcePackage firstResourcePackage = new ResourcePackage();
        ResourcePackage secondResourcePackage = new ResourcePackage("mockedID");

        firstResourcePackage.setId("mockedID");
        firstResourcePackage.setReferences(references);

        boolean sameID = Objects.equals(firstResourcePackage.getId(), secondResourcePackage.getId());
        boolean sameReferences = Objects.equals(firstResourcePackage.getReferences(), secondResourcePackage.getReferences());

        assertTrue(sameID && !sameReferences);
    }



}