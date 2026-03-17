package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalMapper {
    private final com.github.dozermapper.core.Mapper mapper;

    public ProfessionalMapper(Mapper dozerMapper){
        this.mapper = dozerMapper;
    }

    public ProfessionalDTO toDTO(ProfessionalEntity entity){
        return mapper.map(entity, ProfessionalDTO.class);
    }

    public ProfessionalEntity toEntity(ProfessionalCreatedDTO dto){
        return mapper.map(dto, ProfessionalEntity.class);
    }
}
