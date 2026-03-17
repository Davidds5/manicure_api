package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.dto.PaymentDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.PaymentEntity;
import com.github.dozermapper.core.Mapper;

import java.time.LocalDateTime;

public class PaymentMapper {

    private final Mapper mapper;

    public PaymentMapper(Mapper dozerMapper){
        this.mapper = dozerMapper;
    }

    public PaymentDTO toDTO(PaymentEntity entity){
        return mapper.map(entity, PaymentDTO.class);
    }
    public PaymentEntity toEntity(PaymentCreateDTO dto, AppointmentEntity appointment) {
        PaymentEntity entity = mapper.map(dto, PaymentEntity.class);
        entity.setAppointment(appointment);
        entity.setPaidAt(LocalDateTime.now());
        return entity;
    }
}