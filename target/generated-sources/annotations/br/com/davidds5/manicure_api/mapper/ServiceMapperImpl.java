package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T09:42:49-0300",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public ServiceDTO toDTO(ServiceEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ServiceDTO.ServiceDTOBuilder serviceDTO = ServiceDTO.builder();

        if ( entity.getActive() != null ) {
            serviceDTO.active( entity.getActive() );
        }
        serviceDTO.description( entity.getDescription() );
        serviceDTO.duration( entity.getDuration() );
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

        serviceEntity.active( dto.getActive() );
        serviceEntity.description( dto.getDescription() );
        serviceEntity.duration( dto.getDuration() );
        serviceEntity.name( dto.getName() );
        serviceEntity.price( dto.getPrice() );

        return serviceEntity.build();
    }
}
