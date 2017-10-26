package fr.insee.rmes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.MetadataServiceFakeImpl;
import fr.insee.rmes.metadata.service.MetadataServiceImpl;


@Configuration
public class MetadataServiceConfig {

	
	@Bean(name = "metadataService")
	@Conditional(value = MetadataServiceFakeImplCondition.class)
	public MetadataService getMetadataServiceFakeImpl() {
		return new MetadataServiceFakeImpl();
	}

	@Bean(name = "metadataService")
	@Conditional(value = MetadataServiceImplCondition.class)
	public MetadataService getMetadataServiceImpl() {
		return new MetadataServiceImpl();
	}

}
