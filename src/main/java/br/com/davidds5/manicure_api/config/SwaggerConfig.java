package br.com.davidds5.manicure_api.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Manicure API - Gestão de Salão")
                        .version("v1")
                        .description("API completa para gestão de salão de beleza com Spring Boot")
                        .contact(new Contact()
                                .name("David Silva")
                                .email("SEU_EMAIL")
                                .url("https://github.com/davidds5")))
                                
                                .addSecurityItem(new SecurityRequirement()
                                .addList(securitySchemeName))
                                .components(new Components()
                                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}