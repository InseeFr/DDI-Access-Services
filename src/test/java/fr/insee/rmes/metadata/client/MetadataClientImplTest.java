package fr.insee.rmes.metadata.client;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fr.insee.rmes.metadata.client.MetadataClientImpl;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.repository.MetadataRepository;

public class MetadataClientImplTest {
	
	@Autowired
	RestTemplate restTemplate;

	@Value("${fr.insee.rmes.api.remote.metadata.url}")
	String serviceUrl;

	@Value("${fr.insee.rmes.api.remote.metadata.agency}")
	String agency;

	@Value("${fr.insee.rmes.api.remote.metadata.key}")
	String apiKey;
	
	MetadataClientImpl metadataClient;
	
	@Before
	public void MetadataClientImplTestInit() {
		metadataClient = new MetadataClientImpl();
	}
	
	/**
	 * Test de la méthode de fake
	 * @throws Exception
	 */
	@Test
	public void getUnits() throws Exception {
		List<Unit> units = new ArrayList<Unit>();
		units.addAll(metadataClient.getUnits());
		assertNotNull(units);
		assertTrue(units.size()<4);
		assertTrue(units.size()>1);
	}
	
//	@Test
//	public void getChildrenRef() throws Exception {
//		//GIVEN
//		String id = "";
//		ColecticaItemRefList colecticaItemRefList;
//		//WHEN
//		when().get("%s/api/v1/set/%s/%s?api_key=%s", serviceUrl, agency, id, apiKey);
//		//THEN
//		
//	}
	
	
	

}
