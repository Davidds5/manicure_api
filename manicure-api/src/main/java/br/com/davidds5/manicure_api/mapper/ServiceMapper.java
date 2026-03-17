package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    private final Mapper mapper;

    public ServiceMapper(Mapper dozerMapper){
        this.mapper = dozerMapper;
    }

    public ServiceDTO toDTO(ServiceEntity entity){
        return mapper.map(entity, ServiceDTO.class);
    }

    public ServiceEntity toEntity(ServiceCreateDTO dto){
        return mapper.map(dto, ServiceEntity.class);
    }

}
