package fr.insee.rmes.tocolecticaapi;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomUUIDExtensionFunctionTest {

    @Test
    void shouldReturnResponseConstructionWhenCall() throws SaxonApiException {
        RandomUUIDExtensionFunction randomUUIDExtensionFunction = new RandomUUIDExtensionFunction();
        String [] sequence = randomUUIDExtensionFunction.call(null).toString().split("-");
        assertTrue(sequence.length==5 && sequence[0].length()==8 && sequence[1].length()==4 && sequence[2].length()==4 && sequence[3].length()==4 && sequence[4].length()==12 );
    }

}