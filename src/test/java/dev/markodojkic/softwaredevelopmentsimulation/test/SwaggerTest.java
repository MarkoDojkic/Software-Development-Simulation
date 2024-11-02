package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.config.SwaggerConfig;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(SwaggerConfig.class) // Import the Swagger configuration
class SwaggerTest {
    @Autowired
    private OpenAPI openAPI;

    @Autowired
    private GroupedOpenApi groupedOpenApi;

    @Test
    void testCustomOpenAPIConfig() {
        Info info = openAPI.getInfo();
        assertEquals("Software development simulator™ API", info.getTitle());
        assertEquals("This is the API documentation for the Software development simulator™ Developed by Ⓒ Marko Dojkić", info.getDescription());
        assertEquals("v1.4.0", info.getVersion());

        License license = info.getLicense();
        assertEquals("MIT License", license.getName());
        assertEquals("https://github.com/MarkoDojkic/Software-Development-Simulation/blob/main/LICENSE", license.getUrl());

        ExternalDocumentation externalDocs = openAPI.getExternalDocs();
        assertEquals("GitHub Repository", externalDocs.getDescription());
        assertEquals("https://github.com/MarkoDojkic/Software-Development-Simulation", externalDocs.getUrl());
    }

    @Test
    void testApiGroupConfig() {
        // Verify that the GroupedOpenApi is set up with the correct group name and paths
        assertEquals("api", groupedOpenApi.getGroup());
        assertEquals(List.of("/api/**"), groupedOpenApi.getPathsToMatch());
    }
}