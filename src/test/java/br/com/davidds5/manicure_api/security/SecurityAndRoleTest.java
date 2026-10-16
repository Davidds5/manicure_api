package br.com.davidds5.manicure_api.security;

import br.com.davidds5.manicure_api.config.JwtSecurityValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SecurityAndRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("ROLE_PROFESSIONAL deve receber 403 Forbidden em /admin/tenants")
    @WithMockUser(username = "pro@salao.com", roles = {"PROFESSIONAL"})
    void professionalAccessToSuperAdminEndpoints_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/tenants"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_TENANT_ADMIN deve receber 403 Forbidden em /admin/tenants")
    @WithMockUser(username = "admin@salao.com", roles = {"TENANT_ADMIN"})
    void tenantAdminAccessToSuperAdminEndpoints_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/tenants"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_SUPER_ADMIN deve receber 200 OK em /admin/tenants")
    @WithMockUser(username = "super@platform.com", roles = {"SUPER_ADMIN"})
    void superAdminAccessToSuperAdminEndpoints_Ok() throws Exception {
        mockMvc.perform(get("/admin/tenants"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anônimo deve acessar /actuator/health com sucesso (200)")
    void anonymousAccessToActuatorHealth_Ok() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anônimo não deve acessar /actuator/metrics (401/403)")
    void anonymousAccessToActuatorMetrics_UnauthorizedOrForbidden() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_SUPER_ADMIN acessa /actuator/metrics com sucesso (200)")
    @WithMockUser(username = "super@platform.com", roles = {"SUPER_ADMIN"})
    void superAdminAccessToActuatorMetrics_Ok() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JwtSecurityValidator deve falhar em profile prod se secret for curto (< 32 bytes) ou nulo")
    void jwtSecurityValidator_FailsInProdWithShortSecret() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");

        JwtSecurityValidator validator = new JwtSecurityValidator(env);
        ReflectionTestUtils.setField(validator, "secret", "short-secret");

        assertThrows(IllegalStateException.class, validator::validateJwtSecret);
    }
}
