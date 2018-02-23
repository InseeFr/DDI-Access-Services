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
	public String getQuestionnaire(String idDDIInstance, String idDDIInstrument) throws Exception {

		// Step 1 : Get the DDIInstance, the DDIInstrument and Check type (an
		// Exception throws if not)
		//////////////////////////////////////////////
		// While Instrument not found
		// Step 2 : Get all the group references

		// Step 3 : foreach group in groups --> Search subGroups and store the
		// currentGroup as ColecticaItem

		// Step 4 : foreach subGroup in subGroups in currentGroup --> Search
		// StudyUnits and store the currentSubGroup as ColecticaItem

		// Step 5 : foreach StudyUnit in currentStudyUnit in currentSubGroup in
		// currentGroup -->
		// Search Instruments and store the currentStudyUnit as ColecticaItem

		// Step 6 : foreach Instrument in instruments in currentStudyUnit in
		// currentSubGroup in currentGroup --> Search idDDIInstrument

		//// Step 7 : if the idDDIInstrument : leave the Loop and get the
		//// children list of this instrument
		/////////////////////////////////////////////////////////////////

		// Step 8 : Among all of the studyUnit's chidren, search the instrument
		// relating to its id as parameter

		// Step 9 : Insert the content of the instrument got to the enveloppe as
		// a child of StudyUnit's template.

		// Step 10 : Insert the other references of the studyUnit to the
		// enveloppe as children of RessourcePackageTemplate

		// Step 11 : return the filled out enveloppe as result

		return "";
	}

}
