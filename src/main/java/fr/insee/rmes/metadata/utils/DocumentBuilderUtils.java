package fr.insee.rmes.metadata.utils;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;

@Service
public class DocumentBuilderUtils {

	public static Document getDocument(String fragment) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
