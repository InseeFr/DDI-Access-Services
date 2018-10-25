package fr.insee.rmes.metadata.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.NodeList;

import fr.insee.rmes.metadata.model.ColecticaItem;

public class XpathProcessorImplTest {

	private RestTemplate restTemplate;

	@Test
	public void newXpathProcessorImplTest() {
		// GIVEN

		// WHEN
		XpathProcessorImpl xpathProcessorImpl = new XpathProcessorImpl();
		// THEN
		assertNotEquals(null, xpathProcessorImpl);

	}

	@Test
	public void queryListTest() throws Exception {
		// GIVEN
		XpathProcessorImpl xpathProcessorImpl = new XpathProcessorImpl();
		ColecticaItem colecticaItem;
		restTemplate = new RestTemplate();
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		// WHEN
		NodeList nodeList = xpathProcessorImpl.queryList(colecticaItem.item, "/Fragment[1]/*");
		// THEN
		assertNotEquals(null, nodeList);
		assertNotEquals(null, nodeList.item(0));
		assertNotEquals(null, nodeList.item(0).getTextContent());
		assertNotEquals("", nodeList.item(0).getTextContent());
	}

	@Test
	public void queryStringTest() throws Exception {
		// GIVEN
		XpathProcessorImpl xpathProcessorImpl = new XpathProcessorImpl();
		ColecticaItem colecticaItem;
		restTemplate = new RestTemplate();
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		// WHEN
		String result = xpathProcessorImpl.queryString(colecticaItem.item, "/Fragment[1]/*");
		// THEN
		assertNotEquals(null, result);
		assertNotEquals("", result);
		assertTrue(result.length() > 20);
	}
	
	@Test
	public void queryTextTest() throws Exception {
		// GIVEN
		XpathProcessorImpl xpathProcessorImpl = new XpathProcessorImpl();
		ColecticaItem colecticaItem;
		restTemplate = new RestTemplate();
		colecticaItem = restTemplate.getForObject("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843",
				ColecticaItem.class);
		// WHEN
		String result = xpathProcessorImpl.queryText(colecticaItem.item, "/Fragment[1]/*");
		// THEN
		assertNotEquals(null, result);
		assertNotEquals("", result);
		assertTrue(result.length() > 20);
	}

}
