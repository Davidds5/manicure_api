package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.dto.AppointmentUpdateDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppointmentMapper {

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "professional", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(target = "payment", ignore = true)
    AppointmentEntity toEntity(AppointmentCreateDTO dto);

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "professional.id", target = "professionalId")
    @Mapping(source = "professional.name", target = "professionalName")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "service.price", target = "servicePrice")
    AppointmentDTO toDto(AppointmentEntity entity);

    void partialUpdate(AppointmentUpdateDTO dto, @MappingTarget AppointmentEntity entity);
}
