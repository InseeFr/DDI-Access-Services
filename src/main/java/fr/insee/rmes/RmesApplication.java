package fr.insee.rmes;

import fr.insee.rmes.config.PropertiesLogger;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@OpenAPIDefinition(info = @Info(title = "DDI-AS", version = "${fr.insee.rmes.ddias.version}"))
@SpringBootApplication
public class RmesApplication {

	public static void main(String[] args) {

		var springApplicationBuilder = new SpringApplicationBuilder();
		springApplicationBuilder.sources(RmesApplication.class)
				.listeners(new PropertiesLogger()); /*Ajout du listener PropertiesLogger qui sera déclenché par Spring au démarrage*/
		springApplicationBuilder.build().run(args);

	}

}
