package fr.insee.rmes.config;

import java.io.File;
import java.io.FileNotFoundException;
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
		// Load properties in files
		loadPropertiesInFile(props, "ddi-access-services.properties");
		loadPropertiesInFile(props, "rmspogbo.properties");
		loadPropertiesInFile(props, "rmespogbo.properties");
		return props;
	}

	/**
	 * Load the properties specified in a file to a Properties object.
	 * 
	 * @param props
	 *            : Properties trageted
	 * @param nameFileProps
	 *            : name of the File to read in the Webapp Folder
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @see Properties
	 */
	private void loadPropertiesInFile(Properties props, String nameFileProps)
			throws FileNotFoundException, IOException {
		File file = new File(String.format("%s/webapps/%s", System.getProperty("catalina.base"), nameFileProps));
		if (file.exists() && !file.isDirectory()) {
			FileReader fileReader = new FileReader(file);
			props.load(fileReader);
			fileReader.close();
		}
	}

}
