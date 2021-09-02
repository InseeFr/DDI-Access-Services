package fr.insee.rmes.search.repository;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.insee.rmes.config.DDIItemRepositoryImplCondition;
import fr.insee.rmes.search.model.DDIItem;
import fr.insee.rmes.search.model.DDIQuery;
import fr.insee.rmes.search.model.DataCollectionContext;
import fr.insee.rmes.search.model.ResponseItem;
import fr.insee.rmes.search.model.ResponseSearchItem;

@Repository
@Conditional(value = DDIItemRepositoryImplCondition.class)
public class DDIItemRepositoryDBImpl implements DDIItemRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public IndexResponse save(String type, ResponseItem item) throws Exception {

		String qString = "";
		String id = item.getId();
		if (getItemById(id) == null) {
			qString = "INSERT INTO ddi_item (id, label, parent, groupid, subgroupid, studyunitid, datacollectionid, resourcepackageid, type, name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(qString, id, item.getLabel(), item.getParent(), item.getGroupId(), item.getSubGroupId(),
					item.getStudyUnitId(), item.getDataCollectionId(), item.getResourcePackageId(), type, item.getName());

		} else {
			qString = "UPDATE ddi_item set id=?, label=?, parent=?, groupid=?, subgroupid=?, studyunitid=?, datacollectionid=?, resourcepackageid=?, type=?, name=? where id=?";
			jdbcTemplate.update(qString, id, item.getLabel(), item.getParent(), item.getGroupId(), item.getSubGroupId(),
					item.getStudyUnitId(), item.getDataCollectionId(), item.getResourcePackageId(), type, item.getName(), id);
		}
		return null;
	}

	@Override
	public List<DDIItem> findByLabel(String label, String... types) throws Exception {
		return null;
	}

	@Override
	public List<DDIItem> findByLabelInSubGroup(String label, String subgroupId, String... types) throws Exception {
		return null;
	}

	@Override
	public List<DDIItem> getSubGroups() throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='sub-group'",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<DDIItem> getStudyUnits(String subGroupId) throws Exception {
		List<DDIItem> ddiItems;
		try {
			String query = "SELECT * FROM ddi_item WHERE type='study-unit' ";
			if (subGroupId != null) {
				query = query.concat("and subgroupid=?");
				ddiItems = jdbcTemplate.query(query, new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subGroupId);
			} else {
				ddiItems = jdbcTemplate.query(query, new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
			}
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<DDIItem> getDataCollections(String studyUnitId) throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query(
					"SELECT * FROM ddi_item WHERE type='data-collection' and studyunitid=?",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class), studyUnitId);
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public DeleteResponse delete(String type, String id) throws Exception {
		return null;
	}

	@Override
	public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception {
		List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='data-collection' and id=?",
				new BeanPropertyRowMapper<DDIItem>(DDIItem.class), dataCollectionId);
		DataCollectionContext dcContext = new DataCollectionContext();
		dcContext.setDataCollectionId(dataCollectionId);
		dcContext.setOperationId(ddiItems.get(0).getStudyUnitId());
		dcContext.setSerieId(ddiItems.get(0).getSubGroupId());
		return dcContext;
	}

	@Override
	public List<ResponseSearchItem> getItemsByCriteria(String subgroupId, String operationId, String dataCollectionId,
			DDIQuery criteria) throws Exception {
		return null;
	}

	@Override
	public DDIItem getItemById(String id) throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE id=?",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class), id);

			if (ddiItems.size() > 0) {
				return ddiItems.get(0);
			} else {
				return null;
			}
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void deleteAll() throws Exception {
		String qString = "DELETE FROM ddi_item";
		jdbcTemplate.update(qString);
	}

	@Override
	public List<DDIItem> getGroups() throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='group'",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
			return ddiItems;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
