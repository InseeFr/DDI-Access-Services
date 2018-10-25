package fr.insee.rmes.metadata.service.groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.service.fragmentInstance.FragmentInstanceService;
import fr.insee.rmes.search.model.DDIItemType;

@Service
public class GroupsServiceImpl implements GroupsService {

	@Autowired
	private FragmentInstanceService fragmentInstanceService;

	@Override
	public String getGroup(String idTopLevel) throws Exception {
		DDIItemType[] ddiItemTypes = { DDIItemType.GROUP, DDIItemType.SUB_GROUP };

		return fragmentInstanceService.getFragmentInstances(idTopLevel, ddiItemTypes);

	}
}
