package br.com.davidds5.manicure_api.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
public class JwtSecurityValidator {

    @Value("${api.security.token.secret:}")
    private String secret;

    private final Environment environment;

    public JwtSecurityValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateJwtSecret() {
        boolean isProdProfile = Arrays.asList(environment.getActiveProfiles()).contains("prod");

        if (isProdProfile) {
            if (secret == null || secret.isBlank()) {
                throw new IllegalStateException("FALHA DE INICIALIZAÇÃO EM PRODUÇÃO: Variável de ambiente JWT_SECRET não informada!");
            }
            if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
                throw new IllegalStateException("FALHA DE INICIALIZAÇÃO EM PRODUÇÃO: JWT_SECRET deve possuir no mínimo 32 bytes (256 bits) para garantir segurança com HMAC-SHA256!");
            }
            log.info("JWT_SECRET validado com sucesso para ambiente de produção.");
        }
    }
}
