package fr.insee.rmes.utils.ddi;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.io.Resources;

import fr.insee.rmes.metadata.utils.DocumentBuilderUtils;

public class DDIDocumentBuilder {

	private final static Logger logger = LogManager.getLogger(DDIDocumentBuilder.class);

	private Boolean envelope;
	private String nameEnvelope = Envelope.DEFAULT.toString();
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

	public void setEnvelope(Boolean envelope) {
		this.envelope = envelope;
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
				if (this.nameEnvelope.equals(Envelope.DEFAULT.toString())) {
					packagedDocument.getDocumentElement().appendChild(itemNode);
				} else {
					appendChildByParent("g:ResourcePackage", itemNode);
					refactor(itemNode, packagedDocument);
				}
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
				refactor(resourcePackageNode, packagedDocument);
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
				refactor(itemNode, packagedDocument);
			}
			for (Integer key : nodesWithParentNames.keySet()) {
				Map<Node, String> map = nodesWithParentNames.get(key);
				for (Node node : map.keySet()) {
					importChildByParent(map.get(node), node);
				}
			}
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
	 * Build a DDIDocument with specific nodes in addition to the
	 * ressourcePackageNode.
	 * 
	 * @param Map<Node,String>
	 *            nodesWithParentNames
	 * @return DDIDocumentBuilder ddiDocument
	 */
	public DDIDocumentBuilder buildRessourcePackageWithCustomNodes(
			TreeMap<Integer, Map<Node, String>> nodesWithParentNames) {
		if (envelope) {
			if (null != itemNode) {
				if (this.nameEnvelope.equals(Envelope.DEFAULT.toString())) {
					packagedDocument.getDocumentElement().appendChild(itemNode);
				}
				for (Integer key : nodesWithParentNames.keySet()) {
					Map<Node, String> map = nodesWithParentNames.get(key);
					for (Node node : map.keySet()) {
						if (key.equals(1)) {
							importChild(node);
						} else {
							importChildByParent(map.get(node), node);
						}
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
	 * @param childNode
	 *            : node to append
	 */
	public void appendChild(Node childNode) {
		packagedDocument.getDocumentElement().appendChild(childNode);
	}

	/**
	 * Method of adding the itemNode to the DDIDocument (appendChild)
	 * 
	 * @param parentName
	 *            : Name of the XML Parent
	 * @param childNode
	 *            : node to append
	 */
	public void appendChildByParent(String parentName, Node childNode) {
		NodeList nodeList = packagedDocument.getElementsByTagName(parentName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals(parentName)) {
				try {
					node.appendChild(childNode);
				} catch (Exception e) {
					node.appendChild(childNode);
				}

			}

		}

	}

	public void importChildByParent(String parentName, Node childNode) {
		NodeList nodeList = packagedDocument.getElementsByTagName(parentName);
		Node node = nodeList.item(0);
		if (node.getNodeName().equals(parentName)) {
			Node clonedNode = childNode.cloneNode(true);
			node.appendChild(packagedDocument.adoptNode(clonedNode));

			refactor(clonedNode, packagedDocument);
		}
	}

	public NodeList getElementByTagName(String name) {
		NodeList nodeList = packagedDocument.getElementsByTagName(name);
		return nodeList;
	}

	public void importChild(Node childNode) {
		Node node, clonedNode;
		node = packagedDocument.getLastChild();
		clonedNode = childNode.cloneNode(true);
		node.appendChild(packagedDocument.adoptNode(clonedNode));
		refactor(clonedNode, packagedDocument);
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



	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		try {
			encode();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			StreamResult streamResult = new StreamResult(stringWriter);
			transformer.transform(new DOMSource(packagedDocument), streamResult);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return stringWriter.toString();
	}

	private Node buildNode(Document document, String rootId, Map<String, String> references) throws Exception {
		Node node = DocumentBuilderUtils.getNode(references.get(rootId), document);
		walk(node, document, references);
		return node;
	}

	private Document buildEnvelope() throws Exception {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("transforms/templates/");
		strBuilder.append(this.nameEnvelope);
		URL url = Resources.getResource(strBuilder.toString());
		String fragment = FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8.name());
		return DocumentBuilderUtils.getDocument(fragment);
	}

	private Document buildWithoutEnvelope() throws Exception {
		return DocumentBuilderUtils.getDocument(null);

	}

	private void walk(Node root, Document document, Map<String, String> references) throws Exception {
		NodeList rootNodes = root.getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node node = rootNodes.item(i);
			if (node.getNodeName().contains("Reference")) {
				String fragment = references.get(getId(node));
				if (null != fragment) {
					Node child = DocumentBuilderUtils.getNode(fragment, document);
					root.appendChild(child);
					root.removeChild(node);
					walk(child, document, references);
				}
			} else {
				walk(node, document, references);
			}
		}
	}

	private static String getId(Node refNode) throws Exception {
		NodeList refChildren = refNode.getChildNodes();
		for (int i = 0; i < refChildren.getLength(); i++) {
			if (refChildren.item(i).getNodeName().equals("r:ID")) {
				logger.info(refNode.getNodeName() + " -> " + refChildren.item(i).getTextContent());
				return refChildren.item(i).getTextContent();
			}
		}
		throw new Exception("No reference found in node");
	}

	/*
	 * Read all document, remove unused breakline, normalize spaces and encode XML characters 
	 */
	private void encode() throws XPathExpressionException {
		Node node = packagedDocument.getFirstChild();
		encode(node);
	}

	private void encode(NodeList nodeListToEncode) {
		for (int i = 0; i < nodeListToEncode.getLength(); ++i) {
			encode(nodeListToEncode.item(i));
		}
	}

	private void encode(Node nodeToEncode) {
		if (nodeToEncode == null) {
			return;
		}
		if (nodeToEncode.hasChildNodes()) {
			encode(nodeToEncode.getChildNodes());
		}
		if (nodeToEncode.getNodeType() == Node.TEXT_NODE) {
			nodeToEncode.setTextContent(StringUtils.removeStart(nodeToEncode.getTextContent(), "\n"));
			nodeToEncode.setTextContent(StringUtils.removeEnd(nodeToEncode.getTextContent(), "\n"));
			nodeToEncode.setTextContent(StringUtils.normalizeSpace(nodeToEncode.getTextContent()));
		}

	}
}
