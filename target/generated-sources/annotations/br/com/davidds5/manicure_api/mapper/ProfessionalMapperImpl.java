package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-23T00:16:36-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ProfessionalMapperImpl implements ProfessionalMapper {

    @Override
    public ProfessionalDTO toDTO(ProfessionalEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProfessionalDTO.ProfessionalDTOBuilder professionalDTO = ProfessionalDTO.builder();

        professionalDTO.name( entity.getName() );
        professionalDTO.specialty( entity.getSpecialty() );
        professionalDTO.active( entity.getActive() );
        professionalDTO.id( entity.getId() );

        return professionalDTO.build();
    }

    @Override
    public ProfessionalEntity toEntity(ProfessionalCreatedDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProfessionalEntity.ProfessionalEntityBuilder professionalEntity = ProfessionalEntity.builder();

        professionalEntity.name( dto.getNome() );
        professionalEntity.specialty( dto.getSpecialty() );
        professionalEntity.active( dto.isActive() );

        return professionalEntity.build();
    }
}
