package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.data.AppointmentData;
import br.com.davidds5.manicure_api.dto.*;
import br.com.davidds5.manicure_api.entity.*;
import br.com.davidds5.manicure_api.event.AppointmentCreatedEvent;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;

import br.com.davidds5.manicure_api.repository.*;
import br.com.davidds5.manicure_api.util.Constants;
import br.com.davidds5.manicure_api.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final ApplicationEventPublisher eventPublisher;
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final SubscriptionService subscriptionService;
    private final AppointmentData appointmentData;
    private final MeterRegistry meterRegistry;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // ================= PRIVATE HELPERS =================

    private AppointmentEntity getAppointment(Long id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado: " + id));
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId != null && (appointment.getTenantId() == null || !currentTenantId.equals(appointment.getTenantId()))) {
            throw new ResourceNotFoundException("Agendamento não encontrado: " + id);
        }
        return appointment;
    }

    private ClientEntity getClient(Long id) {
        ClientEntity client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId != null && (client.getTenantId() == null || !currentTenantId.equals(client.getTenantId()))) {
            throw new ResourceNotFoundException("Cliente não encontrado: " + id);
        }
        return client;
    }

    private ProfessionalEntity getProfessional(Long id) {
        ProfessionalEntity professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado: " + id));
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId != null && (professional.getTenantId() == null || !currentTenantId.equals(professional.getTenantId()))) {
            throw new ResourceNotFoundException("Profissional não encontrado: " + id);
        }

        if (!professional.getActive()) {
            throw new BusinessException("Profissional inativo");
        }

        return professional;
    }

    private ServiceEntity getService(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        if (currentTenantId != null && (service.getTenantId() == null || !currentTenantId.equals(service.getTenantId()))) {
            throw new ResourceNotFoundException("Serviço não encontrado: " + id);
        }
        return service;
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

    private void validateTimeConflict(Long professionalId, LocalDateTime dateTime, Integer durationMinutes, Long excludeId) {
        int duration = (durationMinutes != null && durationMinutes > 0) ? durationMinutes : 30;
        LocalDateTime newStart = dateTime;
        LocalDateTime newEnd = newStart.plusMinutes(duration);

        LocalDateTime searchStart = dateTime.toLocalDate().minusDays(1).atStartOfDay();
        LocalDateTime searchEnd = dateTime.toLocalDate().plusDays(1).atTime(23, 59, 59);

        List<AppointmentEntity> appointments = appointmentRepository
                .findActiveByProfessionalAndDateRange(professionalId, searchStart, searchEnd, AppointmentEntity.AppointmentStatus.CANCELLED);

        boolean hasConflict = appointments.stream()
                .filter(a -> excludeId == null || !a.getId().equals(excludeId))
                .filter(a -> a.getStatus() != AppointmentEntity.AppointmentStatus.CANCELLED)
                .anyMatch(a -> {
                    LocalDateTime existingStart = a.getDateTime();
                    int existingDuration = (a.getService() != null && a.getService().getDuration() != null && a.getService().getDuration() > 0)
                            ? a.getService().getDuration()
                            : 30;
                    LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);

                    return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                });

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

    private void validateTenantScope(Long currentTenantId, ClientEntity client, ProfessionalEntity professional, ServiceEntity service) {
        if (currentTenantId != null) {
            if (client.getTenantId() == null || !currentTenantId.equals(client.getTenantId())) {
                throw new ResourceNotFoundException("Cliente não encontrado: " + client.getId());
            }
            if (professional.getTenantId() == null || !currentTenantId.equals(professional.getTenantId())) {
                throw new ResourceNotFoundException("Profissional não encontrado: " + professional.getId());
            }
            if (service.getTenantId() == null || !currentTenantId.equals(service.getTenantId())) {
                throw new ResourceNotFoundException("Serviço não encontrado: " + service.getId());
            }
        } else {
            Long clientTenant = client.getTenantId();
            Long profTenant = professional.getTenantId();
            Long servTenant = service.getTenantId();

            if (clientTenant == null || profTenant == null || !clientTenant.equals(profTenant)) {
                throw new BusinessException("Cruzamento de dados: cliente e profissional pertencem a salões distintos.");
            }
            if (servTenant == null || !profTenant.equals(servTenant)) {
                throw new BusinessException("Cruzamento de dados: profissional e serviço pertencem a salões distintos.");
            }
        }
    }

    // ================= CREATE =================

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreateDTO dto) {
        
        return meterRegistry.timer("api_appointment_creation_time").record(()-> {
            
        log.info("Criando agendamento");

        Long currentTenantId = br.com.davidds5.manicure_api.config.TenantContext.getTenantId();
        subscriptionService.validateAppointmentLimit(currentTenantId);

        ClientEntity client = getClient(dto.getClientId());
        ProfessionalEntity professional = getProfessional(dto.getProfessionalId());
        ServiceEntity service = getService(dto.getServiceId());

        validateTenantScope(currentTenantId, client, professional, service);

        validateFutureDate(dto.getDateTime());
        validateTimeConflict(professional.getId(), dto.getDateTime(), service.getDuration(), null);

        AppointmentEntity entity = new AppointmentEntity();

        if (currentTenantId != null) {
            entity.setTenantId(currentTenantId);
        } else {
            entity.setTenantId(professional.getTenantId());
        }

        entity.setClient(client);
        entity.setProfessional(professional);
        entity.setService(service);
        entity.setDateTime(dto.getDateTime());
        entity.setStatus(AppointmentEntity.AppointmentStatus.SCHEDULED);

        AppointmentEntity saved = appointmentRepository.save(entity);

        eventPublisher.publishEvent(new AppointmentCreatedEvent(
            saved.getId(),
            saved.getClient().getEmail(),
            saved.getClient().getName(),
            saved.getDateTime()
        ));

        meterRegistry.counter("api_appointment_created_total").increment();
        return toDTO(saved);
    });
   
    }

    // ================= CREATE PUBLIC (PORTAL DA CLIENTE) =================

    @Transactional
    public AppointmentDTO createPublicAppointment(PublicAppointmentCreateDTO dto) {
        log.info("Criando agendamento público para: {}", dto.getClientName());

        ProfessionalEntity professional = professionalRepository.findById(dto.getProfessionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado: " + dto.getProfessionalId()));

        if (!professional.getActive()) {
            throw new BusinessException("Profissional inativo");
        }

        Long tenantId = professional.getTenantId();
        if (tenantId == null) {
            throw new BusinessException("Profissional sem salão vinculado.");
        }

        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + dto.getServiceId()));

        if (!tenantId.equals(service.getTenantId())) {
            throw new BusinessException("Serviço não pertence ao mesmo salão do profissional.");
        }

        subscriptionService.validateAppointmentLimit(tenantId);
        validateFutureDate(dto.getDateTime());
        validateTimeConflict(professional.getId(), dto.getDateTime(), service.getDuration(), null);

        // Busca cliente existente por email ou telefone dentro do mesmo salão, ou cadastra automaticamente
        ClientEntity client = clientRepository.findByEmailAndTenantId(dto.getClientEmail(), tenantId)
                .or(() -> clientRepository.findByPhoneAndTenantId(dto.getClientPhone(), tenantId))
                .orElseGet(() -> {
                    ClientEntity newClient = ClientEntity.builder()
                            .tenantId(tenantId)
                            .name(dto.getClientName())
                            .phone(dto.getClientPhone())
                            .email(dto.getClientEmail())
                            .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                            .build();
                    return clientRepository.save(newClient);
                });

        client.setName(dto.getClientName());
        client.setPhone(dto.getClientPhone());
        clientRepository.save(client);

        AppointmentEntity entity = new AppointmentEntity();
        entity.setTenantId(tenantId);
        entity.setClient(client);
        entity.setProfessional(professional);
        entity.setService(service);
        entity.setDateTime(dto.getDateTime());
        entity.setStatus(AppointmentEntity.AppointmentStatus.SCHEDULED);

        AppointmentEntity saved = appointmentRepository.save(entity);

        eventPublisher.publishEvent(new AppointmentCreatedEvent(
            saved.getId(),
            saved.getClient().getEmail(),
            saved.getClient().getName(),
            saved.getDateTime()
        ));

        meterRegistry.counter("api_appointment_created_total").increment();
        return toDTO(saved);
    }

    // ================= UPDATE =================

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        log.info("Atualizando agendamento {}", id);

        AppointmentEntity existing = getAppointment(id);
        validateNotCompleted(existing);

        if (dto.getDateTime() != null) {
            validateFutureDate(dto.getDateTime());
            Integer dur = existing.getService() != null ? existing.getService().getDuration() : 30;
            validateTimeConflict(existing.getProfessional().getId(), dto.getDateTime(), dur, id);
            existing.setDateTime(dto.getDateTime());
        }

        if (dto.getStatus() != null) {
            validateStatusTransition(existing.getStatus(), dto.getStatus());
            existing.setStatus(dto.getStatus());
        }

        AppointmentEntity updated = appointmentRepository.save(existing);

        return toDTO(updated);
    }

    // ================= OCCUPIED SLOTS =================

    @Transactional(readOnly = true)
    public List<OccupiedSlotDTO> getOccupiedSlots(Long professionalId, java.time.LocalDate date) {
        log.info("Buscando horários ocupados para o profissional {} na data {}", professionalId, date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<AppointmentEntity> appointments = appointmentRepository
                .findActiveByProfessionalAndDateRange(professionalId, startOfDay, endOfDay, AppointmentEntity.AppointmentStatus.CANCELLED);

        return appointments.stream()
                .filter(a -> a.getStatus() != AppointmentEntity.AppointmentStatus.CANCELLED)
                .map(a -> {
                    int duration = (a.getService() != null && a.getService().getDuration() != null && a.getService().getDuration() > 0)
                            ? a.getService().getDuration()
                            : 30;
                    LocalDateTime start = a.getDateTime();
                    LocalDateTime end = start.plusMinutes(duration);
                    String startTimeStr = String.format("%02d:%02d", start.getHour(), start.getMinute());
                    String endTimeStr = String.format("%02d:%02d", end.getHour(), end.getMinute());

                    return OccupiedSlotDTO.builder()
                            .time(startTimeStr)
                            .endTime(endTimeStr)
                            .startDateTime(start)
                            .endDateTime(end)
                            .durationMinutes(duration)
                            .build();
                })
                .sorted(java.util.Comparator.comparing(OccupiedSlotDTO::getStartDateTime))
                .toList();
    }

    // ================= CANCEL =================

    @Transactional
    public void cancelAppointment(Long id) {
        log.info("Cancelando agendamento {}", id);

        AppointmentEntity existing = getAppointment(id);
        validateNotCompleted(existing);

        if (!DateUtil.canCancel(existing.getDateTime())) {
            throw new BusinessException(
                    "Cancelamento só com " + Constants.CANCEL_HOURS_AHEAD + "h antecedência"
            );
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CANCELLED);
        appointmentRepository.save(existing);
    }

    // ================= CONFIRM =================

    @Transactional
    public AppointmentDTO confirmAppointment(Long id) {
        log.info("Confirmando agendamento {}", id);

        AppointmentEntity existing = getAppointment(id);

        if (existing.getStatus() != AppointmentEntity.AppointmentStatus.SCHEDULED) {
            throw new BusinessException("Apenas SCHEDULED pode ser confirmado");
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CONFIRMED);

        AppointmentEntity updated = appointmentRepository.save(existing);

        return toDTO(updated);
    }

    // ================= FIND =================

    @Transactional(readOnly = true)
    public AppointmentDTO findById(Long id) {
        log.debug("Buscando agendamento {}", id);
        return toDTO(getAppointment(id));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findAll(Pageable pageable, AppointmentEntity.AppointmentStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findWithFilters(status, startDate, endDate, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByClientId(Long clientId, Pageable pageable) {
        return appointmentRepository.findByClientId(clientId, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> findByProfessionalId(Long professionalId, Pageable pageable) {
        return appointmentRepository.findByProfessionalId(professionalId, pageable)
                .map(this::toDTO);
    }
    private AppointmentDTO toDTO(AppointmentEntity entity) {
        if (entity == null) return null;

        return AppointmentDTO.builder()
                .id(entity.getId())
                .clientId(entity.getClient().getId())
                .clientName(entity.getClient().getName())
                .professionalId(entity.getProfessional().getId())
                .professionalName(entity.getProfessional().getName())
                .serviceId(entity.getService().getId())
                .serviceName(entity.getService().getName())
                .servicePrice(entity.getService().getPrice())
                .dateTime(entity.getDateTime())
                .status(entity.getStatus())
                .build();
    }
}