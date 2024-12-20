package fr.insee.rmes.utils;

import fr.insee.rmes.exceptions.RmesException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;

public class XsltUtils {

	private static final Logger logger = LoggerFactory.getLogger(XsltUtils.class);

	  private XsltUtils() {
		    throw new IllegalStateException("Utility class");
	}


	public static void xsltTransform(Map<String, String> xmlContent, InputStream odtFileIS, InputStream xslFileIS,
			PrintStream printStream, Path tempDir) throws TransformerException {
		// prepare transformer
		StreamSource xsrc = new StreamSource(xslFileIS);
		Transformer xsltTransformer = XMLUtils.newTransformer(xsrc);

		// Pass parameters in a file to the transformer
		xmlContent.forEach((paramName, xmlData) -> {
			try {
				addParameter(xsltTransformer, paramName, xmlData, tempDir);
			} catch (RmesException e) {
				logger.error(e.getMessageAndDetails());
			}
		});

		// transformation
		xsltTransformer.transform(new StreamSource(odtFileIS), new StreamResult(printStream));
	}


	private static void addParameter(Transformer xsltTransformer, String paramName, String paramData, Path tempDir) throws RmesException {
		CopyOption[] options = { StandardCopyOption.REPLACE_EXISTING };
		try {
			Path tempFile = Files.createTempFile(tempDir, paramName, FileExtension.XML_EXTENSION.extension());
			InputStream is = IOUtils.toInputStream(paramData, StandardCharsets.UTF_8);
			Files.copy(is, tempFile, options);

			// Convert to file URI
			String fileUri = tempFile.toUri().toString();
			logger.debug("Setting XSLT parameter '{}' to '{}'", paramName, fileUri);
			xsltTransformer.setParameter(paramName, fileUri);
		} catch (IOException e) {
			throw new RmesException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "IOException - Can't create temp files for XSLT Transformer");
		}
	}
	
	public static void createOdtFromXml(Path outputPath, Path finalPath, InputStream zipToCompleteIS, Path tempDir)
			throws IOException {
		Path contentPath = Paths.get(tempDir + "/content.xml");
		Files.copy(outputPath, contentPath, StandardCopyOption.REPLACE_EXISTING);
		Path zipPath = Paths.get(tempDir + "/export.zip");
		Files.copy(zipToCompleteIS, zipPath, StandardCopyOption.REPLACE_EXISTING);
		FilesUtils.addFileToZipFolder(contentPath, zipPath);
		Files.copy(zipPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
	}

	public static byte[] transformerInputStreamWithXsl(byte[] input,InputStream xslCheckReference) throws TransformerException {
		return doTransform(new StreamSource(xslCheckReference),new StreamSource(new ByteArrayInputStream(input)) );
    }

	public static byte[] transformerStringWithXsl(String ddi, InputStream xslRemoveNameSpaces) throws TransformerException {
		  return doTransform(new StreamSource(xslRemoveNameSpaces), new StreamSource(new StringReader(ddi)));
	}

	public static byte[] transformerFileWithXsl(File input, InputStream xslCheckReference) throws TransformerException {
        return doTransform(new StreamSource(xslCheckReference), new StreamSource(input));
	}

	private static byte[] doTransform(Source stylesheetSource, Source inputSource) throws TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result outputResult = new StreamResult(baos);
		XMLUtils.newTransformer(stylesheetSource).transform(inputSource, outputResult);
		return baos.toByteArray();
	}
}
