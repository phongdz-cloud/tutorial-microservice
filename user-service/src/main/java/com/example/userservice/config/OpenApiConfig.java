package com.example.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearer = new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("REST API for managing users in the microservices architecture")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components().addSecuritySchemes("bearerAuth", bearer))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("API Gateway server"),
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server")));
    }
}
