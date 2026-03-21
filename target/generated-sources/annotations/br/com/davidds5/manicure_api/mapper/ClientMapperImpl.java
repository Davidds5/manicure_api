package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-21T20:29:48-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public ClientDTO toDTO(ClientEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClientDTO.ClientDTOBuilder clientDTO = ClientDTO.builder();

        clientDTO.id( entity.getId() );
        clientDTO.name( entity.getName() );
        clientDTO.phone( entity.getPhone() );
        clientDTO.email( entity.getEmail() );

        return clientDTO.build();
    }

    @Override
    public ClientEntity toEntity(ClientCreatedDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ClientEntity.ClientEntityBuilder clientEntity = ClientEntity.builder();

        clientEntity.name( dto.getName() );
        clientEntity.phone( dto.getPhone() );
        clientEntity.email( dto.getEmail() );

        return clientEntity.build();
    }
}
