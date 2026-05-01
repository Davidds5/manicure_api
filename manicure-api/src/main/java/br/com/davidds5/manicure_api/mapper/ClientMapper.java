package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO toDTO(ClientEntity entity);

    @Mapping(target = "password", ignore = true)
    ClientEntity toEntity(ClientCreatedDTO dto);

    void updateEntity(ClientCreatedDTO dto, @MappingTarget ClientEntity entity);
}