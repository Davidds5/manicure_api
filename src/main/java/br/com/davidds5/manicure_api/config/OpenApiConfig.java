package br.com.davidds5.manicure_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class OpenApiConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Ambiente Local (Desenvolvimento)"))
                .addServersItem(new Server()
                        .url("https://manicure-api-vi63.onrender.com")
                        .description("Produção (Render)"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"))
                .info(new Info()
                        .title("BelasUnhas SaaS API")
                        .version("1.0.0")
                        .description("Backend RESTful Multi-Tenant para Agendamento e Gestão de Salões de Beleza e Esmalterias."))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}