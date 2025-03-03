package fr.insee.rmes.utils;

import fr.insee.rmes.exceptions.RmesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
public class ExportUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtils.class);

    public static final int FILENAME_MAX_LENGTH = 50;

    public byte[] exportAsODT(Map<String, byte[]> xmlContent, String xslFile, String xmlPattern, String zip) throws RmesException {
        return exportAsBytes(xmlContent, xslFile, xmlPattern, zip);
    }

    public byte[] exportAsODS(Map<String, byte[]> xmlContent, String xslFile, String xmlPattern, String zip) throws RmesException {
        return exportAsBytes(xmlContent, xslFile, xmlPattern, zip);
    }

    private byte[] exportAsBytes(Map<String, byte[]> xmlParameters, String xslFile, String xmlPattern, String zip) throws RmesException {
        try (
                InputStream xslFileIS = getClass().getResourceAsStream(xslFile);
                InputStream odtFileIS = getClass().getResourceAsStream(xmlPattern);
                InputStream zipToCompleteIS = getClass().getResourceAsStream(zip)) {

            Path tempDir = Files.createTempDirectory("forExport");
            var contentXml = XsltUtils.transformerInputStreamWithXslWithParameters(odtFileIS, xslFileIS, transformer -> addParameters(xmlParameters, tempDir, transformer));
            // create odt
            return XsltUtils.createOdtFromXml(contentXml, zipToCompleteIS.readAllBytes());
        } catch (IOException | TransformerException e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getClass().getSimpleName());
        }
    }

    private static void addParameters(Map<String, byte[]> xmlContent, Path tempDir, Transformer xsltTransformer) {
        xmlContent.forEach((paramName, xmlData) -> addParameter(xsltTransformer, paramName, xmlData, tempDir));
    }

    private static void addParameter(Transformer xsltTransformer, String paramName, byte[] paramData, Path tempDir) {

        try {
            Path tempFile = Files.createTempFile(tempDir, paramName, FileExtension.XML_EXTENSION.extension());
            Files.write(tempFile, paramData);
            tempFile.toFile().deleteOnExit();

            // Convert to file URI
            String fileUri = tempFile.toUri().toString();
            logger.debug("Setting XSLT parameter '{}' to '{}'", paramName, fileUri);
            xsltTransformer.setParameter(paramName, fileUri);
        } catch (IOException e) {
            logger.error("Can't create temp files for XSLT Transformer", e);
            throw new UncheckedIOException(e);
        }
    }


}
