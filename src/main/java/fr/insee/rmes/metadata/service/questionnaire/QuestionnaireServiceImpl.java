package fr.insee.rmes.metadata.service.questionnaire;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fr.insee.rmes.metadata.repository.GroupRepository;
import fr.insee.rmes.metadata.repository.MetadataRepository;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceItem;
import fr.insee.rmes.metadata.utils.XpathProcessor;
import fr.insee.rmes.search.service.SearchService;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

	private final static Logger logger = LogManager.getLogger(QuestionnaireServiceImpl.class);

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

	@Override
	public String getQuestionnaire(String idDdiInstance, String idDdiInstrument) throws Exception {

		// Step 1 : Get the DdiItem and Check if it's a DDI instance (an
		// Exception throws if not)

		// Step 2 : Get the ColecticaItem group and the associated subGroup
		// References

		// Step 3 : Get the ColecticaItem sub-group and the associated studyUnit
		// References

		// Step 4 : Get the ColecticaItem studyUnit and all its children

		// Step 5 : Among all of the studyUnit's chidren, search the instrument
		// relating to its id as parameter

		// Step 6 : Insert the content of the instrument got to the enveloppe as
		// a child of StudyUnit's template.

		// Step 7 : Insert the other references of the studyUnit to the
		// enveloppe as children of RessourcePackageTemplate

		// Step 8 : return the filled out enveloppe as result

		return "";
	}

}
