package br.com.davidds5.manicure_api.multitenancy;

import br.com.davidds5.manicure_api.dto.AutenticacaoDTO;
import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import br.com.davidds5.manicure_api.entity.TenantEntity;
import br.com.davidds5.manicure_api.enums.ProfessionalRole;
import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.ServiceRepository;
import br.com.davidds5.manicure_api.repository.TenantRepository;
import br.com.davidds5.manicure_api.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
public class PublicPortalMultiTenantTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private TenantEntity tenantA;
    private TenantEntity tenantB;
    private TenantEntity tenantSuspended;

    @BeforeEach
    void setUp() {
        tenantA = tenantRepository.save(TenantEntity.builder()
                .name("Salão Glamour A")
                .slug("salao-a")
                .plan(TenantPlan.PRO)
                .status(TenantStatus.ACTIVE)
                .brandColor("#ff0077")
                .build());

        tenantB = tenantRepository.save(TenantEntity.builder()
                .name("Salão Elegance B")
                .slug("salao-b")
                .plan(TenantPlan.FREE)
                .status(TenantStatus.ACTIVE)
                .brandColor("#0077ff")
                .build());

        tenantSuspended = tenantRepository.save(TenantEntity.builder()
                .name("Salão Bloqueado")
                .slug("salao-suspenso")
                .plan(TenantPlan.FREE)
                .status(TenantStatus.SUSPENDED)
                .brandColor("#000000")
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .tenantId(tenantA.getId())
                .name("Esmaltação Salão A")
                .description("Desc A")
                .price(BigDecimal.valueOf(50.0))
                .duration(45)
                .active(true)
                .build());

        serviceRepository.save(ServiceEntity.builder()
                .tenantId(tenantB.getId())
                .name("Esmaltação Salão B")
                .description("Desc B")
                .price(BigDecimal.valueOf(70.0))
                .duration(50)
                .active(true)
                .build());
    }

    @Test
    @DisplayName("Rota pública isolada por slug retorna apenas os serviços do salão A")
    void getPublicServicesBySlug_ReturnsOnlyTenantServices() throws Exception {
        mockMvc.perform(get("/api/v1/public/salao-a/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Esmaltação Salão A")));
    }

    @Test
    @DisplayName("Rota pública com slug inexistente ou salão suspenso retorna 404")
    void getPublicServicesBySlug_NonExistentOrSuspended_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/public/slug-que-nao-existe/services"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/public/salao-suspenso/services"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Requisição anônima para rotas privadas antigas sem slug exige autenticação (401/403)")
    void anonymousAccessToOldRoutes_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Cadastro público de cliente isolado por slug com validação de e-mail duplicado (409)")
    void publicClientSignup_ScopedBySlug_HandlesDuplicatesProperly() throws Exception {
        ClientCreatedDTO dto = ClientCreatedDTO.builder()
                .name("Cliente Teste")
                .phone("(11) 98888-0000")
                .email("duplicado@cliente.com")
                .password("senha123456")
                .build();

        // 1. Cadastra no salão A -> 201 Created
        mockMvc.perform(post("/api/v1/public/salao-a/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("duplicado@cliente.com")));

        // 2. Tenta cadastrar novamente no mesmo salão A -> 409 Conflict
        mockMvc.perform(post("/api/v1/public/salao-a/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

        // 3. Cadastra o mesmo e-mail no salão B -> Permitido (201 Created)
        mockMvc.perform(post("/api/v1/public/salao-b/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("duplicado@cliente.com")));
    }

    @Test
    @DisplayName("Login com mesmo e-mail em dois salões gera JWT isolado por tenant")
    void loginWithSameEmailInTwoTenants_GeneratesCorrectTenantJwt() throws Exception {
        String sharedEmail = "mesmo_email@profissional.com";
        String password = "senhaForte123";
        String encodedPassword = passwordEncoder.encode(password);

        professionalRepository.save(ProfessionalEntity.builder()
                .tenantId(tenantA.getId())
                .name("Profissional A")
                .email(sharedEmail)
                .specialty("Manicure")
                .password(encodedPassword)
                .role(ProfessionalRole.PROFESSIONAL)
                .active(true)
                .build());

        professionalRepository.save(ProfessionalEntity.builder()
                .tenantId(tenantB.getId())
                .name("Profissional B")
                .email(sharedEmail)
                .specialty("Pedicure")
                .password(encodedPassword)
                .role(ProfessionalRole.TENANT_ADMIN)
                .active(true)
                .build());

        // Login no salão A
        AutenticacaoDTO loginDtoA = new AutenticacaoDTO(sharedEmail, password, "salao-a");
        var responseA = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDtoA)))
                .andExpect(status().isOk())
                .andReturn();

        String tokenA = objectMapper.readTree(responseA.getResponse().getContentAsString()).get("token").asText();
        assertEquals(tenantA.getId(), tokenService.getTenantId(tokenA));

        // Login no salão B
        AutenticacaoDTO loginDtoB = new AutenticacaoDTO(sharedEmail, password, "salao-b");
        var responseB = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDtoB)))
                .andExpect(status().isOk())
                .andReturn();

        String tokenB = objectMapper.readTree(responseB.getResponse().getContentAsString()).get("token").asText();
        assertEquals(tenantB.getId(), tokenService.getTenantId(tokenB));

        // Login com slug errado -> 401 genérico
        AutenticacaoDTO loginDtoWrongSlug = new AutenticacaoDTO(sharedEmail, password, "slug-inexistente");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDtoWrongSlug)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("E-mail ou senha incorretos")));

        // Login com senha errada -> 401 genérico
        AutenticacaoDTO loginDtoWrongPass = new AutenticacaoDTO(sharedEmail, "senha-errada", "salao-a");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDtoWrongPass)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("E-mail ou senha incorretos")));

        // Login direto por e-mail sem tenantSlug (para contas com e-mail único)
        String uniqueEmail = "direto@profissional.com";
        professionalRepository.save(ProfessionalEntity.builder()
                .tenantId(tenantA.getId())
                .name("Profissional Direto")
                .email(uniqueEmail)
                .specialty("Manicure")
                .password(encodedPassword)
                .role(ProfessionalRole.TENANT_ADMIN)
                .active(true)
                .build());

        AutenticacaoDTO loginDirectDto = new AutenticacaoDTO(uniqueEmail, password, null);
        var responseDirect = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDirectDto)))
                .andExpect(status().isOk())
                .andReturn();

        String directToken = objectMapper.readTree(responseDirect.getResponse().getContentAsString()).get("token").asText();
        assertNotNull(directToken);
        assertEquals(tenantA.getId(), tokenService.getTenantId(directToken));
    }
}
