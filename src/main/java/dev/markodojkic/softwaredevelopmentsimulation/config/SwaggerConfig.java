package dev.markodojkic.softwaredevelopmentsimulation.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Software development simulator™ API")
                        .description("This is the API documentation for the Software development simulator™ Developed by Ⓒ Marko Dojkić")
                        .version("v1.4.0")
                        .license(new License()
                            .name("MIT License")
                            .url("https://github.com/MarkoDojkic/Software-Development-Simulation/blob/main/LICENSE")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/MarkoDojkic/Software-Development-Simulation"));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }
}
