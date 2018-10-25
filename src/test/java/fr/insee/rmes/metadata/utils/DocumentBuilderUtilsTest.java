package fr.insee.rmes.metadata.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;

public class DocumentBuilderUtilsTest {

	private RestTemplate restTemplate;
	private DDIDocumentBuilder ddiDocument;

	@Test
	public void getDocumentTest() throws Exception {
		// GIVEN
		restTemplate = new RestTemplate();
		ColecticaItem colecticaItem;
		Document document;
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		// WHEN
		document = DocumentBuilderUtils.getDocument(colecticaItem.item);
		// THEN
		assertNotEquals(null, colecticaItem.item);
		assertNotEquals("", colecticaItem.item);
		assertNotEquals(null, document);

	}

	@Test
	public void getDocumentEmptyTest() throws Exception {
		// GIVEN
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		restTemplate = new RestTemplate();
		ColecticaItem colecticaItem;
		Document document;
		DocumentBuilder builder = factory.newDocumentBuilder();
		// WHEN
		document = DocumentBuilderUtils.getDocument("");
		// THEN
		assertNotEquals(null, document);

	}

	@Test
	public void getDocumentNullTest() throws Exception {
		// GIVEN
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		restTemplate = new RestTemplate();
		ColecticaItem colecticaItem;
		Document document;
		DocumentBuilder builder = factory.newDocumentBuilder();
		// WHEN
		document = DocumentBuilderUtils.getDocument(null);
		// THEN
		assertNotEquals(null, document);

	}

	@Test
	public void newDocumentTest() throws Exception {
		// GIVEN

		// WHEN
		DocumentBuilderUtils documentBuilderUtils = new DocumentBuilderUtils();
		// THEN
		assertNotEquals(null, documentBuilderUtils);
	}

	@Test
	public void getNodeTest() throws Exception {
		// GIVEN
		restTemplate = new RestTemplate();
		ColecticaItem colecticaItem;
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		Node node;
		Document document;

		document = DocumentBuilderUtils.getDocument(colecticaItem.item);

		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		// WHEN
		node = DocumentBuilderUtils.getNode(colecticaItem.item, document);
		// THEN
		assertNotEquals(null, colecticaItem.item);
		assertNotEquals("", colecticaItem.item);
		assertNotEquals(0, node.getChildNodes().getLength());
		assertNotEquals(null, node.getChildNodes().getLength());
	}

	@Test
	public void getNodeDDIDocumentTest() throws Exception {
		// GIVEN
		ddiDocument = new DDIDocumentBuilder();
		restTemplate = new RestTemplate();
		ColecticaItem colecticaItem;
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		Node node;
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		// WHEN
		node = DocumentBuilderUtils.getNode(colecticaItem.item, ddiDocument);

		// THEN
		assertNotEquals(null, colecticaItem.item);
		assertNotEquals("", colecticaItem.item);
		assertNotEquals(0, node.getChildNodes().getLength());
		assertNotEquals(null, node.getChildNodes().getLength());
	}

}
