package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-21T20:29:48-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public ServiceDTO toDTO(ServiceEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ServiceDTO.ServiceDTOBuilder serviceDTO = ServiceDTO.builder();

        serviceDTO.id( entity.getId() );
        serviceDTO.name( entity.getName() );
        serviceDTO.price( entity.getPrice() );

        return serviceDTO.build();
    }

    @Override
    public ServiceEntity toEntity(ServiceCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ServiceEntity.ServiceEntityBuilder serviceEntity = ServiceEntity.builder();

        serviceEntity.name( dto.getName() );
        serviceEntity.price( dto.getPrice() );

        return serviceEntity.build();
    }
}
