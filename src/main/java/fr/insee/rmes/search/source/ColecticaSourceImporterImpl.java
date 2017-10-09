package fr.insee.rmes.search.source;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.search.service.SearchService;

@Service
public class ColecticaSourceImporterImpl implements ColecticaSourceImporter {

	private final static Logger logger = LogManager.getLogger(ColecticaSourceImporter.class);

	@Autowired
	MetadataService metadataService;

	@Autowired
	SearchService searchService;

	List<String> rootIds;

	@PostConstruct
	public void setUp() throws Exception {
		try {
			rootIds = metadataService.getGroupIds();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void source() throws Exception {
		for (String id : rootIds) {
			logger.debug("Getting data from colectica API for root id " + id);
			ResponseItem r = metadataService.getDDIRoot(id);
			logger.debug("Root contains " + r.getChildren().size() + " groups");
			for (ResponseItem g : r.getChildren()) {
				System.out.println(g);
				searchService.save("group", g);
				saveSeries(g.getChildren());
			}
		}
	}

	public void saveSeries(List<ResponseItem> subGroups) throws Exception {
		for (ResponseItem s : subGroups) {
			searchService.save("sub-group", s);
			saveOperations(s.getChildren());
		}
	}

	public void saveOperations(List<ResponseItem> studyUnits) throws Exception {
		for (ResponseItem o : studyUnits) {
			searchService.save("study-unit", o);
			saveDataCollections(o.getChildren());
		}
	}

	public void saveDataCollections(List<ResponseItem> dataCollections) throws Exception {
		for (ResponseItem dc : dataCollections) {
			searchService.save("data-collection", dc);
			saveInstrumentSchemes(dc.getChildren());
		}
	}

	public void saveInstrumentSchemes(List<ResponseItem> instrumentSchemes) throws Exception {
		for (ResponseItem i : instrumentSchemes) {
			searchService.save("instrument-scheme", i);
			saveQuestionnaires(i.getChildren());
		}
	}

	public void saveQuestionnaires(List<ResponseItem> instruments) throws Exception {
		for (ResponseItem q : instruments) {
			searchService.save("instrument", q);
		}
	}

}
