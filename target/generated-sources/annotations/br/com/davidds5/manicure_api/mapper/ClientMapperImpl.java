package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T09:42:49-0300",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public ClientDTO toDTO(ClientEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClientDTO.ClientDTOBuilder clientDTO = ClientDTO.builder();

        clientDTO.email( entity.getEmail() );
        clientDTO.id( entity.getId() );
        clientDTO.name( entity.getName() );
        clientDTO.phone( entity.getPhone() );

        return clientDTO.build();
    }

    @Override
    public ClientEntity toEntity(ClientCreatedDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ClientEntity.ClientEntityBuilder clientEntity = ClientEntity.builder();

        clientEntity.email( dto.getEmail() );
        clientEntity.name( dto.getName() );
        clientEntity.phone( dto.getPhone() );

        return clientEntity.build();
    }

    @Override
    public void updateEntity(ClientCreatedDTO dto, ClientEntity entity) {
        if ( dto == null ) {
            return;
        }

        entity.setEmail( dto.getEmail() );
        entity.setName( dto.getName() );
        entity.setPassword( dto.getPassword() );
        entity.setPhone( dto.getPhone() );
    }
}
