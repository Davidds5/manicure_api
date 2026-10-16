package br.com.davidds5.manicure_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ProfessionalMapper;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalMapper professionalMapper;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;
    private final br.com.davidds5.manicure_api.repository.TenantRepository tenantRepository;

    @CacheEvict(value = "professionals", allEntries = true)
    @Transactional
    public ProfessionalDTO createProfessional(ProfessionalCreatedDTO dto) {
        log.info("Criando novo profissional: {}", dto.getName());

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null) {
            throw new BusinessException("Contexto de salão (tenant) obrigatório para cadastrar profissional.");
        }
        subscriptionService.validateProfessionalLimit(currentTenantId);

        ProfessionalEntity entity = professionalMapper.toEntity(dto);
        entity.setTenantId(currentTenantId);
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        } else {
            entity.setRole(br.com.davidds5.manicure_api.enums.ProfessionalRole.PROFESSIONAL);
        }
        
        String senhaCriptografada = passwordEncoder.encode(dto.getPassword());
        entity.setPassword(senhaCriptografada);

        ProfessionalEntity saved = professionalRepository.save(entity);
        log.info("Profissional criado com ID: {}", saved.getId());
        return professionalMapper.toDTO(saved);
    }
    
    @Cacheable(value = "professionals", key = "(T(br.com.davidds5.manicure_api.config.TenantContext).getTenantId() != null ? T(br.com.davidds5.manicure_api.config.TenantContext).getTenantId().toString() : 'global') + ':' + #id")
    @Transactional(readOnly = true)
    public ProfessionalDTO findById(Long id) {
        log.info("Buscando profissional por ID: {}", id);
        ProfessionalEntity entity = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(entity.getTenantId())) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + id);
        }

        return professionalMapper.toDTO(entity);
    }

    @Cacheable(value = "professionals", key = "(T(br.com.davidds5.manicure_api.config.TenantContext).getTenantId() != null ? T(br.com.davidds5.manicure_api.config.TenantContext).getTenantId().toString() : 'global') + ':active'")
    @Transactional(readOnly = true)
    public List<ProfessionalDTO> findAllActive() {
        log.info("Listando todos os profissionais ativos");
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null) {
            throw new BusinessException("Contexto de salão (tenant) obrigatório.");
        }

        List<ProfessionalEntity> professionals = professionalRepository.findByTenantIdAndActiveTrue(currentTenantId);

        List<ProfessionalDTO> collect = professionals
                .stream()
                .map(professionalMapper::toDTO)
                .collect(Collectors.toList());
        return collect;
    }

    @Transactional(readOnly = true)
    public List<ProfessionalDTO> findByTenantSlug(String tenantSlug) {
        var tenant = tenantRepository.findBySlugAndDeletedAtIsNull(tenantSlug.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Salão não encontrado com o slug: " + tenantSlug));

        if (tenant.getStatus() == br.com.davidds5.manicure_api.enums.TenantStatus.SUSPENDED) {
            throw new ResourceNotFoundException("Salão não encontrado ou suspenso: " + tenantSlug);
        }

        return professionalRepository.findByTenantIdAndActiveTrue(tenant.getId())
                .stream()
                .map(professionalMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "professionals", allEntries = true)
    @Transactional
    public ProfessionalDTO updateProfessional(Long id, ProfessionalCreatedDTO dto) {
        log.info("Atualizando profissional ID: {}", id);

        ProfessionalEntity existing = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(existing.getTenantId())) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + id);
        }

        existing.setName(dto.getName());
        existing.setSpecialty(dto.getSpecialty());
        existing.setActive(dto.isActive());
        if (dto.getRole() != null) {
            existing.setRole(dto.getRole());
        }

        ProfessionalEntity updated = professionalRepository.save(existing);
        log.info("Profissional atualizado com ID: {}", updated.getId());
        return professionalMapper.toDTO(updated);
    }

    @CacheEvict(value = "professionals", allEntries = true)
    @Transactional
    public void deleteProfessional(Long id) {
        log.info("Deletando profissional ID: {}", id);

        ProfessionalEntity existing = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(existing.getTenantId())) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + id);
        }

        professionalRepository.delete(existing);
        log.info("Profissional deletado com ID: {}", id);
    }
}