package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfessionalMapper {

    // Mapeia de entity -> DTO
    @Mapping(target = "name", source = "name")          // ProfessionalEntity.name -> ProfessionalDTO.name
    @Mapping(target = "specialty", source = "specialty")
    @Mapping(target = "active", source = "active")
    ProfessionalDTO toDTO(ProfessionalEntity entity);

    // Mapeia de DTO -> entity
    @Mapping(target = "name", source = "nome")         // ProfessionalCreatedDTO.nome -> ProfessionalEntity.name
    @Mapping(target = "specialty", source = "specialty")
    @Mapping(target = "active", source = "active")
    ProfessionalEntity toEntity(ProfessionalCreatedDTO dto);
}