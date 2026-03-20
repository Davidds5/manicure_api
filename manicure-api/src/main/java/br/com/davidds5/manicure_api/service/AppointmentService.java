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
}
