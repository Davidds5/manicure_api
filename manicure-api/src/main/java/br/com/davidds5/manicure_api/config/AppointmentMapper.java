package br.com.davidds5.manicure_api.config;

import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "client", source = "client")
    @Mapping(target = "professional", source = "professional")
    @Mapping(target = "service", source = "service")
    AppointmentEntity toEntity(AppointmentCreateDTO dto,
                               ClientEntity client,
                               ProfessionalEntity professional,
                               ServiceEntity service);

    AppointmentDTO toDTO(AppointmentEntity entity);
}