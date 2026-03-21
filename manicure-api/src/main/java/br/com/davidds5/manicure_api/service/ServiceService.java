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

    @Transactional
    public ServiceDTO createService(ServiceCreateDTO dto) {
        log.info("Criando novo serviço: {}", dto.getName());

        if (dto.getPrice() <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }

        if (dto.getDuration() <= 0) {
            throw new BusinessException("Duração deve ser maior que zero");
        }

        ServiceEntity entity = serviceMapper.toEntity(dto);
        ServiceEntity saved = serviceRepository.save(entity);

        return serviceMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ServiceDTO findById(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));

        return serviceMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<ServiceDTO> findAll() {
        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toDTO)
                .toList();
    }

    @Transactional
    public ServiceDTO updateService(Long id, ServiceUpdateDTO dto) {

        ServiceEntity existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null && dto.getPrice() <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }

        if (dto.getDurationMinutes() != null && dto.getDurationMinutes() <= 0) {
            throw new BusinessException("Duração deve ser maior que zero");
        }

        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }

        if (dto.getDurationMinutes() != null) {
            existing.setDurationMinutes(dto.getDurationMinutes());
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

        serviceRepository.delete(entity);
    }
}