package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.dto.PaymentDTO;
import br.com.davidds5.manicure_api.entity.PaymentEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T12:03:21-0300",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDTO toDTO(PaymentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PaymentDTO.PaymentDTOBuilder paymentDTO = PaymentDTO.builder();

        paymentDTO.amount( entity.getAmount() );
        paymentDTO.id( entity.getId() );
        paymentDTO.paymentMethod( entity.getPaymentMethod() );

        return paymentDTO.build();
    }

    @Override
    public PaymentEntity toEntity(PaymentCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PaymentEntity.PaymentEntityBuilder paymentEntity = PaymentEntity.builder();

        paymentEntity.amount( dto.getAmount() );
        paymentEntity.paymentMethod( dto.getPaymentMethod() );

        return paymentEntity.build();
    }
}
