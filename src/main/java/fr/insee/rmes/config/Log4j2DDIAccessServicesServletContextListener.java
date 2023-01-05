package fr.insee.rmes.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.apache.logging.log4j.web.Log4jWebSupport;
import org.springframework.context.annotation.Configuration;



@Configuration
@WebListener
public class Log4j2DDIAccessServicesServletContextListener implements ServletContextListener {

	private String log4j2ConfigFile;

	private Log4jServletContextListener listener;
	
	private static final String LOG4J_CONFIG_FILE = "log4j2.xml";
	private static final String CATALINA_BASE ="catalina.base";
	private static final String WEBAPPS = "%s/webapps/%s";

	public Log4j2DDIAccessServicesServletContextListener() {
		this.listener = new Log4jServletContextListener();
		try {
			this.getEnvironmentProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().setInitParameter(Log4jWebSupport.LOG4J_CONFIG_LOCATION,
				log4j2ConfigFile);
		listener.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		listener.contextDestroyed(servletContextEvent);
	}

	
	private void getEnvironmentProperties() throws IOException {
		
		Properties props = this.getProperties();
		String log4JExternalFile = props.getProperty("fr.insee.rmespogbo.log.configuration");
		this.log4j2ConfigFile = LOG4J_CONFIG_FILE;
		File f = new File(String.format(WEBAPPS, System.getProperty(CATALINA_BASE), LOG4J_CONFIG_FILE));
		if (f.exists() && !f.isDirectory()) {
			this.log4j2ConfigFile = String.format(WEBAPPS, System.getProperty(CATALINA_BASE), LOG4J_CONFIG_FILE);
		}
		File f2 = new File(log4JExternalFile);
		if (f2.exists() && !f2.isDirectory()) {
			this.log4j2ConfigFile = String.format(log4JExternalFile);
		}
		

	}
	
	private Properties getProperties() throws IOException {
		Properties props = new Properties();
		String env = System.getProperty("fr.insee.rmes.env");
		if (null == env) {
			env = "dev";
		}
		String propsPath = String.format("env/%s/ddi-access-services.properties", env);
		props.load(getClass().getClassLoader().getResourceAsStream(propsPath));
		File f = new File(
				String.format(WEBAPPS, System.getProperty(CATALINA_BASE), "ddi-access-services.properties"));
		if (f.exists() && !f.isDirectory()) {
			try (FileReader r = new FileReader(f)){
				props.load(r);
			}
		}
		File f2 = new File(
				String.format(WEBAPPS, System.getProperty(CATALINA_BASE), "rmspogbo.properties"));
		if (f2.exists() && !f2.isDirectory()) {
			try (FileReader r2 = new FileReader(f2)) {
				props.load(r2);
			}
		}
		File f3 = new File(
				String.format(WEBAPPS, System.getProperty(CATALINA_BASE), "rmespogbo.properties"));
		if (f3.exists() && !f3.isDirectory()) {
			try (FileReader r3 = new FileReader(f3)){
				props.load(r3);
			}
		}
		return props;
	}
	
}
