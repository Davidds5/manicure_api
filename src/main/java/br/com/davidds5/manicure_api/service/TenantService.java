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

        // Criar assinatura padrão para o Tenant
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

    @Transactional(readOnly = true)
    public TenantDetailsDTO getMyTenant() {
        Long tenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Contexto de tenant não encontrado na sessão do usuário.");
        }

        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Salão/Tenant não encontrado com ID: " + tenantId));

        var subscription = subscriptionService.createDefaultSubscription(tenant.getId(), tenant.getPlan());

        return new TenantDetailsDTO(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getPlan(),
                tenant.getStatus(),
                tenant.getLogoUrl(),
                tenant.getBrandColor(),
                tenant.getOwnerId(),
                tenant.getCreatedAt(),
                subscription.getStatus(),
                subscription.getMaxProfessionals(),
                subscription.getMaxAppointmentsPerMonth(),
                subscription.getNextBillingAt()
        );
    }

    @Transactional
    public TenantResponseDTO updateMyTenant(TenantUpdateDTO dto) {
        Long tenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Contexto de tenant não encontrado na sessão do usuário.");
        }

        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Salão/Tenant não encontrado com ID: " + tenantId));

        if (dto.name() != null && !dto.name().isBlank()) {
            tenant.setName(dto.name().trim());
        }
        if (dto.logoUrl() != null) {
            tenant.setLogoUrl(dto.logoUrl().trim());
        }
        if (dto.brandColor() != null && !dto.brandColor().isBlank()) {
            tenant.setBrandColor(dto.brandColor().trim());
        }

        TenantEntity updated = tenantRepository.save(tenant);

        return new TenantResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getSlug(),
                updated.getPlan(),
                updated.getStatus(),
                updated.getLogoUrl(),
                updated.getBrandColor(),
                updated.getOwnerId(),
                updated.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<TenantResponseDTO> listAllTenants(org.springframework.data.domain.Pageable pageable) {
        return tenantRepository.findAll(pageable)
                .map(t -> new TenantResponseDTO(
                        t.getId(),
                        t.getName(),
                        t.getSlug(),
                        t.getPlan(),
                        t.getStatus(),
                        t.getLogoUrl(),
                        t.getBrandColor(),
                        t.getOwnerId(),
                        t.getCreatedAt()
                ));
    }

    @Transactional
    public TenantResponseDTO updateTenantStatus(Long id, TenantStatus status) {
        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salão/Tenant não encontrado com ID: " + id));

        tenant.setStatus(status);
        TenantEntity updated = tenantRepository.save(tenant);

        return new TenantResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getSlug(),
                updated.getPlan(),
                updated.getStatus(),
                updated.getLogoUrl(),
                updated.getBrandColor(),
                updated.getOwnerId(),
                updated.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public PlatformMetricsDTO getPlatformMetrics() {
        var all = tenantRepository.findAll();
        long total = all.size();
        long active = all.stream().filter(t -> t.getStatus() == TenantStatus.ACTIVE).count();
        long trial = all.stream().filter(t -> t.getStatus() == TenantStatus.TRIAL).count();
        long suspended = all.stream().filter(t -> t.getStatus() == TenantStatus.SUSPENDED).count();

        // Cálculo de MRR hipotético: PRO = R$ 99/mês, ENTERPRISE = R$ 249/mês
        double mrr = all.stream()
                .filter(t -> t.getStatus() == TenantStatus.ACTIVE)
                .mapToDouble(t -> switch (t.getPlan()) {
                    case PRO -> 99.00;
                    case ENTERPRISE -> 249.00;
                    default -> 0.00;
                })
                .sum();

        return new PlatformMetricsDTO(total, active, trial, suspended, mrr);
    }
}
