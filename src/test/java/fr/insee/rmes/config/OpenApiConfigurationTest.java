package fr.insee.rmes.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigurationTest {

    @ParameterizedTest
    @ValueSource(strings = { "2025", "--",""," " })
    void shouldReturnOpenAPIWhenAppVersionIsNotNull(String appVersion) {
        OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();
        OpenAPI response =openApiConfiguration.openAPI(appVersion);
        assertTrue(response.toString().contains("version: "+appVersion));
    }

    @Test
    void shouldReturnOpenAPIWhenAppVersionIsNull() {
        OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();
        OpenAPI response =openApiConfiguration.openAPI(null);
        assertTrue(response.toString().contains("version: null"));
    }

}