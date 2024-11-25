package fr.insee.rmes.utils;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.tocolecticaapi.service.Constants;
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
		Transformer xsltTransformer = XMLUtils.getTransformerFactory().newTransformer(xsrc);

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
			String absolutePath = tempFile.toFile().getAbsolutePath();
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
		Path contentPath = Paths.get(tempDir.toString() + "/content.xml");
		Files.copy(outputPath, contentPath, StandardCopyOption.REPLACE_EXISTING);
		Path zipPath = Paths.get(tempDir.toString() + "/export.zip");
		Files.copy(zipToCompleteIS, zipPath, StandardCopyOption.REPLACE_EXISTING);
		FilesUtils.addFileToZipFolder(contentPath, zipPath);
		Files.copy(zipPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static String buildParams(Boolean lg1, Boolean lg2, Boolean includeEmptyFields, String targetType) {
		String includeEmptyFieldsString = (Boolean.TRUE.equals(includeEmptyFields) ? "true" : "false");
		String parametersXML = "";

		parametersXML = parametersXML.concat(Constants.XML_OPEN_PARAMETERS_TAG);

		parametersXML = parametersXML.concat(Constants.XML_OPEN_LANGUAGES_TAG);
		if (Boolean.TRUE.equals(lg1))
			parametersXML = parametersXML.concat("<language id=\"Fr\">1</language>");
		if (Boolean.TRUE.equals(lg2))
			parametersXML = parametersXML.concat("<language id=\"En\">2</language>");
		parametersXML = parametersXML.concat(Constants.XML_END_LANGUAGES_TAG);

		parametersXML = parametersXML.concat(Constants.XML_OPEN_INCLUDE_EMPTY_FIELDS_TAG);
		parametersXML = parametersXML.concat(includeEmptyFieldsString);
		parametersXML = parametersXML.concat(Constants.XML_END_INCLUDE_EMPTY_FIELDS_TAG);

		parametersXML = parametersXML.concat(Constants.XML_OPEN_TARGET_TYPE_TAG);
		parametersXML = parametersXML.concat(targetType);
		parametersXML = parametersXML.concat(Constants.XML_END_TARGET_TYPE_TAG);

		parametersXML = parametersXML.concat(Constants.XML_END_PARAMETERS_TAG);
		return XMLUtils.encodeXml(parametersXML);
	}

    public static void transformerInputStreamWithXsl(InputStream input,InputStream xslCheckReference, File output) throws Exception {
        Source stylesheetSource = new StreamSource(xslCheckReference);
        Transformer transformer = XMLUtils.getTransformerFactory().newTransformer(stylesheetSource);
        Source inputSource = new StreamSource(input);
        Result outputResult = new StreamResult(output);
        transformer.transform(inputSource, outputResult);
    }

	public static void transformerStringWithXsl(String ddi,InputStream xslRemoveNameSpaces, File output) throws Exception{
		Source stylesheetSource = new StreamSource(xslRemoveNameSpaces);
		Transformer transformer = XMLUtils.getTransformerFactory().newTransformer(stylesheetSource);
		Source inputSource = new StreamSource(new StringReader(ddi));
		Result outputResult = new StreamResult(output);
		transformer.transform(inputSource, outputResult);
	}

	public static void transformerFileWithXsl(File input, InputStream xslCheckReference, File output) throws Exception {
		Source stylesheetSource = new StreamSource(xslCheckReference);
		Transformer transformer = XMLUtils.getTransformerFactory().newTransformer(stylesheetSource);
		Source inputSource = new StreamSource(input);
		Result outputResult = new StreamResult(output);
		transformer.transform(inputSource, outputResult);
	}
}
