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
    private final AppointmentData appointmentData;

    // ================= PRIVATE HELPERS =================

    private AppointmentEntity getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado: " + id));
    }

    private ClientEntity getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }

    private ProfessionalEntity getProfessional(Long id) {
        ProfessionalEntity professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado: " + id));

        if (!professional.getActive()) {
            throw new BusinessException("Profissional inativo");
        }

        return professional;
    }

    private ServiceEntity getService(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
    }

    private void validateFutureDate(LocalDateTime dateTime) {
        if (!DateUtil.isFuture(dateTime)) {
            throw new BusinessException(Constants.FUTURE_DATE);
        }
    }

    private void validateNotCompleted(AppointmentEntity appointment) {
        if (appointment.getStatus() == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Agendamento concluído não pode ser alterado");
        }
    }

    private void validateTimeConflict(Long professionalId, LocalDateTime dateTime, Long excludeId) {
        List<AppointmentEntity> conflicts = appointmentData
                .findByProfessionalIdAndDateTime(professionalId, dateTime);

        boolean hasConflict = conflicts.stream()
                .anyMatch(a -> excludeId == null || !a.getId().equals(excludeId));

        if (hasConflict) {
            throw new BusinessException(Constants.TIME_CONFLICT);
        }
    }

    private void validateStatusTransition(AppointmentEntity.AppointmentStatus current,
                                          AppointmentEntity.AppointmentStatus next) {

        if (current == AppointmentEntity.AppointmentStatus.CANCELLED ||
                current == AppointmentEntity.AppointmentStatus.COMPLETED) {
            throw new BusinessException("Status finalizado não pode ser alterado");
        }
    }

    // ================= CREATE =================

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreateDTO dto) {

        // 1. Buscar entidades relacionadas
        ClientEntity client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        ProfessionalEntity professional = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        // 2. Criar entidade (AQUI entra o trecho que você perguntou)
        AppointmentEntity entity = new AppointmentEntity();

        entity.setClient(client);
        entity.setProfessional(professional);
        entity.setService(service);
        entity.setStatus(AppointmentEntity.AppointmentStatus.SCHEDULED);

        // se tiver data:
        entity.setDateTime(dto.getDateTime());

        // 3. Salvar
        AppointmentEntity saved = appointmentRepository.save(entity);

        // 4. Converter pra DTO
        return appointmentMapper.toDTO(saved);
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        log.info("Atualizando agendamento {}", id);

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

    // ================= CANCEL/CONFIRM =================

    @Transactional
    public void cancelAppointment(Long id) {
        log.info("Cancelando agendamento {}", id);

        AppointmentEntity existing = getAppointment(id);
        validateNotCompleted(existing);

        if (!DateUtil.canCancel(existing.getDateTime())) {
            throw new BusinessException("Cancelamento só com " + Constants.CANCEL_HOURS_AHEAD + "h antecedência");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CANCELLED);
        appointmentRepository.save(existing);
    }

    @Transactional
    public AppointmentDTO confirmAppointment(Long id) {
        log.info("Confirmando agendamento {}", id);

        AppointmentEntity existing = getAppointment(id);

        if (existing.getStatus() != AppointmentEntity.AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Apenas SCHEDULED pode ser confirmado");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CONFIRMED);
        return appointmentMapper.toDTO(appointmentRepository.save(existing));
    }

    // ================= FIND =================

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        log.debug("Buscando agendamento {}", id);
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
}