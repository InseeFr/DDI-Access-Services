package fr.insee.rmes.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesLoggerTest {

    @Test
    void shouldVerifyStateAndTestResoutValeurAvecMasquePwdWhenOnApplicationEvent() {
        DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext();
        SpringApplication springApplication = new SpringApplication();
        String[] args = {"password", "World"};
        ConfigurableEnvironment configurableEnvironment = new StandardEnvironment();
        ApplicationEnvironmentPreparedEvent environment = new ApplicationEnvironmentPreparedEvent(defaultBootstrapContext,springApplication,args,configurableEnvironment);

        PropertiesLogger propertiesLogger = new PropertiesLogger();
        String save = propertiesLogger.toString();
        propertiesLogger.onApplicationEvent(environment);
        String after = propertiesLogger.toString();

        assertEquals(save,after);

    }
}