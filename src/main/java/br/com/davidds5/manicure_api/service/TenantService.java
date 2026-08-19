package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.TenantSignupDTO;
import br.com.davidds5.manicure_api.dto.TenantResponseDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.TenantEntity;
import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public TenantResponseDTO signup(TenantSignupDTO dto) {
        String slug = dto.slug().toLowerCase().trim();

        if (tenantRepository.existsBySlug(slug)) {
            throw new BusinessException("Já existe um salão cadastrado com este slug/endereço.");
        }

        if (professionalRepository.findByEmail(dto.ownerEmail()).isPresent()) {
            throw new BusinessException("Já existe um usuário/profissional cadastrado com este e-mail.");
        }

        TenantEntity tenant = TenantEntity.builder()
                .name(dto.salonName())
                .slug(slug)
                .plan(TenantPlan.FREE)
                .status(TenantStatus.TRIAL)
                .logoUrl(dto.logoUrl())
                .brandColor(dto.brandColor() != null && !dto.brandColor().isBlank() ? dto.brandColor() : "#000000")
                .build();
        tenant = tenantRepository.save(tenant);

        ProfessionalEntity owner = ProfessionalEntity.builder()
                .tenantId(tenant.getId())
                .name(dto.ownerName())
                .email(dto.ownerEmail())
                .password(passwordEncoder.encode(dto.ownerPassword()))
                .specialty(dto.specialty() != null && !dto.specialty().isBlank() ? dto.specialty() : "Proprietário")
                .active(true)
                .build();
        owner = professionalRepository.save(owner);

        tenant.setOwnerId(owner.getId());
        tenant = tenantRepository.save(tenant);

        // 6. Criar assinatura padrão para o Tenant
        subscriptionService.createDefaultSubscription(tenant.getId(), tenant.getPlan());

        return new TenantResponseDTO(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getPlan(),
                tenant.getStatus(),
                tenant.getLogoUrl(),
                tenant.getBrandColor(),
                tenant.getOwnerId(),
                tenant.getCreatedAt()
        );
    }
}
