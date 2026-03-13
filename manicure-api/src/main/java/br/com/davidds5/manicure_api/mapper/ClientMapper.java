package br.com.davidds5.manicure_api.mapper;


import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Component;
@Component
public class ClientMapper {

    private final Mapper mapper;

    public ClientMapper(Mapper dozerMapper){
        this.mapper = dozerMapper;
    }

    public ClientDTO toDTO(ClientEntity entity){
        return mapper.map(entity, ClientDTO.class);
    }

    public ClientEntity toEntity(ClientCreatedDTO dto){
        return mapper.map(dto, ClientEntity.class);
    }
}
