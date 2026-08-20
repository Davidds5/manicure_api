package br.com.davidds5.manicure_api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @org.springframework.beans.factory.annotation.Autowired
    private br.com.davidds5.manicure_api.repository.TenantRepository tenantRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private br.com.davidds5.manicure_api.repository.ProfessionalRepository professionalRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private br.com.davidds5.manicure_api.repository.ServiceRepository serviceRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private br.com.davidds5.manicure_api.service.SubscriptionService subscriptionService;

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/seed-test")
    public String seedTest() {
        String testEmail = "teste@salao.com";
        String testSlug = "studio-bella";

        var profOpt = professionalRepository.findByEmail(testEmail);
        if (profOpt.isPresent()) {
            var prof = profOpt.get();
            prof.setPassword(passwordEncoder.encode("123456"));
            prof.setActive(true);
            professionalRepository.save(prof);
            return "Conta já existia. Senha atualizada para: 123456";
        }

        var tenant = tenantRepository.findBySlug(testSlug).orElseGet(() -> {
            var t = br.com.davidds5.manicure_api.entity.TenantEntity.builder()
                    .name("Studio Bella Nails")
                    .slug(testSlug)
                    .plan(br.com.davidds5.manicure_api.enums.TenantPlan.PRO)
                    .status(br.com.davidds5.manicure_api.enums.TenantStatus.ACTIVE)
                    .brandColor("#ec4899")
                    .build();
            return tenantRepository.save(t);
        });

        var owner = br.com.davidds5.manicure_api.entity.ProfessionalEntity.builder()
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

        if (serviceRepository.count() == 0) {
            serviceRepository.save(br.com.davidds5.manicure_api.entity.ServiceEntity.builder()
                    .tenantId(tenant.getId())
                    .name("Alongamento em Fibra de Vidro")
                    .description("Alongamento completo com fibra de vidro")
                    .price(140.0)
                    .duration(60)
                    .active(true)
                    .build());

            serviceRepository.save(br.com.davidds5.manicure_api.entity.ServiceEntity.builder()
                    .tenantId(tenant.getId())
                    .name("Esmaltação em Gel")
                    .description("Esmaltação de alta durabilidade em gel")
                    .price(75.0)
                    .duration(45)
                    .active(true)
                    .build());
        }

        return "Conta de teste criada com sucesso! E-mail: " + testEmail + " | Senha: 123456";
    }

    @GetMapping("/test-log")
    public String testLog() {
        log.trace("TRACE log");
        log.debug("DEBUG log");
        log.info("INFO log");
        log.warn("WARN log");
        log.error("ERROR log");
        return "Logs funcionando!";
    }
}

