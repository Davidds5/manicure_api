package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.ProfessionalMapper;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.util.Constants;
import br.com.davidds5.manicure_api.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalMapper professionalMapper;

    @Transactional
    public ProfessionalDTO createProfessional(ProfessionalCreatedDTO dto) {
        log.info("Criando novo profissional: {}", dto.getNome());
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
        List<ProfessionalDTO> collect = professionalRepository.findByActiveTrue()
                .stream()
                .map(professionalMapper::toDTO)
                .collect(Collectors.toList());
        return collect; // compatível com Java 8+
    }

    @Transactional
    public ProfessionalDTO updateProfessional(Long id, ProfessionalCreatedDTO dto) {
        log.info("Atualizando profissional ID: {}", id);

        ProfessionalEntity existing = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        existing.setName(dto.getNome());
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