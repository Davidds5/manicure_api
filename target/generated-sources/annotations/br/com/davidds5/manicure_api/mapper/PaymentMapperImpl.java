package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.dto.PaymentDTO;
import br.com.davidds5.manicure_api.entity.PaymentEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-21T20:29:48-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDTO toDTO(PaymentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PaymentDTO.PaymentDTOBuilder paymentDTO = PaymentDTO.builder();

        paymentDTO.id( entity.getId() );
        paymentDTO.amount( entity.getAmount() );
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
