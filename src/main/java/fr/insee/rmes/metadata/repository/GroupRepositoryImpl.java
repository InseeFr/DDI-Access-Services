package fr.insee.rmes.metadata.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.config.MetaDataRootContext;

@Service
public class GroupRepositoryImpl implements GroupRepository {


	@Autowired
	MetaDataRootContext metaDataRootContext;

	@Override
	public List<String> getRootIds() throws Exception {
		return metaDataRootContext.getSubGroupIds();
	}

	@Override
	public List<String> getRessourcePackageIds() {
		return metaDataRootContext.getRessourcePackageIds();
	}

}
