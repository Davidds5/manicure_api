package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.enums.ProfessionalRole;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceOptimizedTest {

    private TokenService tokenService;
    private static final String SECRET = "a-very-strong-secret-key-that-is-at-least-32-bytes-long";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
    }

    @Test
    @DisplayName("Deve gerar token e decodificar em uma única operação com claims corretas")
    void testSingleDecodeTokenVerification() {
        ProfessionalEntity professional = ProfessionalEntity.builder()
                .id(10L)
                .tenantId(42L)
                .name("Alice")
                .email("alice@tenant.com")
                .role(ProfessionalRole.TENANT_ADMIN)
                .build();

        String token = tokenService.gerarToken(professional);
        assertNotNull(token);

        DecodedJWT decoded = tokenService.decodeToken(token);
        assertNotNull(decoded);
        assertEquals("alice@tenant.com", decoded.getSubject());
        assertEquals("ROLE_TENANT_ADMIN", decoded.getClaim("role").asString());
        assertEquals(42L, decoded.getClaim("tenant_id").asLong());
    }

    @Test
    @DisplayName("Deve rejeitar token inválido ou adulterado")
    void testInvalidTokenRejection() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.payload";
        assertThrows(RuntimeException.class, () -> tokenService.decodeToken(invalidToken));
    }
}
