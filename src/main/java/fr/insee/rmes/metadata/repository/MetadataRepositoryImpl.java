package fr.insee.rmes.metadata.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.client.MetadataClient;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Unit;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

	@Autowired
	MetadataClient metadataClient;

	@Override
	public ColecticaItem findById(String id) throws Exception {
		return metadataClient.getItem(id);
	}

	@Override
	public ColecticaItemRefList getChildrenRef(String id) throws Exception {
		return metadataClient.getChildrenRef(id);
	}

	@Override
	public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
		return metadataClient.getItems(refs);
	}

	@Override
	public List<Unit> getUnits() throws Exception {
		return metadataClient.getUnits();
	}
}
