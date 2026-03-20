package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.data.AppointmentData;
import br.com.davidds5.manicure_api.dto.*;
import br.com.davidds5.manicure_api.entity.*;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.AppointmentMapper;
import br.com.davidds5.manicure_api.repository.*;
import br.com.davidds5.manicure_api.util.Constants;
import br.com.davidds5.manicure_api.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.davidds5.manicure_api.util.DateUtil;
import br.com.davidds5.manicure_api.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service

public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentData appointmentData; // ✅ Substitui Repository

    public AppointmentService(AppointmentRepository appointmentRepository, ClientRepository clientRepository, ProfessionalRepository professionalRepository, ServiceRepository serviceRepository, AppointmentMapper appointmentMapper, AppointmentData appointmentData) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentMapper = appointmentMapper;
        this.appointmentData = appointmentData;
    }


    private void validateTimeConflict(Long professionalId, LocalDateTime dateTime, Long appointmentId) {
        List<AppointmentEntity> conflicts = appointmentData
                .findByProfessionalIdAndDateTime(professionalId, dateTime);
        // ...
    }

    // ================= CREATE =================
    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreateDTO dto) {
        log.info("Criando novo agendamento");

        ClientEntity client = getClient(dto.getClientId());
        ProfessionalEntity professional = getProfessional(dto.getProfessionalId());
        ServiceEntity service = getService(dto.getServiceId());

        validateFutureDate(dto.getDateTime());
        validateTimeConflict(professional.getId(), dto.getDateTime(), null);

        AppointmentEntity entity = appointmentMapper.toEntity(dto, client, professional, service);
        AppointmentEntity saved = appointmentRepository.save(entity);

        log.info("Agendamento criado com ID: {}", saved.getId());
        return appointmentMapper.toDTO(saved);
    }

    // ================= FIND =================
    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        return appointmentMapper.toDTO(getAppointment(id));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByClientId(Long clientId, Pageable pageable) {
        return appointmentRepository.findByClientId(clientId, pageable)
                .map(appointmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByProfessionalId(Long professionalId, Pageable pageable) {
        return appointmentRepository.findByProfessionalId(professionalId, pageable)
                .map(appointmentMapper::toDTO);
    }

    // ================= UPDATE =================
    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        log.info("Atualizando agendamento ID: {}", id);

        AppointmentEntity existing = getAppointment(id);

        validateNotCompleted(existing);

        if (dto.getDateTime() != null) {
            validateFutureDate(dto.getDateTime());
            validateTimeConflict(existing.getProfessional().getId(), dto.getDateTime(), id);
            existing.setDateTime(dto.getDateTime());
        }

        if (dto.getStatus() != null) {
            validateStatusTransition(existing.getStatus(), dto.getStatus());
            existing.setStatus(dto.getStatus());
        }

        return appointmentMapper.toDTO(appointmentRepository.save(existing));
    }

    // ================= CANCEL =================
    @Transactional
    public void cancelAppointment(Long id) {
        log.info("Cancelando agendamento ID: {}", id);

        AppointmentEntity existing = getAppointment(id);

        if (existing.getStatus() == AppointmentEntity.AppointmentStatus.CANCELLED ||
                existing.getStatus() == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Agendamento não pode ser cancelado neste estado");
        }

        if (existing.getDateTime().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BusinessException("Cancelamento só permitido com 2 horas de antecedência");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CANCELLED);
        appointmentRepository.save(existing);
    }

    // ================= CONFIRM =================

    @Transactional
    public AppointmentDTO confirmAppointment(Long id) {
        log.info("Confirmando agendamento ID: {}", id);

        AppointmentEntity existing = getAppointment(id);

        if (existing.getStatus() != AppointmentEntity.AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Apenas agendamentos 'SCHEDULED' podem ser confirmados");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CONFIRMED);
        appointmentRepository.save(existing);

        return appointmentMapper.toDTO(existing);
    }
    // ================= PRIVATE METHODS =================

    private AppointmentEntity getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + id));
    }

    private ClientEntity getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    private ProfessionalEntity getProfessional(Long id) {
        ProfessionalEntity professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));

        if (!professional.getActive()) {
            throw new BusinessException("Profissional não está ativo");
        }

        return professional;
    }

    private ServiceEntity getService(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));
    }

    private void validateFutureDate(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data e hora devem ser futuras");
        }
    }

    private void validateNotCompleted(AppointmentEntity appointment) {
        if (appointment.getStatus() == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Não é possível alterar agendamento concluído");
        }
    }

    private void validateTimeConflict(Long professionalId, LocalDateTime dateTime, Long appointmentId) {
        List<AppointmentEntity> existingAppointments =
                appointmentRepository.findByProfessionalAndDateTime(professionalId, dateTime);

        boolean conflict = existingAppointments.stream()
                .anyMatch(a -> appointmentId == null || !a.getId().equals(appointmentId));

        if (conflict) {
            throw new BusinessException("Horário já reservado para este profissional");
        }
    }

    private void validateStatusTransition(AppointmentEntity.AppointmentStatus current,
                                          AppointmentEntity.AppointmentStatus next) {

        if (current == AppointmentEntity.AppointmentStatus.CANCELLED ||
                current == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Não é possível alterar um agendamento finalizado");
        }

        if (current == AppointmentEntity.AppointmentStatus.SCHEDULED &&
                next == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Agendamento precisa ser confirmado antes de concluir");
        }
    }
    // AppointmentService.java

    @Transactional
    public void cancelAppointment(Long id) {
        AppointmentEntity existing = getAppointment(id);

        if (!DateUtil.canCancel(existing.getDateTime())) {
            throw new BusinessException("Cancelamento só com " + Constants.CANCEL_HOURS_AHEAD + "h de antecedência");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CANCELLED);
    }
}