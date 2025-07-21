package fr.insee.rmes.utils.ddi;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DDIFragmentDocumentBuilderTest {

    @Test
    void shouldCreateHashMapDDIFragmentDocumentBuilder(){

        Map<String, Enum<Envelope>> mockedFragments = new HashMap<>();
        mockedFragments.put("mockedExample",Envelope.DEFAULT);

        DDIFragmentDocumentBuilder ddiFragmentDocumentBuilder = new DDIFragmentDocumentBuilder();
        var valueBefore = ddiFragmentDocumentBuilder.getFragments();
        ddiFragmentDocumentBuilder.setFragments(mockedFragments);
        var valueAfter = ddiFragmentDocumentBuilder.getFragments();

        assertTrue(valueAfter!=valueBefore && valueAfter==mockedFragments);
    }

}