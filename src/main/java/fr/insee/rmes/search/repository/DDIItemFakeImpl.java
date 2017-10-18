package fr.insee.rmes.search.repository;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import fr.insee.rmes.config.DDIItemFakeImplCondition;
import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseItem;

@Repository
@Conditional(value = DDIItemFakeImplCondition.class)
public class DDIItemFakeImpl implements DDIItemRepository {

	@Override
	public IndexResponse save(String type, ResponseItem item) throws Exception {
		return null;
	}

	@Override
	public List<DDIItem> findByLabel(String label, String... types) throws Exception {
		// TODO implement a fake method
		return null;
	}

	public List<DDIItem> findByLabelInSubGroup(String label, String subgroupId, String... types) throws Exception {
		// TODO implement a fake method
		return null;
	}

	@Override
	public List<DDIItem> getSubGroups() throws Exception {
		List<DDIItem> subGroups = new ArrayList<DDIItem>();
		DDIItem ddiItem2 = new DDIItem("SG02", "Enquête auprès des salariés de l'État", null, "SubGroup");
		DDIItem ddiItem3 = new DDIItem("SG03", "Enquête Emploi en continu", null, "SubGroup");
		DDIItem ddiItem4 = new DDIItem("SG04", "Technologies de l'information et de la communication", null,
				"SubGroup");
		DDIItem ddiItem5 = new DDIItem("SG05", "Statistiques sur les ressources et conditions de vie", null,
				"SubGroup");
		DDIItem ddiItem6 = new DDIItem("SG06", "Enquête sectorielle annuelle", null, "SubGroup");
		DDIItem ddiItem7 = new DDIItem("SG07", "Enquête trimestrielle de conjoncture dans l'artisanat du bâtiment",
				null, "SubGroup");
		DDIItem ddiItem8 = new DDIItem("SG08", "Enquête trimestrielle de conjoncture dans la promotion immobilière",
				null, "SubGroup");
		DDIItem ddiItem9 = new DDIItem("SG09",
				"Enquête sur les investissements dans l'industrie pour protéger l'environnement ", null, "SubGroup");
		DDIItem ddiItem10 = new DDIItem("SG10", "Enquête Achats-Production", null, "SubGroup");
		DDIItem ddiItem11 = new DDIItem("SG11", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires",
				null, "SubGroup");
		subGroups.add(ddiItem2);
		subGroups.add(ddiItem3);
		subGroups.add(ddiItem4);
		subGroups.add(ddiItem5);
		subGroups.add(ddiItem6);
		subGroups.add(ddiItem7);
		subGroups.add(ddiItem8);
		subGroups.add(ddiItem9);
		subGroups.add(ddiItem10);
		subGroups.add(ddiItem11);
		return subGroups;
	}

	@Override
	public List<DDIItem> getStudyUnits(String subgGroupId) throws Exception {
		List<DDIItem> studyUnits = new ArrayList<DDIItem>();
		System.out.println(subgGroupId);
		if(subgGroupId.equals("SG02")){
			DDIItem ddiItem = new DDIItem("SU02", "Enquête auprès des salariés de l'État 2019", "SG02", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG03")){
			DDIItem ddiItem = new DDIItem("SU03", "Enquête Emploi en continu 2017", "SG03", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG04")){
			DDIItem ddiItem = new DDIItem("SU04", "Enquête sur les technologies de l'information et de la communication (auprès des ménages) 2019", "SG04", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG05")){
			DDIItem ddiItem = new DDIItem("SU05", "Statistiques sur les ressources et conditions de vie 2018", "SG05", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG06")){
			DDIItem ddiItem = new DDIItem("SU06", "Enquête sectorielle annuelle 2018", "SG06", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG07")){
			DDIItem ddiItem = new DDIItem("SU07", "Enquête trimestrielle de conjoncture dans l'artisanat du bâtiment 2018", "SG07", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG08")){
			DDIItem ddiItem = new DDIItem("SU08", "Enquête trimestrielle de conjoncture dans la promotion immobilière 2018", "SG08", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG09")){
			DDIItem ddiItem = new DDIItem("SU09", "Enquête sur les investissements dans l'industrie pour protéger l'environnement 2017", "SG09", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG10")){
			DDIItem ddiItem = new DDIItem("SU10", "Enquête Achats-Production 2018", "SG10", "StudyUnit");
			studyUnits.add(ddiItem);
		}
		if(subgGroupId.equals("SG11")){
			DDIItem ddiItem = new DDIItem("SU11", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires 2018", "SG11", "StudyUnit");
			studyUnits.add(ddiItem);
		}		
		return studyUnits;
	}

	@Override
	public List<DDIItem> getDataCollections(String studyUnitId) throws Exception {
		List<DDIItem> dataCollections = new ArrayList<DDIItem>();
		if(studyUnitId.equals("SU02")){
			DDIItem ddiItem = new DDIItem("DC0201", "Enquête auprès des salariés de l'État 2019", "SU02", "DataCollection");
			dataCollections.add(ddiItem);
		}
		if(studyUnitId.equals("SU03")){
			DDIItem ddiItem = new DDIItem("DC0301", "Enquête Emploi en continu 2017 - Vague 1", "SU03", "DataCollection");
			DDIItem ddiItem2 = new DDIItem("DC0302", "Enquête Emploi en continu 2017 - Vague 2", "SU03", "DataCollection");
			DDIItem ddiItem3 = new DDIItem("DC0303", "Enquête Emploi en continu 2017 - Vague 3", "SU03", "DataCollection");
			DDIItem ddiItem4 = new DDIItem("DC0304", "Enquête Emploi en continu 2017 - Vague 4", "SU03", "DataCollection");
			DDIItem ddiItem5 = new DDIItem("DC0305", "Enquête Emploi en continu 2017 - Vague 5", "SU03", "DataCollection");
			DDIItem ddiItem6 = new DDIItem("DC0306", "Enquête Emploi en continu 2017 - Vague 6", "SU03", "DataCollection");
			dataCollections.add(ddiItem);
			dataCollections.add(ddiItem2);
			dataCollections.add(ddiItem3);
			dataCollections.add(ddiItem4);
			dataCollections.add(ddiItem5);
			dataCollections.add(ddiItem6);
		}
		if(studyUnitId.equals("SU04")){
			DDIItem ddiItem = new DDIItem("DC0400", "Enquête sur les technologies de l'information et de la communication (auprès des ménages) 2019", "SU04", "DataCollection");
			dataCollections.add(ddiItem);
		}
		if(studyUnitId.equals("SU05")){
			DDIItem ddiItem = new DDIItem("DC0500", "Statistiques sur les ressources et conditions de vie 2018", "SU05", "DataCollection");
			dataCollections.add(ddiItem);
		}
		if(studyUnitId.equals("SU06")){
			DDIItem ddiItem = new DDIItem("DC0600", "Enquête sectorielle annuelle 2018", "SU06", "DataCollection");
			dataCollections.add(ddiItem);
		}
		if(studyUnitId.equals("SU07")){
			DDIItem ddiItem = new DDIItem("DC07101", "Enquête trimestrielle de conjoncture dans l'artisanat du bâtiment 1er trimestre 2018", "SU07", "DataCollection");
			DDIItem ddiItem2 = new DDIItem("DC07202", "Enquête trimestrielle de conjoncture dans l'artisanat du bâtiment 2ème trimestre 2018", "SU07", "DataCollection");
			DDIItem ddiItem3 = new DDIItem("DC07303", "Enquête trimestrielle de conjoncture dans l'artisanat du bâtiment 3ème trimestre 2018", "SU07", "DataCollection");
			DDIItem ddiItem4 = new DDIItem("DC07404", "Enquête trimestrielle de conjoncture dans l'artisanat du bâtiment 4ème trimestre 2018", "SU07", "DataCollection");
			dataCollections.add(ddiItem);
			dataCollections.add(ddiItem2);
			dataCollections.add(ddiItem3);
			dataCollections.add(ddiItem4);
		}
		if(studyUnitId.equals("SU08")){
			DDIItem ddiItem = new DDIItem("DC08101", "Enquête trimestrielle de conjoncture dans la promotion immobilière 1er trimestre 2018", "SU08", "DataCollection");
			DDIItem ddiItem2 = new DDIItem("DC08202", "Enquête trimestrielle de conjoncture dans la promotion immobilière 2ème trimestre 2018", "SU08", "DataCollection");
			DDIItem ddiItem3 = new DDIItem("DC08303", "Enquête trimestrielle de conjoncture dans la promotion immobilière 3ème trimestre 2018", "SU08", "DataCollection");
			DDIItem ddiItem4 = new DDIItem("DC08404", "Enquête trimestrielle de conjoncture dans la promotion immobilière 4ème trimestre 2018", "SU08", "DataCollection");
			dataCollections.add(ddiItem);
			dataCollections.add(ddiItem2);
			dataCollections.add(ddiItem3);
			dataCollections.add(ddiItem4);
		}
		if(studyUnitId.equals("SU09")){
			DDIItem ddiItem = new DDIItem("DC0900", "Enquête sur les investissements dans l'industrie pour protéger l'environnement 2017", "SU09", "DataCollection");
			dataCollections.add(ddiItem);
		}
		if(studyUnitId.equals("SU10")){
			DDIItem ddiItem = new DDIItem("DC1000", "Enquête Achats-Production 2018", "SU10", "DataCollection");
			dataCollections.add(ddiItem);
		}
		if(studyUnitId.equals("SU11")){
			DDIItem ddiItem = new DDIItem("DC1101", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires janvier 2018", "SU11", "DataCollection");
			DDIItem ddiItem2 = new DDIItem("DC1102", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires février 2018", "SU11", "DataCollection");
			DDIItem ddiItem3 = new DDIItem("DC1103", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires mars 2018", "SU11", "DataCollection");
			DDIItem ddiItem4 = new DDIItem("DC1104", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires avril 2018", "SU11", "DataCollection");
			DDIItem ddiItem5 = new DDIItem("DC1105", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires mai 2018", "SU11", "DataCollection");
			DDIItem ddiItem6 = new DDIItem("DC1106", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires juin 2018", "SU11", "DataCollection");
			DDIItem ddiItem7 = new DDIItem("DC1107", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires juillet 2018", "SU11", "DataCollection");
			DDIItem ddiItem8 = new DDIItem("DC1108", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires août 2018", "SU11", "DataCollection");
			DDIItem ddiItem9 = new DDIItem("DC1109", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires septembre 2018", "SU11", "DataCollection");
			DDIItem ddiItem10 = new DDIItem("DC1110", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires octobre 2018", "SU11", "DataCollection");
			DDIItem ddiItem11 = new DDIItem("DC1111", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires novembre 2018", "SU11", "DataCollection");
			DDIItem ddiItem12 = new DDIItem("DC1112", "Enquête mensuelle sur l'activité des grandes surfaces alimentaires décembre 2018", "SU11", "DataCollection");
			dataCollections.add(ddiItem);
			dataCollections.add(ddiItem2);
			dataCollections.add(ddiItem3);
			dataCollections.add(ddiItem4);
			dataCollections.add(ddiItem5);
			dataCollections.add(ddiItem6);
			dataCollections.add(ddiItem7);
			dataCollections.add(ddiItem8);
			dataCollections.add(ddiItem9);
			dataCollections.add(ddiItem10);
			dataCollections.add(ddiItem11);
			dataCollections.add(ddiItem12);
		}
		return dataCollections;
	}

	@Override
	public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
		DataCollectionContext dataCollectionContext = new DataCollectionContext();
		dataCollectionContext.setDataCollectionId(dataCollectionId);
		dataCollectionContext.setSerieId("SG"+dataCollectionId.substring(2, 4));
		dataCollectionContext.setOperationId("SU"+dataCollectionId.substring(2, 4));
		return dataCollectionContext;
	}

	@Override
	public DeleteResponse delete(String type, String id) throws Exception {
		return null;
	}
}
