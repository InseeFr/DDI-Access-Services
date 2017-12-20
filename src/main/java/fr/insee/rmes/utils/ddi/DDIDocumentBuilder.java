package fr.insee.rmes.utils.ddi;

import com.google.common.io.Resources;

import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.metadata.utils.XpathProcessorImpl;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class DDIDocumentBuilder {

	private Boolean envelope;
	private String nameEnvelope = "";
	private Node itemNode;
	private Node resourcePackageNode;
	private Document packagedDocument;

	public DDIDocumentBuilder() {
		this.envelope = true;
		try {
			packagedDocument = buildEnvelope();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DDIDocumentBuilder(Boolean envelope, Enum<Envelope> envelopeName) {
		this.nameEnvelope = envelopeName.toString();
		this.envelope = envelope;
		if (envelope) {
			try {

				packagedDocument = buildEnvelope();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				packagedDocument = buildWithoutEnvelope();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public DDIDocumentBuilder build() {
		if (envelope) {
			if (null != itemNode) {
				// packagedDocument.getDocumentElement().appendChild(itemNode);
				appendChildByParent("g:ResourcePackage", itemNode);
				refactor(itemNode, packagedDocument);
			}
			if (null != resourcePackageNode) {
				packagedDocument.getDocumentElement().appendChild(resourcePackageNode);
			}
		} else {
			if (null != itemNode) {
				packagedDocument.appendChild(itemNode);
			}
			if (null != resourcePackageNode) {
				packagedDocument.appendChild(resourcePackageNode);
			}
		}
		return this;
	}

	/**
	 * Build a DDIDocument with a specific node in addition to the main node.
	 * 
	 * @param node
	 *            : node to insert
	 * @param nameParent
	 *            : name of the parent ex:CodeListScheme to insert CodeLists
	 * @return DDIDocumentBuilder ddiDocument
	 */
	public DDIDocumentBuilder buildWithCustomNode(Node node, String nameParent) {
		if (envelope) {
			if (null != itemNode) {
				// packagedDocument.getDocumentElement().appendChild(itemNode);
				appendChildByParent("g:ResourcePackage", itemNode);
				importChildByParent(nameParent, node);
				refactor(itemNode, packagedDocument);
			}
			if (null != resourcePackageNode) {
				packagedDocument.getDocumentElement().appendChild(resourcePackageNode);
			}
		} else {
			if (null != itemNode) {
				packagedDocument.appendChild(itemNode);
			}
			if (null != resourcePackageNode) {
				packagedDocument.appendChild(resourcePackageNode);
			}
		}
		return this;
	}

	/**
	 * Build a DDIDocument with specific nodes in addition to the main node.
	 * 
	 * @param Map<Node,String>
	 *            nodesWithParentNames
	 * @return DDIDocumentBuilder ddiDocument
	 */
	public DDIDocumentBuilder buildWithCustomNodes(TreeMap<Integer, Map<Node, String>> nodesWithParentNames) {
		if (envelope) {
			if (null != itemNode) {
				// packagedDocument.getDocumentElement().appendChild(itemNode);
				appendChildByParent("g:ResourcePackage", itemNode);

				for (Integer key : nodesWithParentNames.keySet()) {
					Map<Node, String> map = nodesWithParentNames.get(key);
					for (Node node : map.keySet()) {
						importChildByParent(map.get(node), node);
					}
				}

			}
			refactor(itemNode, packagedDocument);
		}
		if (null != resourcePackageNode) {
			packagedDocument.getDocumentElement().appendChild(resourcePackageNode);
		} else {
			if (null != itemNode) {
				// packagedDocument.appendChild(itemNode);
			}
			if (null != resourcePackageNode) {
				packagedDocument.appendChild(resourcePackageNode);
			}
		}
		return this;
	}

	/**
	 * Method of refactoring to fix prefix of nodes
	 * 
	 * @param node
	 *            Node to fix (name and namespace)
	 * @param document
	 *            DDIDocument
	 */
	public void refactor(Node node, Document document) {

		switch (node.getNodeName()) {
		case "CodeList":
			changeTagName(document, "CodeList", "l:CodeList", "");
		case "Code":
			changeTagName(document, "Code", "l:Code", "");
		case "Category":
			changeTagName(document, "Category", "l:Category", "");
		case "CategoryScheme":
			changeTagName(document, "CategoryScheme", "l:CategoryScheme", "");
		}
	}

	/**
	 * Method of refactoring to fix prefix of nodes
	 * 
	 * @param List<nodes>
	 *            Nodes to fix (name and namespace)
	 * @param document
	 *            DDIDocument
	 */
	public void refactor(List<Node> nodes, Document document) {

		for (Node node : nodes) {
			refactor(node, document);
		}
	}

	/**
	 * Method which change the name of a node for a specific document
	 * 
	 * @param doc
	 *            : document concerned
	 * @param fromTag
	 *            : initial Tag ---> target
	 * @param toTag
	 *            : New tag
	 * @param namespace
	 *            : New nameSpace
	 */
	public void changeTagName(Document doc, String fromTag, String toTag, String namespace) {
		NodeList nodes = doc.getElementsByTagName(fromTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element elem = (Element) nodes.item(i);
				doc.renameNode(elem, namespace, toTag);
			}
		}
	}

	/**
	 * Method of adding the itemNode to the DDIDocument (appendChild)
	 * 
	 * @param parentName
	 *            : Name of the XML Parent
	 * @param childNode
	 *            : node to append
	 */
	public String appendChildByParent(String parentName, Node childNode) {
		NodeList nodeList = packagedDocument.getDocumentElement().getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(parentName)) {
				Node nodeListChild = node.getLastChild();
				Node finalNode = nodeListChild.getPreviousSibling();
				finalNode.appendChild(childNode);

			}

		}
		return this.toString();
	}

	public String importChildByParent(String parentName, Node childNode) {
		NodeList nodeList = packagedDocument.getDocumentElement().getChildNodes();
		Node node, nodeChild, clonedNode;
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);
			System.out.println(node.getNodeName());
			System.out.println(node.getParentNode().getNodeName());

			if (node.getNodeName().equals(parentName)) {
				clonedNode = childNode.cloneNode(true);
				node.appendChild(packagedDocument.adoptNode(clonedNode));

				refactor(clonedNode, packagedDocument);
			} else {
				NodeList nodeListRoot = node.getChildNodes();
				for (int j = 0; j < nodeListRoot.getLength(); j++) {
					nodeChild = nodeListRoot.item(j);
					System.out.println(nodeChild.getNodeName());
					System.out.println(nodeChild.getParentNode().getNodeName());
					if (nodeChild.getNodeName().equals(parentName)) {
						clonedNode = childNode.cloneNode(true);
						nodeChild.appendChild(packagedDocument.adoptNode(clonedNode));

						refactor(clonedNode, packagedDocument);
					}
				}
			}

		}
		return this.toString();
	}

	/**
	 * Method of adding a list of itemNode to the DDIDocument (appendChild)
	 * 
	 * @param parentName
	 *            : Name of the XML Parent
	 * @param childNode
	 *            : node to append
	 */
	public void appendChildrenByParent(String parentName, List<Node> childNodes) {
		NodeList nodeList = packagedDocument.getDocumentElement().getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(parentName)) {
				Node nodeListChild = node.getLastChild();
				Node finalNode = nodeListChild.getPreviousSibling();
				for (Node nodeChild : childNodes) {
					finalNode.appendChild(nodeChild);
				}

			}

		}
	}

	public DDIDocumentBuilder buildItemDocument(String rootId, Map<String, String> references) throws Exception {
		itemNode = buildNode(packagedDocument, rootId, references);
		return this;
	}

	public DDIDocumentBuilder buildResourcePackageDocument(String rootId, Map<String, String> references)
			throws Exception {
		resourcePackageNode = buildNode(packagedDocument, rootId, references);
		return this;
	}

	public Document getDocument() {
		return packagedDocument;
	}

	public String toString() {
		StringWriter stringWriter = new StringWriter();
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", packagedDocument,
					XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				node.getParentNode().removeChild(node);
			}
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			StreamResult streamResult = new StreamResult(stringWriter);
			transformer.transform(new DOMSource(packagedDocument), streamResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}

	private Node buildNode(Document document, String rootId, Map<String, String> references) throws Exception {
		Node node = getNode(references.get(rootId), document);
		walk(node, document, references);
		return node;
	}

	private Document buildEnvelope() throws Exception {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("transforms/templates/");
		strBuilder.append(this.nameEnvelope);
		URL url = Resources.getResource(strBuilder.toString());
		String fragment = FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8.name());
		return getDocument(fragment);
	}

	private Document buildWithoutEnvelope() throws Exception {
		return getDocument(null);

	}

	private void walk(Node root, Document document, Map<String, String> references) throws Exception {
		NodeList rootNodes = root.getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node node = rootNodes.item(i);
			if (node.getNodeName().contains("Reference")) {
				String fragment = references.get(getId(node));
				if (null != fragment) {
					Node child = getNode(fragment, document);
					root.appendChild(child);
					root.removeChild(node);
					walk(child, document, references);
				}
			}
		}
	}

	private Node getNode(String fragment, Document doc) throws Exception {
		Element node = getDocument(fragment).getDocumentElement();
		Node newNode = node.cloneNode(true);
		// Transfer ownership of the new node into the destination document
		doc.adoptNode(newNode);
		return newNode;
	}

	private static String getId(Node refNode) throws Exception {
		NodeList refChildren = refNode.getChildNodes();
		for (int i = 0; i < refChildren.getLength(); i++) {
			if (refChildren.item(i).getNodeName().equals("r:ID")) {
				System.out.println(refNode.getNodeName() + " -> " + refChildren.item(i).getTextContent());
				return refChildren.item(i).getTextContent();
			}
		}
		throw new Exception("No reference found in node");
	}

	private Document getDocument(String fragment) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		if (null == fragment || fragment.isEmpty()) {
			return builder.newDocument();
		}
		InputSource ddiSource = new InputSource(new StringReader(fragment));
		return builder.parse(ddiSource);
	}

}
