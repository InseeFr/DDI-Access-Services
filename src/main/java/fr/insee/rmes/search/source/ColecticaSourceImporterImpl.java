package fr.insee.rmes.search.source;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.search.service.SearchService;

@Service
public class ColecticaSourceImporterImpl implements ColecticaSourceImporter {

	private final static Logger logger = LogManager.getLogger(ColecticaSourceImporter.class);

	@Autowired
	MetadataService metadataService;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	SearchService searchService;

	List<String> rootIds;
	List<String> ressourcePackageIds;

	@PostConstruct
	public void setUp() throws Exception {
		try {
			rootIds = metadataService.getGroupIds();
			ressourcePackageIds = metadataService.getRessourcePackageIds();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void source() throws Exception {

		// searchService.deleteAll();
		for (String id : rootIds) {
			if (StringUtils.isNotEmpty(id)) {
				logger.debug("Getting data from colectica API for root id " + id);
				ResponseItem r = metadataServiceItem.getDDIRoot(id);
				logger.debug("Root contains " + r.getChildren().size() + " groups");
				for (ResponseItem g : r.getChildren()) {
					searchService.save("group", g);
					saveSeries(g.getChildren());
				}

				List<ResponseItem> clsList = metadataServiceItem.getDDICodeListSchemeFromGroupRoot(id);
				logger.debug("Root contains " + clsList.size() + " CodeListScheme");
				for (ResponseItem cls : clsList) {
					searchService.save("code-list-scheme", cls);
					saveCodeList(cls.getChildren());
				}
			}
		}

		for (String id : ressourcePackageIds) {
			if (id != null && !id.equals("")) {
				logger.debug("Getting data from colectica API for ressource package  id " + id);
				List<ResponseItem> clsList = metadataServiceItem.getDDICodeListSchemeFromResourcePackage(id);
				logger.debug("RessourcePackage contains " + clsList.size() + " CodeListScheme");
				for (ResponseItem cls : clsList) {
					searchService.save("code-list-scheme", cls);
					saveCodeList(cls.getChildren());
				}
			}
		}

	}

	private void saveCodeList(List<ResponseItem> clsList) throws Exception {
		for (ResponseItem cls : clsList) {
			searchService.save("code-list", cls);
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
