package fr.insee.rmes.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * ² Created by acordier on 24/07/17.
 */
public class SwaggerConfig extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7852516054619312011L;
	
	private final static Logger logger = LogManager.getLogger(SwaggerConfig.class);

	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
			Properties props = getEnvironmentProperties();
			BeanConfig beanConfig = new BeanConfig();
			beanConfig.setTitle("DDI Access services");
			beanConfig.setVersion("0.1");
			beanConfig.setDescription("DDI Access API endpoints");
            beanConfig.setSchemes(new String[]{props.getProperty("fr.insee.rmes.api.scheme")});
            beanConfig.setBasePath(props.getProperty("fr.insee.rmes.api.name"));
			beanConfig.setHost(props.getProperty("fr.insee.rmes.api.host"));
			beanConfig.setResourcePackage("fr.insee.rmes.webservice.rest");
			beanConfig.setScan(true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private Properties getEnvironmentProperties() throws IOException {
		Properties props = new Properties();
		String env = System.getProperty("fr.insee.rmes.env");
		if (null == env) {
			env = "dev";
		}
		String propsPath = String.format("env/%s/ddi-access-services.properties", env);
		props.load(getClass().getClassLoader().getResourceAsStream(propsPath));
		File f = new File(
				String.format("%s/webapps/%s", System.getProperty("catalina.base"), "ddi-access-services.properties"));
		if (f.exists() && !f.isDirectory()) {
			FileReader r = new FileReader(f);
			props.load(r);
			r.close();
		}
		File f2 = new File(
				String.format("%s/webapps/%s", System.getProperty("catalina.base"), "rmspogbo.properties"));
		if (f2.exists() && !f2.isDirectory()) {
			FileReader r2 = new FileReader(f2);
			props.load(r2);
			r2.close();
		}
		File f3 = new File(
				String.format("%s/webapps/%s", System.getProperty("catalina.base"), "rmespogbo.properties"));
		if (f3.exists() && !f3.isDirectory()) {
			FileReader r3 = new FileReader(f3);
			props.load(r3);
			r3.close();
		}
		return props;
	}

}
