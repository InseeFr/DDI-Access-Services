package fr.insee.rmes.metadata.fragmentInstance;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import fr.insee.rmes.metadata.client.MetadataClient;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.service.fragmentInstance.FragmentInstanceServiceImpl;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.utils.FileUtils;
import fr.insee.rmes.utils.ddi.DDIDocumentBuilder;
import fr.insee.rmes.webservice.rest.RMeSException;

@RunWith(MockitoJUnitRunner.class)
public class FragmentInstanceServiceTest {

	private static final String idTopLevel = "41c2eca1-8b18-4932-809c-9330ca00fef3";

	private String groupString;

	private String subGroupString;

	private DDIItemType[] types = { DDIItemType.GROUP, DDIItemType.SUB_GROUP };
	private FragmentInstanceServiceImpl fragmentInstanceServiceImpl;

	@Autowired
	@Mock
	private MetadataServiceItem metadataServiceItem;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		fragmentInstanceServiceImpl = new FragmentInstanceServiceImpl();
		metadataServiceItem = mock(MetadataServiceItem.class);
		fragmentInstanceServiceImpl.setMetadataServiceItem(metadataServiceItem);
		this.subGroupString = FileUtils.getResourceAsString("/groups/subGroup-fragment.xml");
		this.groupString = FileUtils.getResourceAsString("/groups/group-fragment.xml");
	}

	////////////////// STEP1///////////////////////////
	@Test(expected = Exception.class)
	public void testStep1CaseNull() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		when(metadataServiceItem.getItem(idTopLevel)).thenReturn(colecticaItem);
		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, null);
		// THEN
		assertTrue(res.length() > 0);
	}

	@Test
	public void testStep1CaseGroup() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		colecticaItem.setItemType("4bd6eef6-99df-40e6-9b11-5b8f64e5cb23");
		colecticaItem.setItem(groupString);

		when(metadataServiceItem.getItemByType(idTopLevel, types[0])).thenReturn(colecticaItem);
		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, types);
		// THEN
		assertTrue(res.length() > 0);
	}

	@Test
	public void testStep1CaseSubGroup() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		colecticaItem.setItemType("2d57296d-095c-485a-b970-8c63c215c1d0");
		colecticaItem.setItem(subGroupString);

		when(metadataServiceItem.getItemByType(idTopLevel, types[1])).thenReturn(colecticaItem);

		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, types);
		// THEN
		assertTrue(res.length() > 0);
	}

	////////////////// STEP2///////////////////////////
	@Test
	public void testStep2CaseNull() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		colecticaItem.setItemType("2d57296d-095c-485a-b970-8c63c215c1d0");
		colecticaItem.setItem(subGroupString);
		DDIDocumentBuilder ddiB = new DDIDocumentBuilder();

		when(metadataServiceItem.getItem(idTopLevel)).thenReturn(colecticaItem);
		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, null);
		// THEN
		assertTrue(ddiB != null);
		assertTrue(res.length() > 0);
	}

	@Test
	public void testStep2CaseNotNull() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		colecticaItem.setItemType("2d57296d-095c-485a-b970-8c63c215c1d0");
		colecticaItem.setItem(subGroupString);
		DDIDocumentBuilder ddiB = new DDIDocumentBuilder();

		when(metadataServiceItem.getItemByType(idTopLevel, types[1])).thenReturn(colecticaItem);
		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, types);
		// THEN
		assertTrue(ddiB != null);
		assertTrue(res.length() > 0);
	}

	////////////////// STEP4///////////////////////////
	@Test
	public void testStep4CaseNull() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		colecticaItem.setItemType("2d57296d-095c-485a-b970-8c63c215c1d0");
		colecticaItem.setItem(subGroupString);
		DDIDocumentBuilder ddiB = new DDIDocumentBuilder();
		ColecticaItemRef ref = new ColecticaItemRef();
		ref.agencyId = colecticaItem.agencyId;
		ref.identifier = colecticaItem.identifier;
		ColecticaItemRefList refs = new ColecticaItemRefList();
		refs.identifiers = new ArrayList<ColecticaItemRef>();
		refs.identifiers.add(ref);
		List<ColecticaItem> items = new ArrayList<ColecticaItem>();
		items.add(colecticaItem);
		when(metadataServiceItem.getChildrenRef(idTopLevel)).thenReturn(refs);
		when(metadataServiceItem.getItems(refs)).thenReturn(items);
		when(metadataServiceItem.getItem(idTopLevel)).thenReturn(colecticaItem);
		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, null);
		// THEN
		assertTrue(ddiB != null);
		assertTrue(res.length() > 0);
	}

	@Test
	public void testStep4CaseNotNull() throws Exception {
		// GIVEN
		ColecticaItem colecticaItem = new ColecticaItem();
		colecticaItem.agencyId = "fr.insee";
		colecticaItem.identifier = idTopLevel;
		colecticaItem.setItemType("2d57296d-095c-485a-b970-8c63c215c1d0");
		colecticaItem.setItem(subGroupString);
		DDIDocumentBuilder ddiB = new DDIDocumentBuilder();
		ColecticaItemRef ref = new ColecticaItemRef();
		ref.agencyId = colecticaItem.agencyId;
		ref.identifier = colecticaItem.identifier;
		ColecticaItemRefList refs = new ColecticaItemRefList();
		refs.identifiers = new ArrayList<ColecticaItemRef>();
		refs.identifiers.add(ref);
		List<ColecticaItem> items = new ArrayList<ColecticaItem>();
		items.add(colecticaItem);
		when(metadataServiceItem.getChildrenRef(idTopLevel)).thenReturn(refs);
		when(metadataServiceItem.getItems(refs)).thenReturn(items);
		when(metadataServiceItem.getItemByType(idTopLevel, types[1])).thenReturn(colecticaItem);
		// WHEN
		String res = fragmentInstanceServiceImpl.getFragmentInstances(idTopLevel, types);
		// THEN
		assertTrue(ddiB != null);
		assertTrue(res.length() > 0);
	}

}
