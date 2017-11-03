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
			qString = "INSERT INTO ddi_item (id, label, parent, groupid, subgroupid, studyunitid, datacollectionid, resourcepackageid, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			jdbcTemplate.update(qString, id, item.getLabel(), item.getParent(), item.getGroupId(), item.getSubGroupId(),
					item.getStudyUnitId(), item.getDataCollectionId(), item.getResourcePackageId(), type);

		} else {
			qString = "UPDATE ddi_item set id=?, label=?, parent=?, groupid=?, subgroupid=?, studyunitid=?, datacollectionid=?, resourcepackageid=?, type=? where id=?";
			jdbcTemplate.update(qString, id, item.getLabel(), item.getParent(), item.getGroupId(), item.getSubGroupId(),
					item.getStudyUnitId(), item.getDataCollectionId(), item.getResourcePackageId(), type, id);
		}
		return null;
	}

	@Override
	public List<DDIItem> findByLabel(String label, String... types) throws Exception {
		return null;
	}

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
	public List<DDIItem> getStudyUnits(String subgGroupId) throws Exception {
		try {
			List<DDIItem> ddiItems = jdbcTemplate.query(
					"SELECT * FROM ddi_item WHERE type='study-unit' and subgroupid=?",
					new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subgGroupId);
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
		List<ResponseSearchItem> responses = new ArrayList<ResponseSearchItem>();
		List<DDIItem> ddiItems = null;
		String criteriaFilter = null;
		if (criteria.getFilter() != null) {
			criteriaFilter = "%" + criteria.getFilter() + "%";
			criteriaFilter = criteriaFilter.toUpperCase();
		}
		for (String type : criteria.getTypes()) {
			if (type.toLowerCase().equals("instrument")) {
				if (subgroupId != null) {
					if (operationId != null) {
						if (dataCollectionId != null) {
							if (criteriaFilter != null) {
								ddiItems = jdbcTemplate.query(
										"SELECT * FROM ddi_item WHERE type='instrument' and datacollectionid=? and UPPER(label) like ?",
										new BeanPropertyRowMapper<DDIItem>(DDIItem.class), dataCollectionId,
										criteriaFilter);
							} else {
								ddiItems = jdbcTemplate.query(
										"SELECT * FROM ddi_item WHERE type='instrument' and datacollectionid=? ",
										new BeanPropertyRowMapper<DDIItem>(DDIItem.class), dataCollectionId);
							}
						} else {
							if (criteriaFilter != null) {
								ddiItems = jdbcTemplate.query(
										"SELECT * FROM ddi_item WHERE type='instrument' and operationid=? and UPPER(label) like ?",
										new BeanPropertyRowMapper<DDIItem>(DDIItem.class), operationId, criteriaFilter);
							} else {
								ddiItems = jdbcTemplate.query(
										"SELECT * FROM ddi_item WHERE type='instrument' and operationid=?",
										new BeanPropertyRowMapper<DDIItem>(DDIItem.class), operationId);
							}
						}
					} else {
						if (criteriaFilter != null) {
							ddiItems = jdbcTemplate.query(
									"SELECT * FROM ddi_item WHERE type='instrument' and subgroupid=? and UPPER(label) like ?",
									new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subgroupId, criteriaFilter);
						} else {
							ddiItems = jdbcTemplate.query(
									"SELECT * FROM ddi_item WHERE type='instrument'and subgroupid=?",
									new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subgroupId);
						}
					}
				} else {
					if (criteriaFilter != null) {
						ddiItems = jdbcTemplate.query(
								"SELECT * FROM ddi_item WHERE type='instrument' and UPPER(label) like ?",
								new BeanPropertyRowMapper<DDIItem>(DDIItem.class), criteriaFilter);
					} else {
						ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='instrument'",
								new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
					}
				}

				for (DDIItem ddiItem : ddiItems) {
					ResponseSearchItem rsi = new ResponseSearchItem();
					rsi.setId(ddiItem.getId());
					rsi.setTitle(ddiItem.getLabel());
					rsi.setDataCollectionId(getItemById(ddiItem.getDataCollectionId()).getLabel());
					rsi.setStudyUnitId(getItemById(ddiItem.getStudyUnitId()).getLabel());
					rsi.setSubgroupId(getItemById(ddiItem.getSubGroupId()).getLabel());
					rsi.setVersion("1");
					responses.add(rsi);
				}	
				
				
			}

			if (type.toLowerCase().equals("codelist")) {
				if (subgroupId != null) {
					if (operationId != null) {
						if (criteriaFilter != null) {
							ddiItems = jdbcTemplate.query(
									"SELECT * FROM ddi_item WHERE type='code-list' and operationid=?  and UPPER(label) like ?",
									new BeanPropertyRowMapper<DDIItem>(DDIItem.class), operationId, criteriaFilter);
						} else {
							ddiItems = jdbcTemplate.query(
									"SELECT * FROM ddi_item WHERE type='code-list' and operationid=?",
									new BeanPropertyRowMapper<DDIItem>(DDIItem.class), operationId);
						}
					} else {
						if (criteriaFilter != null) {
							ddiItems = jdbcTemplate.query(
									"SELECT * FROM ddi_item WHERE type='code-list' and subgroupid=?and UPPER(label) like ?",
									new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subgroupId, criteriaFilter);
						} else {
							ddiItems = jdbcTemplate.query(
									"SELECT * FROM ddi_item WHERE type='code-list' and subgroupid=?",
									new BeanPropertyRowMapper<DDIItem>(DDIItem.class), subgroupId);
						}
					}
				} else {
					if (criteriaFilter != null) {
						ddiItems = jdbcTemplate.query(
								"SELECT * FROM ddi_item WHERE type='code-list' and UPPER(label) like ?",
								new BeanPropertyRowMapper<DDIItem>(DDIItem.class), criteriaFilter);
					} else {
						ddiItems = jdbcTemplate.query("SELECT * FROM ddi_item WHERE type='code-list'",
								new BeanPropertyRowMapper<DDIItem>(DDIItem.class));
					}
				}
			}
			for (DDIItem ddiItem : ddiItems) {
				ResponseSearchItem rsi = new ResponseSearchItem();
				rsi.setId(ddiItem.getId());
				rsi.setTitle(ddiItem.getLabel());
				//rsi.setStudyUnitId(getItemById(ddiItem.getStudyUnitId()).getLabel());
				//rsi.setSubgroupId(getItemById(ddiItem.getSubGroupId()).getLabel());
				rsi.setVersion("1");
				responses.add(rsi);
			}	
		}

		
		if (responses.size() > 0) {
			return responses;
		} else {
			return null;
		}
	}

	private DDIItem getItemById(String id) throws Exception {
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

}
