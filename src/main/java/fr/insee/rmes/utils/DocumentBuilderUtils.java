package fr.insee.rmes.utils;

import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class DocumentBuilderUtils {


	public static Document getDocument(String fragment) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Désactiver l'accès aux entités externes pour des raisons de sécurité (prévention des attaques XXE)
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

		DocumentBuilder builder = factory.newDocumentBuilder();

		if (null == fragment || fragment.isEmpty()) {
			return builder.newDocument();
		}

		InputSource ddiSource = new InputSource(new StringReader(fragment));
		return builder.parse(ddiSource);
	}
	
	public static Node getNode(String fragment, Document doc) throws Exception {
		Element node = DocumentBuilderUtils.getDocument(fragment).getDocumentElement();
		Node newNode = node.cloneNode(true);
		// Transfer ownership of the new node into the destination document
		doc.adoptNode(newNode);
		return newNode;
	}
	
	public static Node getNode(String fragment, DDIDocumentBuilder doc) throws Exception {
		return getNode(fragment, doc.getDocument());
	}
}
