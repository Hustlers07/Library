package com.library.user_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * OpenAPI Configuration
 * Configures Swagger documentation with bearer token authentication support
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0.0")
                        .description("REST API for user management and authentication with JWT Bearer token support")
                        .contact(new Contact()
                                .name("Library Team")
                                .email("support@library.com")
                        )
                )
                .servers(Arrays.asList(
                        new Server().url("http://192.168.49.2:30003/user-management/").description("Local Development Server"),
                        new Server().url("/user-management").description("Current Server")
                ))
                // .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token in the format: Bearer <your-jwt-token>")
                        )
                );
    }
}
