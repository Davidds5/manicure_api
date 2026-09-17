package br.com.davidds5.manicure_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
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

    @CacheEvict(value = "professionals", allEntries = true)
    @Transactional
    public ProfessionalDTO createProfessional(ProfessionalCreatedDTO dto) {
        log.info("Criando novo profissional: {}", dto.getName());

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        subscriptionService.validateProfessionalLimit(currentTenantId);

        ProfessionalEntity entity = professionalMapper.toEntity(dto);
        if (currentTenantId != null) {
            entity.setTenantId(currentTenantId);
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
        if (currentTenantId != null && (entity.getTenantId() == null || !currentTenantId.equals(entity.getTenantId()))) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + id);
        }

        return professionalMapper.toDTO(entity);
    }

    @Cacheable(value = "professionals", key = "(T(br.com.davidds5.manicure_api.config.TenantContext).getTenantId() != null ? T(br.com.davidds5.manicure_api.config.TenantContext).getTenantId().toString() : 'global') + ':active'")
    @Transactional(readOnly = true)
    public List<ProfessionalDTO> findAllActive() {
        log.info("Listando todos os profissionais ativos");
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        List<ProfessionalEntity> professionals;
        if (currentTenantId != null) {
            professionals = professionalRepository.findByTenantIdAndActiveTrue(currentTenantId);
        } else {
            professionals = professionalRepository.findByActiveTrue();
        }

        List<ProfessionalDTO> collect = professionals
                .stream()
                .map(professionalMapper::toDTO)
                .collect(Collectors.toList());
        log.debug("Fui no Banco de dados");
        return collect; 
    }

    @CacheEvict(value = "professionals", allEntries = true)
    @Transactional
    public ProfessionalDTO updateProfessional(Long id, ProfessionalCreatedDTO dto) {
        log.info("Atualizando profissional ID: {}", id);

        ProfessionalEntity existing = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId != null && (existing.getTenantId() == null || !currentTenantId.equals(existing.getTenantId()))) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + id);
        }

        existing.setName(dto.getName());
        existing.setSpecialty(dto.getSpecialty());
        existing.setActive(dto.isActive());

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
        if (currentTenantId != null && (existing.getTenantId() == null || !currentTenantId.equals(existing.getTenantId()))) {
            throw new ResourceNotFoundException("Profissional não encontrado com ID: " + id);
        }

        professionalRepository.delete(existing);
        log.info("Profissional deletado com ID: {}", id);
    }
}