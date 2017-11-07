package fr.insee.rmes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import fr.insee.rmes.search.repository.DDIItemFakeImpl;
import fr.insee.rmes.search.repository.DDIItemRepository;
import fr.insee.rmes.search.repository.DDIItemRepositoryDBImpl;
import fr.insee.rmes.search.repository.DDIItemRepositoryImpl;


@Configuration
public class DDIItemRepositoryConfig {

	
	@Bean(name = "ddiItemRepository")
	@Conditional(value = DDIItemFakeImplCondition.class)
	public DDIItemRepository getDDIItemFakeImpl() {
		return new DDIItemFakeImpl();
	}

	@Bean(name = "ddiItemRepository")
	@Conditional(value = DDIItemRepositoryImplCondition.class)
	public DDIItemRepository getDDIItemRepositoryImpl() {
		return new DDIItemRepositoryImpl();
	}
	
	@Bean(name = "ddiItemRepository")
	@Conditional(value = DDIItemRepositoryDBImplCondition.class)
	public DDIItemRepository getDDIItemRepositoryDBImpl() {
		return new DDIItemRepositoryDBImpl();
	}

}
