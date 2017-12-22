package fr.insee.rmes.metadata.service.questionnaire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.search.service.SearchService;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

	@Autowired
	MetadataRepository metadataRepository;

	@Autowired
	MetadataServiceItem metadataServiceItem;

	@Autowired
	MetadataService metadataService;

	@Autowired
	SearchService searchService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	XpathProcessor xpathProcessor;

	public String getQuestionnaire(String id) throws Exception {
		DDIItem instrumentDDIItem = searchService.getDDIItemById(id);
		StringBuilder res = new StringBuilder();
		String studyUnitFragment = metadataServiceItem.getItem(instrumentDDIItem.getStudyUnitId()).item;
		String fragmentExp = "//*[local-name()='Fragment']/*[local-name()='StudyUnit']//text()";
		res.append(xpathProcessor.queryText(studyUnitFragment, fragmentExp));
		res.append(metadataService.getItemByType(id, DDIItemType.QUESTIONNAIRE));
		return res.toString();
	}

}
