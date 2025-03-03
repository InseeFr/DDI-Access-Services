package fr.insee.rmes;

import fr.insee.rmes.config.InseeSecurityTokenProperties;
import fr.insee.rmes.config.PropertiesLogger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(InseeSecurityTokenProperties.class)
public class RmesApplication {

	public static void main(String[] args) {

		var springApplicationBuilder = new SpringApplicationBuilder();
		springApplicationBuilder.sources(RmesApplication.class)
				.listeners(new PropertiesLogger()); /*Ajout du listener PropertiesLogger qui sera déclenché par Spring au démarrage*/
		springApplicationBuilder.build().run(args);

	}

}
