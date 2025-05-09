package fr.insee.rmes.utils;

import fr.insee.rmes.exceptions.RmesException;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExportUtilsTest {

    @Test
    void shouldThrowRmesExceptionWhenExportAsODT() throws RmesException {
        Map<String, byte[]> xmlParameters = new HashMap<>();
        xmlParameters.put("sequence", HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"));
        ExportUtils exportUtils = new ExportUtils();
        RmesException exception = assertThrows(RmesException.class, () -> exportUtils.exportAsODT(xmlParameters,"xslFile","xmlPattern","zip"));
        assertEquals("{\"details\":\"TransformerConfigurationException\",\"message\":\"net.sf.saxon.s9api.SaxonApiException: I\\/O error reported by XML parser processing null\"}",exception.getDetails());
    }

    @Test
    void shouldThrowRmesExceptionWhenExportAsODS()  {
        Map<String, byte[]> xmlParameters = new HashMap<>();
        xmlParameters.put("sequence", HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"));
        ExportUtils exportUtils = new ExportUtils();
        RmesException exception = assertThrows(RmesException.class, () -> exportUtils.exportAsODS(xmlParameters,"xslFile","xmlPattern","zip"));
        assertEquals("{\"details\":\"TransformerConfigurationException\",\"message\":\"net.sf.saxon.s9api.SaxonApiException: I\\/O error reported by XML parser processing null\"}",exception.getDetails());
    }

}