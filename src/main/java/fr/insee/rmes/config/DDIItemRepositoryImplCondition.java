package fr.insee.rmes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
@PropertySource(value = { "classpath:env/${fr.insee.rmes.env:dev}/ddi-access-services.properties",
		"file:${catalina.base}/webapps/ddi-access-services.properties" }, ignoreResourceNotFound = true)
public class DDIItemRepositoryImplCondition implements Condition {

	@Value("${fr.insee.rmes.search.DDIItemRepository.impl}")
	private String ddiItemRepositoryImpl;

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
//		if (context.getEnvironment().getProperty("fr.insee.rmes.search.DDIItemRepository.impl")
//				.equals("DDIItemRepositoryImpl")) {
//			return true;
//		} else {
//			return false;
//		}
		return false;
	}

}
