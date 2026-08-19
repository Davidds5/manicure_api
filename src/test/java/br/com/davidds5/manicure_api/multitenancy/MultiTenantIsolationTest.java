package br.com.davidds5.manicure_api.multitenancy;

import br.com.davidds5.manicure_api.config.TenantContext;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import br.com.davidds5.manicure_api.entity.TenantEntity;
import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.ServiceRepository;
import br.com.davidds5.manicure_api.repository.TenantRepository;
import br.com.davidds5.manicure_api.service.ClientService;
import br.com.davidds5.manicure_api.service.ProfessionalService;
import br.com.davidds5.manicure_api.service.ServiceService;
import br.com.davidds5.manicure_api.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class MultiTenantIsolationTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private TokenService tokenService;

    private TenantEntity tenantA;
    private TenantEntity tenantB;

    @BeforeEach
    void setupTenants() {
        tenantA = tenantRepository.save(TenantEntity.builder()
                .name("Salão Beleza Pura")
                .slug("beleza-pura")
                .plan(TenantPlan.FREE)
                .status(TenantStatus.ACTIVE)
                .brandColor("#FF1493")
                .build());

        tenantB = tenantRepository.save(TenantEntity.builder()
                .name("Salão Glamour Total")
                .slug("glamour-total")
                .plan(TenantPlan.PRO)
                .status(TenantStatus.ACTIVE)
                .brandColor("#8A2BE2")
                .build());
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Garante que o Tenant B não consegue visualizar ou acessar clientes do Tenant A")
    void testTenantDataIsolationForClients() {
        // 1. Cadastra Cliente no escopo do Tenant A
        TenantContext.setTenantId(tenantA.getId());
        ClientEntity clientA = clientRepository.save(ClientEntity.builder()
                .name("Ana do Salão A")
                .email("ana@salaoa.com")
                .phone("11999990001")
                .password("123456")
                .tenantId(tenantA.getId())
                .build());

        // 2. Consulta estando logado no Tenant A
        TenantContext.setTenantId(tenantA.getId());
        var clientFoundA = clientRepository.findById(clientA.getId());
        assertTrue(clientFoundA.isPresent(), "Tenant A deve conseguir encontrar seu próprio cliente");

        // 3. Muda contexto para Tenant B
        TenantContext.setTenantId(tenantB.getId());

        // 4. Tenant B tenta buscar o cliente criado no Tenant A
        // Com o Hibernate Filter ativado, a query retorna vazio (isolamento garantido)
        var clientFoundB = clientRepository.findById(clientA.getId());
        assertTrue(clientFoundB.isEmpty(), "Tenant B NÃO pode ter acesso aos dados do cliente do Tenant A");
    }

    @Test
    @DisplayName("Garante que o Tenant B não visualiza serviços do Tenant A")
    void testTenantDataIsolationForServices() {
        // 1. Cria Serviços no Tenant A
        TenantContext.setTenantId(tenantA.getId());
        serviceRepository.save(ServiceEntity.builder()
                .name("Manicure Express - Salão A")
                .description("Cutilagem e esmaltação")
                .price(45.0)
                .duration(40)
                .active(true)
                .tenantId(tenantA.getId())
                .build());

        // 2. Cria Serviço no Tenant B
        TenantContext.setTenantId(tenantB.getId());
        serviceRepository.save(ServiceEntity.builder()
                .name("Pedicure Spa - Salão B")
                .description("Spa dos pés")
                .price(70.0)
                .duration(60)
                .active(true)
                .tenantId(tenantB.getId())
                .build());

        // 3. Consulta serviços estando no Tenant B
        TenantContext.setTenantId(tenantB.getId());
        List<ServiceEntity> servicesB = serviceRepository.findAll();

        assertEquals(1, servicesB.size(), "Tenant B deve ver apenas os seus próprios serviços");
        assertEquals("Pedicure Spa - Salão B", servicesB.get(0).getName());
        assertEquals(tenantB.getId(), servicesB.get(0).getTenantId());
    }

    @Test
    @DisplayName("Garante que o token JWT gera e propaga a claim correta de tenant_id")
    void testJwtTokenTenantClaim() {
        ProfessionalEntity profTenantA = ProfessionalEntity.builder()
                .name("Profissional A")
                .email("prof@salaoa.com")
                .password("hash123")
                .specialty("Manicure")
                .active(true)
                .tenantId(tenantA.getId())
                .build();

        String token = tokenService.gerarToken(profTenantA);
        assertNotNull(token);

        Long extractedTenantId = tokenService.getTenantId(token);
        assertEquals(tenantA.getId(), extractedTenantId, "O tenant_id no JWT deve ser igual ao do profissional");
    }
}
