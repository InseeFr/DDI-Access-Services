package fr.insee.rmes.metadata.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GroupRepositoryImpl implements GroupRepository {

	// @Autowired
	// JdbcTemplate jdbcTemplate;

	// TODO externalize the root ID in a database
	@Override
	public List<String> getRootIds() throws Exception {
		// return jdbcTemplate.queryForList("SELECT id FROM ddi_group",
		// String.class);
		List<String> rootIds = new ArrayList<String>();
		rootIds.add("");
		return rootIds;
	}

}
