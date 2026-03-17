package br.com.davidds5.manicure_api.service;


import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ProfessionalMapper;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final ProfessionalMapper professionalMapper;

    @Transactional
    public ProfessionalDTO createProfessional(ProfessionalCreatedDTO dto) {
        log.info("Criando novo profissional: {}", dto.getName());

        ProfessionalEntity entity = professionalMapper.toEntity(dto);
        ProfessionalEntity saved = professionalRepository.save(entity);

        log.info("Profissional criado com ID: {}", saved.getId());
        return professionalMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ProfessionalDTO findById(Long id) {
        log.info("Buscando profissional por ID: {}", id);
        ProfessionalEntity entity = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));
        return professionalMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalDTO> findAllActive() {
        log.info("Listando todos os profissionais ativos");
        return professionalRepository.findByActiveTrue()
                .stream()
                .map(professionalMapper::toDTO)
                .toList();
    }

    @Transactional
    public ProfessionalDTO updateProfessional(Long id, ProfessionalCreatedDTO dto) {
        log.info("Atualizando profissional ID: {}", id);

        ProfessionalEntity existing = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        existing.setName(dto.getName());
        existing.setSpecialty(dto.getSpecialty());
        existing.setActive(dto.isActive());

        ProfessionalEntity updated = professionalRepository.save(existing);
        log.info("Profissional atualizado com ID: {}", updated.getId());
        return professionalMapper.toDTO(updated);
    }

    @Transactional
    public void deleteProfessional(Long id) {
        log.info("Deletando profissional ID: {}", id);

        ProfessionalEntity existing = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        professionalRepository.delete(existing);
        log.info("Profissional deletado com ID: {}", id);
    }
}