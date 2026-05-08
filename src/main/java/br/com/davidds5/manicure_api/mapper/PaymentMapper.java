package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.dto.PaymentDTO;
import br.com.davidds5.manicure_api.entity.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDTO toDTO(PaymentEntity entity);

    PaymentEntity toEntity(PaymentCreateDTO dto);
}