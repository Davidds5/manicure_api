package br.com.davidds5.manicure_api.service;

import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.dto.PaymentDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.PaymentEntity;
import br.com.davidds5.manicure_api.exceptions.BusinessException;
import br.com.davidds5.manicure_api.exceptions.ResourceNotFoundException;
import br.com.davidds5.manicure_api.mapper.PaymentMapper;
import br.com.davidds5.manicure_api.repository.AppointmentRepository;
import br.com.davidds5.manicure_api.repository.PaymentRepository;
import br.com.davidds5.manicure_api.util.Constants;
import br.com.davidds5.manicure_api.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentDTO registerPayment(Long appointmentId, PaymentCreateDTO dto) {

        log.info("Registrando pagamento para agendamento ID: {}", appointmentId);

        AppointmentEntity appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Agendamento não encontrado com ID: " + appointmentId));

        if (appointment.getPayment() != null) {
            throw new BusinessException("Pagamento já registrado para este agendamento");
        }

        if (!dto.getAmount().equals(appointment.getService().getPrice())) {
            throw new BusinessException("Valor do pagamento deve ser igual ao valor do serviço: R$ "
                    + appointment.getService().getPrice());
        }

        // ✅ Agora sim: MapStruct puro
        PaymentEntity payment = paymentMapper.toEntity(dto);

        // ✅ REGRA DE NEGÓCIO fica no Service
        payment.setAppointment(appointment);
        payment.setPaidAt(java.time.LocalDateTime.now());

        PaymentEntity saved = paymentRepository.save(payment);

        appointment.setStatus(AppointmentEntity.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        log.info("Pagamento registrado com ID: {}", saved.getId());

        return paymentMapper.toDTO(saved);
    }

    @Transactional
    public void cancelAppointment(Long id) {

        AppointmentEntity existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (!DateUtil.canCancel(existing.getDateTime())) {
            throw new BusinessException(
                    "Cancelamento só com " + Constants.CANCEL_HOURS_AHEAD + "h de antecedência"
            );
        }

        existing.setStatus(AppointmentEntity.AppointmentStatus.CANCELLED);
    }
}