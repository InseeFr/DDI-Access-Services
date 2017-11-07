package fr.insee.rmes.metadata.repository;

import java.util.List;

public interface GroupRepository {

	List<String> getRootIds() throws Exception;

	List<String> getRessourcePackageIds();

}
