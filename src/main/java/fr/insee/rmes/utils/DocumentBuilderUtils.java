package fr.insee.rmes.utils;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Optional;

@Service
public class DocumentBuilderUtils {

	private DocumentBuilderUtils(){}


	public static Document getDocument(String fragment) throws ParserConfigurationException, IOException, SAXException {
		return getDocument(new ByteArrayInputStream(fragment.getBytes()));
	}

	public static Document getDocument(InputStream fragment) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilder builder = DocumentBuilders.createSaferDocumentBuilder(Optional.empty());
		if (null == fragment) {
			return builder.newDocument();
		}
		InputSource ddiSource = new InputSource(fragment);
		return builder.parse(ddiSource);
	}

}
