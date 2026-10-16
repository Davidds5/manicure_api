package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.dto.ServiceUpdateDTO;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ServiceMapper;
import br.com.davidds5.manicure_api.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final br.com.davidds5.manicure_api.repository.TenantRepository tenantRepository;

    @Transactional
    public ServiceDTO createService(ServiceCreateDTO dto) {
        log.info("Criando novo serviço: {}", dto.getName());

        if (dto.getPrice() == null || dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }

        if (dto.getDuration() <= 0) {
            throw new BusinessException("Duração deve ser maior que zero");
        }

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null) {
            throw new BusinessException("Contexto de salão (tenant) obrigatório para cadastrar serviço.");
        }

        ServiceEntity entity = serviceMapper.toEntity(dto);
        entity.setTenantId(currentTenantId);
        ServiceEntity saved = serviceRepository.save(entity);

        return serviceMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ServiceDTO findById(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(entity.getTenantId())) {
            throw new ResourceNotFoundException("Serviço não encontrado com ID: " + id);
        }

        return serviceMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<ServiceDTO> findAll() {
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null) {
            throw new BusinessException("Contexto de salão (tenant) obrigatório.");
        }

        return serviceRepository.findByTenantId(currentTenantId)
                .stream()
                .map(serviceMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceDTO> findByTenantSlug(String tenantSlug) {
        var tenant = tenantRepository.findBySlugAndDeletedAtIsNull(tenantSlug.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Salão não encontrado com o slug: " + tenantSlug));

        if (tenant.getStatus() == br.com.davidds5.manicure_api.enums.TenantStatus.SUSPENDED) {
            throw new ResourceNotFoundException("Salão não encontrado ou suspenso: " + tenantSlug);
        }

        return serviceRepository.findByTenantId(tenant.getId())
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .map(serviceMapper::toDTO)
                .toList();
    }

    @Transactional
    public ServiceDTO updateService(Long id, ServiceUpdateDTO dto) {

        ServiceEntity existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(existing.getTenantId())) {
            throw new ResourceNotFoundException("Serviço não encontrado");
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null && dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }

        if (dto.getDuration() != null && dto.getDuration() <= 0) {
            throw new BusinessException("Duração deve ser maior que zero");
        }

        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }

        if (dto.getDuration() != null) {
            existing.setDuration(dto.getDuration());
        }

        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }

        ServiceEntity updated = serviceRepository.save(existing);

        return serviceMapper.toDTO(updated);
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId == null || !currentTenantId.equals(entity.getTenantId())) {
            throw new ResourceNotFoundException("Serviço não encontrado");
        }

        serviceRepository.delete(entity);
    }
}