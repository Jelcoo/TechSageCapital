package com.techsage.banking.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        // Server environments
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development server");

        Server prodServer = new Server()
                .url("https://api.techsagecapital.nl")
                .description("Production server");

        // API info
        Info information = new Info()
                .title("TechSage Capital API")
                .version("1.0")
                .description("This API exposes endpoints to manage the Capital banking app™");

        // Security scheme for JWT Bearer token
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Security scheme for ATM JWT Bearer token
        SecurityScheme atmBearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("ATM-Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        SecurityRequirement atmSecurityRequirement = new SecurityRequirement()
                .addList("atmBearerAuth");

        return new OpenAPI()
                .info(information)
                .servers(List.of(devServer, prodServer))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth)
                        .addSecuritySchemes("atmBearerAuth", atmBearerAuth))
                .addSecurityItem(securityRequirement)
                .addSecurityItem(atmSecurityRequirement);
    }
}
