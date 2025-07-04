package fr.insee.rmes.tocolecticaapi;

import net.sf.saxon.s9api.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomUUIDExtensionFunctionTest {

    @Test
    void shouldReturnResponseConstructionWhenCall() throws SaxonApiException {
        RandomUUIDExtensionFunction randomUUIDExtensionFunction = new RandomUUIDExtensionFunction();
        String [] sequence = randomUUIDExtensionFunction.call(null).toString().split("-");
        assertTrue(sequence.length==5 && sequence[0].length()==8 && sequence[1].length()==4 && sequence[2].length()==4 && sequence[3].length()==4 && sequence[4].length()==12 );
    }

    @Test
    void shouldReturnAttributesWhenRandomUUIDExtensionFunction() {
        RandomUUIDExtensionFunction random = new RandomUUIDExtensionFunction();

        boolean isEqualGetName = Objects.equals(random.getName().toString(), "randomUUID");
        boolean isEqualGetResultType = random.getResultType().toString()!=null;
        boolean isEqualGetArgumentTypes = Objects.equals(Arrays.toString(random.getArgumentTypes()), "[]");

        assertTrue(isEqualGetResultType && isEqualGetArgumentTypes && isEqualGetName);

    }
}