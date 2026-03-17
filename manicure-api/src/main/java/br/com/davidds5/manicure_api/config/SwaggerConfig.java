package br.com.davidds5.manicure_api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Manicure API - Gestão de Salão")
                        .version("v1")
                        .description("API completa para gestão de salão de beleza com Spring Boot")
                        .contact(new Contact()
                                .name("Seu Nome")
                                .email("seu.email@exemplo.com")
                                .url("https://github.com/seuusuario/manicure-api")));
    }
}