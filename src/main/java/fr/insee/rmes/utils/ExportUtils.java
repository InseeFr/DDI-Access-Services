package fr.insee.rmes.utils;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.service.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class ExportUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtils.class);
    private static final String CAN_T_GENERATE_CODEBOOK = "Can't generate codebook";
    private static final String NULL_STREAM = "Stream is null";

    final int maxLength;

    public ExportUtils(@Value("${fr.insee.rmes.ddias.filenames.maxlength}") int maxLength) {
        this.maxLength = maxLength;
    }

    public ResponseEntity<Resource> exportAsODT(String fileName, Map<String, String> xmlContent, String xslFile, String xmlPattern, String zip, String objectType) throws RmesException {
        return exportAsFileByExtension(fileName, xmlContent, xslFile, xmlPattern, zip, objectType, FilesUtils.ODT_EXTENSION);
    }

    public ResponseEntity<Resource> exportAsODS(String fileName, Map<String, String> xmlContent, String xslFile, String xmlPattern, String zip, String objectType) throws RmesException {
        return exportAsFileByExtension(fileName, xmlContent, xslFile, xmlPattern, zip, objectType, FilesUtils.ODS_EXTENSION);
    }

    private ResponseEntity<Resource> exportAsFileByExtension(String fileName, Map<String, String> xmlContent, String xslFile, String xmlPattern, String zip, String objectType, String extension) throws RmesException {
        logger.debug("Begin To export {} as Response", objectType);
        fileName = fileName.replace(extension, "");

        InputStream input = exportAsInputStream(fileName, xmlContent, xslFile, xmlPattern, zip, objectType, extension);
        if (input == null)
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, CAN_T_GENERATE_CODEBOOK, NULL_STREAM);

        ByteArrayResource resource = null;
        try {
            resource = new ByteArrayResource(IOUtils.toByteArray(input));
            input.close();
        } catch (IOException e) {
            logger.error("Failed to getBytes of resource");
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "IOException");
        }
        logger.debug("End To export {} as Response", objectType);

        HttpHeaders responseHeaders = HttpUtils.generateHttpHeaders(fileName, extension, this.maxLength);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public InputStream exportAsInputStream(String fileName, Map<String, String> xmlContent, String xslFile, String xmlPattern, String zip, String objectType, String extension) throws RmesException {
        logger.debug("Begin To export {} as InputStream", objectType);

        File output = null;
        InputStream odtFileIS = null;
        InputStream xslFileIS = null;
        InputStream zipToCompleteIS = null;
        fileName = fileName.replace(extension, ""); //Remove extension if exists


        try {
            xslFileIS = getClass().getResourceAsStream(xslFile);
            odtFileIS = getClass().getResourceAsStream(xmlPattern);
            zipToCompleteIS = getClass().getResourceAsStream(zip);

            // prepare output
            output = File.createTempFile(Constants.OUTPUT, FilesUtils.getExtension(Constants.XML));
            output.deleteOnExit();

        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }

        try (OutputStream osOutputFile = FileUtils.openOutputStream(output);
             PrintStream printStream = new PrintStream(osOutputFile);) {

            Path tempDir = Files.createTempDirectory("forExport");
            Path finalPath = Paths.get(tempDir.toString(), fileName + extension);

            // transform
            XsltUtils.xsltTransform(xmlContent, odtFileIS, xslFileIS, printStream, tempDir);
            // create odt
            XsltUtils.createOdtFromXml(output, finalPath, zipToCompleteIS, tempDir);

            logger.debug("End To export {} as InputStream", objectType);

            return Files.newInputStream(finalPath);
        } catch (IOException | TransformerException e) {
            throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getClass().getSimpleName());
        } finally {
            try {
                if (odtFileIS != null)
                    odtFileIS.close();
                if (xslFileIS != null)
                    xslFileIS.close();
                if (zipToCompleteIS != null)
                    zipToCompleteIS.close();
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
            }
        }
    }




}
