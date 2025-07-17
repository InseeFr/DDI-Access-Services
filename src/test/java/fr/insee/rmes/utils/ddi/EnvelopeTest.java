package fr.insee.rmes.utils.ddi;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EnvelopeTest {

    @Test
    void shouldCreateEnvelop(){
        List<String> envelopeNameFiles = List.of(
                Envelope.CATEGORY_SCHEME.toString(),
                Envelope.CODE_LIST_SCHEME.toString(),
                Envelope.CATEGORY_SCHEME_FRAGMENT.toString(),
                Envelope.FRAGMENT.toString(),
                Envelope.INSTRUMENT.toString(),
                Envelope.ROOT.toString(),
                Envelope.FRAGMENT_INSTANCE.toString(),
                Envelope.DEFAULT.toString()
                );

        List<String> nameFiles = List.of(
                "ddi-envelope-categoryScheme.xml",
                "ddi-envelope-codeListScheme.xml",
                "ddi-envelope-fragmentDocument-categoryScheme.xml",
                "ddi-fragment-enveloppe.xml",
                "ddi-envelope-instrument.xml",
                "root.xml",
                "ddi-envelope-fragmentInstance.xml",
                "ddi-enveloppe.xml");

        assertEquals(nameFiles,envelopeNameFiles);

    }
}