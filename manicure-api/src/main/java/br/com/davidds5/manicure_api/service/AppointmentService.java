package br.com.davidds5.manicure_api.service;



import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.ClientRepository;
import br.com.davidds5.manicure_api.repository.ProfessionalRepository;
import br.com.davidds5.manicure_api.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreateDTO dto) {
        log.info("Criando novo agendamento");

        // 1. Verifica se cliente existe
        ClientEntity client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + dto.getClientId()));

        // 2. Verifica se profissional existe
        ProfessionalEntity professional = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + dto.getProfessionalId()));

        // 3. Verifica se profissional está ativo
        if (!professional.getActive()) {
            throw new BusinessException("Profissional não está ativo");
        }

        // 4. Verifica se serviço existe
        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + dto.getServiceId()));

        // 5. Verifica se data é futura
        if (dto.getDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data e hora devem ser futuras");
        }

        // 6. Verifica se horário está ocupado (REGRAS DE NEGÓCIO CRÍTICA)
        List<AppointmentEntity> existingAppointments = appointmentRepository
                .findByProfessionalAndDateTime(dto.getProfessionalId(), dto.getDateTime());

        if (!existingAppointments.isEmpty()) {
            throw new BusinessException("Horário já reservado para este profissional");
        }

        // 7. Cria o agendamento
        AppointmentEntity entity = appointmentMapper.toEntity(dto, client, professional, service);
        AppointmentEntity saved = appointmentRepository.save(entity);

        log.info("Agendamento criado com ID: {}", saved.getId());
        return appointmentMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        log.info("Buscando agendamento por ID: {}", id);
        AppointmentEntity entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + id));
        return appointmentMapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findAll(Pageable pageable) {
        log.info("Listando todos os agendamentos com paginação");
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByClientId(Long clientId, Pageable pageable) {
        log.info("Buscando agendamentos do cliente ID: {}", clientId);
        return appointmentRepository.findByClientId(clientId, pageable)
                .map(appointmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByProfessionalId(Long professionalId, Pageable pageable) {
        log.info("Buscando agendamentos do profissional ID: {}", professionalId);
        return appointmentRepository.findByProfessionalId(professionalId, pageable)
                .map(appointmentMapper::toDTO);
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        log.info("Atualizando agendamento ID: {}", id);

        AppointmentEntity existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + id));

        // Verifica se não pode alterar data de agendamento concluído
        if (existing.getStatus() == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Não é possível alterar agendamento concluído");
        }

        // Atualiza campos permitidos
        if (dto.getDateTime() != null) {
            if (dto.getDateTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException("Data e hora devem ser futuras");
            }

            // Verifica novamente se horário está livre
            List<AppointmentEntity> existingAppointments = appointmentRepository
                    .findByProfessionalAndDateTime(existing.getProfessional().getId(), dto.getDateTime());

            if (!existingAppointments.isEmpty() && !existingAppointments.stream()
                    .anyMatch(a -> a.getId().equals(id))) {
                throw new BusinessException("Horário já reservado para este profissional");
            }

            existing.setDateTime(dto.getDateTime());
        }

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        AppointmentEntity updated = appointmentRepository.save(existing);
        log.info("Agendamento atualizado com ID: {}", updated.getId());
        return appointmentMapper.toDTO(updated);
    }

    @Transactional