package fr.insee.rmes.metadata.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.rmes.metadata.client.MetadataClientImpl;
import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.repository.MetadataRepository;

public class MetadataClientImplTest {
	
	
	
	MetadataClientImpl metadataClient;
	
	@Before
	public void MetadataClientImplTestInit() {
		metadataClient = new MetadataClientImpl();
	}
	
	
	@Test
	public void getUnits() throws Exception {
		List<Unit> units = new ArrayList<Unit>();
		units.addAll(metadataClient.getUnits());
		assertNotNull(units);
		assertTrue(units.size()<4);
		assertTrue(units.size()>1);
	}
	

}
