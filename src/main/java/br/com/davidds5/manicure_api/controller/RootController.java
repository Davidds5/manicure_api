package br.com.davidds5.manicure_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RootController {

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private br.com.davidds5.manicure_api.repository.TenantRepository tenantRepository;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private br.com.davidds5.manicure_api.repository.ProfessionalRepository professionalRepository;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private br.com.davidds5.manicure_api.repository.ServiceRepository serviceRepository;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private br.com.davidds5.manicure_api.service.SubscriptionService subscriptionService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "status", "ONLINE",
                "service", "BelasUnhas SaaS API",
                "version", "1.0.0"
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/seed-demo")
    public Map<String, Object> seedDemo() {
        String testEmail = "teste@salao.com";
        String testSlug = "studio-bella";

        try {
            var profOpt = professionalRepository.findByEmail(testEmail);
            if (profOpt.isPresent()) {
                var prof = profOpt.get();
                prof.setPassword(passwordEncoder.encode("123456"));
                prof.setActive(true);
                professionalRepository.save(prof);
                return Map.of("status", "SUCCESS", "message", "Conta demo já existia. Senha atualizada para 123456");
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

            if (subscriptionService != null) {
                subscriptionService.createDefaultSubscription(tenant.getId(), tenant.getPlan());
            }

            if (serviceRepository != null && serviceRepository.count() == 0) {
                serviceRepository.save(br.com.davidds5.manicure_api.entity.ServiceEntity.builder()
                        .tenantId(tenant.getId())
                        .name("Alongamento em Fibra de Vidro")
                        .description("Alongamento completo com fibra de vidro")
                        .price(140.0)
                        .duration(60)
                        .active(true)
                        .build());
            }

            return Map.of("status", "SUCCESS", "message", "Conta demo criada com sucesso! Email: teste@salao.com | Senha: 123456");
        } catch (Exception e) {
            return Map.of("status", "ERROR", "message", e.getMessage());
        }
    }
}
