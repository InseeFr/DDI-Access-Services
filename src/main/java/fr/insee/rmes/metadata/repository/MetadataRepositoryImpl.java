package fr.insee.rmes.metadata.repository;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.insee.rmes.metadata.client.MetadataClient;
import fr.insee.rmes.metadata.model.ColecticaItem;
import fr.insee.rmes.metadata.model.ColecticaItemPostRef;
import fr.insee.rmes.metadata.model.ColecticaItemPostRefList;
import fr.insee.rmes.metadata.model.ColecticaItemRefList;
import fr.insee.rmes.metadata.model.Relationship;
import fr.insee.rmes.metadata.model.ObjectColecticaPost;
import fr.insee.rmes.metadata.model.Unit;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

	private final static Logger logger = LogManager.getLogger(MetadataRepositoryImpl.class);

	@Autowired
	MetadataClient metadataClient;

	@Value("${fr.insee.rmes.api.remote.metadata.agency}")
	public String agencyId;

	@Value("${fr.insee.rmes.api.remote.metadata.user}")
	private String versionResponsability;

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
	public String postItems(ColecticaItemPostRefList refs) throws Exception {
		return metadataClient.postItems(refs);

	}

	@Override
	public String postItem(ColecticaItemPostRef ref) throws Exception {
		return metadataClient.postItem(ref);
	}

	@Override
	public Integer getLastestVersionItem(String id) throws Exception {
		return metadataClient.getLastestVersionItem(id);

	}

	@Override
	public Relationship[] getRelationship(ObjectColecticaPost relationshipPost) throws Exception {
		return metadataClient.getRelationship(relationshipPost);
	}

	@Override
	public Relationship[] getRelationshipChildren(ObjectColecticaPost relationshipPost) {
		return metadataClient.getRelationshipChildren(relationshipPost);
	}
}
