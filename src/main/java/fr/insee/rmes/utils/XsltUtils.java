package fr.insee.rmes.utils;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class XsltUtils {

    private XsltUtils() {
		    throw new IllegalStateException("Utility class");
	}

	static byte[] createOdtFromXml(byte[] contentXml, byte[] zipBase) throws IOException {
		  var byteArrayOutputStream = new ByteArrayOutputStream(zipBase.length);
		  byteArrayOutputStream.writeBytes(zipBase);
		  var zip = new ZipOutputStream(byteArrayOutputStream);
		  zip.putNextEntry(new ZipEntry("content.xml"));
		  zip.write(contentXml);
		  zip.close();
		  return byteArrayOutputStream.toByteArray();
	}

	public static byte[] transformerInputStreamWithXsl(byte[] input,InputStream xslStyleSheet) throws TransformerException {
		return doTransform(new StreamSource(xslStyleSheet),new StreamSource(new ByteArrayInputStream(input)), Optional.empty() );
    }

	public static byte[] transformerStringWithXsl(String ddi, InputStream xslStyleSheet) throws TransformerException {
		  return doTransform(new StreamSource(xslStyleSheet), new StreamSource(new StringReader(ddi)), Optional.empty());
	}

	public static byte[] transformerInputStreamWithXslWithParameters(InputStream input, InputStream xslStyleSheet, Consumer<Transformer> parameterSetter) throws TransformerException {
		return doTransform(new StreamSource(xslStyleSheet),new StreamSource(input), Optional.of(parameterSetter ));
	}

	public static byte[] transformerFileWithXsl(File input, InputStream xslCheckReference) throws TransformerException {
        return doTransform(new StreamSource(xslCheckReference), new StreamSource(input), Optional.empty());
	}

	private static byte[] doTransform(Source stylesheetSource, Source inputSource, Optional<Consumer<Transformer>> parameterSetter) throws TransformerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result outputResult = new StreamResult(baos);
		Transformer transformer = XMLUtils.newTransformer(stylesheetSource);
		parameterSetter.ifPresent(p -> p.accept(transformer));
		transformer.transform(inputSource, outputResult);
		return baos.toByteArray();
	}
}
