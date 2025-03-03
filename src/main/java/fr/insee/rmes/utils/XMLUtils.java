package fr.insee.rmes.utils;

import fr.insee.rmes.tocolecticaapi.RandomUUIDExtensionFunction;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

@Slf4j
public class XMLUtils {

	public static final String DISALLOW_DOCTYPE_DECL = "http://javax.xml.transform.TransformerFactory/feature/disallow-doctype-decl";

	private static final TransformerFactory factory = initTransformerFactory();
	
	private XMLUtils() {
		    throw new IllegalStateException("Utility class");
	}


	public static TransformerFactory getTransformerFactory(){
		  return factory;
	}

	public static String toString(Document xml)
			throws TransformerFactoryConfigurationError, TransformerException {
		Transformer transformer = getTransformerFactory().newTransformer();
		Writer out = new StringWriter();
		transformer.transform(new DOMSource(xml), new StreamResult(out));
		return out.toString();
	}

	public static Node getChild(Node parent, String childName) {
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (childName.equals(child.getNodeName())) {
				return child;
			}
		}
		return null;
	}

	private static TransformerFactory initTransformerFactory() {
		TransformerFactoryImpl factory = new net.sf.saxon.TransformerFactoryImpl();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setFeature(DISALLOW_DOCTYPE_DECL, true);
        } catch (TransformerConfigurationException e) {
            log.info("unsuported feature for net.sf.saxon.TransformerFactoryImpl : {}", e.getMessage());
        }
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "file,jar:file");
		((Processor) factory.getConfiguration().getProcessor()).registerExtensionFunction(new RandomUUIDExtensionFunction());
		return factory;
	}

	public static Transformer newTransformer(Source xslt) throws TransformerConfigurationException {
		return factory.newTransformer(xslt);
	}

	public static Transformer newTransformer() throws TransformerConfigurationException {
		return factory.newTransformer();
	}
}
