package br.com.davidds5.manicure_api.service;
import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
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

@Slf4j
@Service
@RequiredArgsConstructor
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

        log.info("Serviço criado com ID: {}", saved.getId());
        return serviceMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ServiceDTO findById(Long id) {
        log.info("Buscando serviço por ID: {}", id);
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));
        return serviceMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<ServiceDTO> findAll() {
        log.info("Listando todos os serviços");
        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toDTO)
                .toList();
    }

    @Transactional
    public ServiceDTO updateService(Long id, ServiceCreateDTO dto) {
        log.info("Atualizando serviço ID: {}", id);

        ServiceEntity existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setDuration(dto.getDuration());

        ServiceEntity updated = serviceRepository.save(existing);
        log.info("Serviço atualizado com ID: {}", updated.getId());
        return serviceMapper.toDTO(updated);
    }

    @Transactional
    public void deleteService(Long id) {
        log.info("Deletando serviço ID: {}", id);

        ServiceEntity existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));

        serviceRepository.delete(existing);
        log.info("Serviço deletado com ID: {}", id);
    }
}