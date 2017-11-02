package fr.insee.rmes.metadata.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.client.MetadataClient;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRef;
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

	@Override
	public Map<ColecticaItemPostRef, String> postNewItems(ColecticaItemPostRefList refs) throws Exception {
		Map<ColecticaItemPostRef, String> results = new HashMap<ColecticaItemPostRef, String>();

		// Valuing new UUID
		for (ColecticaItemPostRef colecticaItemPostRef : refs.getItems()) {
			colecticaItemPostRef.identifier = UUID.randomUUID().toString();
		}
		String res = metadataClient.postItems(refs);

		for (ColecticaItemPostRef colecticaItemPostRef : refs.getItems()) {
			results.put(colecticaItemPostRef, res);
		}
		return results;
	}

	@Override
	public Map<ColecticaItemPostRef, String> postNewItem(ColecticaItemPostRef ref) throws Exception {
		Map<ColecticaItemPostRef, String> result = new HashMap<ColecticaItemPostRef, String>();
		ref.identifier = UUID.randomUUID().toString();
		metadataClient.postItem(ref);
		result.put(ref, metadataClient.postItem(ref));
		return result;
	}

	@Override
	public Map<ColecticaItemPostRef, String> postUpdateItems(ColecticaItemPostRefList refs) throws Exception {
		Map<ColecticaItemPostRef, String> results = new HashMap<ColecticaItemPostRef, String>();
		int version = 0;
		String result;
		for (ColecticaItemPostRef colecticaItemPostRef : refs.getItems()) {
			version = Integer.valueOf(colecticaItemPostRef.version);
				version++;
				colecticaItemPostRef.version = String.valueOf(version);
				result = metadataClient.postItems(refs);
				results.put(colecticaItemPostRef, result);
				
		}
		return results;

	}
}
