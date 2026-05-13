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
                        .url("https://manicure-api-vi63.onrender.com")
                        .description("Produção - Render"))
                .addSecurityItem(new SecurityRequirement()
                    .addList("bearerAuth"))
                .info(new Info()
                        .title("Manicure API")
                        .version("1.0")
                        .description("API para agendamento de serviços de manicure."))
                .components(new Components()
                    .addSecuritySchemes("bearerAuth", 
                        new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")));
    }

}