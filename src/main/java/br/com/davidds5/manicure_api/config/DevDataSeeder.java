package br.com.davidds5.manicure_api.config;

import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import br.com.davidds5.manicure_api.entity.TenantEntity;
import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.ServiceRepository;
import br.com.davidds5.manicure_api.repository.TenantRepository;
import br.com.davidds5.manicure_api.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final SubscriptionService subscriptionService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String testEmail = "teste@salao.com";
        String testSlug = "studio-bella";

        if (professionalRepository.findByEmail(testEmail).isEmpty()) {
            log.info("🌸 [DevDataSeeder] Criando dados de teste (Tenant, Proprietária e Serviços)...");

            TenantEntity tenant = tenantRepository.findBySlug(testSlug).orElseGet(() -> {
                TenantEntity t = TenantEntity.builder()
                        .name("Studio Bella Nails")
                        .slug(testSlug)
                        .plan(TenantPlan.PRO)
                        .status(TenantStatus.ACTIVE)
                        .brandColor("#ec4899")
                        .build();
                return tenantRepository.save(t);
            });

            ProfessionalEntity owner = ProfessionalEntity.builder()
                    .tenantId(tenant.getId())
                    .name("Mariana Silva")
                    .email(testEmail)
                    .password(passwordEncoder.encode("123456"))
                    .specialty("Nail Designer & Proprietária")
                    .active(true)
                    .build();
            owner = professionalRepository.save(owner);

            tenant.setOwnerId(owner.getId());
            tenantRepository.save(tenant);

            subscriptionService.createDefaultSubscription(tenant.getId(), tenant.getPlan());

            // Cadastra serviços de exemplo
            if (serviceRepository.count() == 0) {
                serviceRepository.save(ServiceEntity.builder()
                        .tenantId(tenant.getId())
                        .name("Alongamento em Fibra de Vidro")
                        .description("Alongamento completo com fibra de vidro")
                        .price(140.0)
                        .duration(60)
                        .active(true)
                        .build());

                serviceRepository.save(ServiceEntity.builder()
                        .tenantId(tenant.getId())
                        .name("Esmaltação em Gel")
                        .description("Esmaltação de alta durabilidade em gel")
                        .price(75.0)
                        .duration(45)
                        .active(true)
                        .build());

                serviceRepository.save(ServiceEntity.builder()
                        .tenantId(tenant.getId())
                        .name("Manicure & Pedicure Completa")
                        .description("Cutilagem, hidratação e esmaltação tradicional")
                        .price(65.0)
                        .duration(50)
                        .active(true)
                        .build());
            }

            log.info("✅ [DevDataSeeder] Conta de teste criada com sucesso: E-mail: {} | Senha: {}", testEmail, "123456");
        }
    }
}
