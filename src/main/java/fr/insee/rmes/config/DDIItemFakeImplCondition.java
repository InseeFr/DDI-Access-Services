package fr.insee.rmes.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DDIItemFakeImplCondition implements Condition {

	private String ddiItemRepositoryImpl;

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		
		try {
			Properties props = getEnvironmentProperties();
			ddiItemRepositoryImpl = props.getProperty("fr.insee.rmes.search.DDIItemRepository.impl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ddiItemRepositoryImpl.equals("DDIItemFakeImpl")) {
			return true;
		} else {
			return false;
		}

	}
	
	
	private Properties getEnvironmentProperties() throws IOException {
		Properties props = new Properties();
		String env = System.getProperty("fr.insee.rmes.env");
		if (null == env) {
			env = "dv";
		}
		String propsPath = String.format("env/%s/ddi-access-services.properties", env);
		props.load(getClass().getClassLoader().getResourceAsStream(propsPath));
		File f = new File(
				String.format("%s/webapps/%s", System.getProperty("catalina.base"), "ddi-access-services.properties"));
		if (f.exists() && !f.isDirectory()) {
			try (FileReader r = new FileReader(f)){
				props.load(r);
			}
		}
		File f2 = new File(
				String.format("%s/webapps/%s", System.getProperty("catalina.base"), "ddi-access-services.properties"));
		if (f2.exists() && !f2.isDirectory()) {
			try (FileReader r2 = new FileReader(f2)){
				props.load(r2);
			}
		}
		return props;
	}
	
	

}
