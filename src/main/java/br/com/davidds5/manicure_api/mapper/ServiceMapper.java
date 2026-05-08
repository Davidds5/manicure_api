package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    ServiceDTO toDTO(ServiceEntity entity);

    ServiceEntity toEntity(ServiceCreateDTO dto);
}