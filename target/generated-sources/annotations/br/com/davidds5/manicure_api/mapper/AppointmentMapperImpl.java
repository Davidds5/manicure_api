package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.dto.AppointmentUpdateDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-23T00:16:36-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public AppointmentEntity toEntity(AppointmentCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AppointmentEntity.AppointmentEntityBuilder appointmentEntity = AppointmentEntity.builder();

        appointmentEntity.dateTime( dto.getDateTime() );

        appointmentEntity.status( AppointmentEntity.AppointmentStatus.SCHEDULED );

        return appointmentEntity.build();
    }

    @Override
    public AppointmentDTO toDto(AppointmentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AppointmentDTO.AppointmentDTOBuilder appointmentDTO = AppointmentDTO.builder();

        appointmentDTO.clientId( entityClientId( entity ) );
        appointmentDTO.clientName( entityClientName( entity ) );
        appointmentDTO.professionalId( entityProfessionalId( entity ) );
        appointmentDTO.professionalName( entityProfessionalName( entity ) );
        appointmentDTO.serviceId( entityServiceId( entity ) );
        appointmentDTO.serviceName( entityServiceName( entity ) );
        appointmentDTO.servicePrice( entityServicePrice( entity ) );
        appointmentDTO.id( entity.getId() );
        appointmentDTO.dateTime( entity.getDateTime() );
        appointmentDTO.status( entity.getStatus() );

        return appointmentDTO.build();
    }

    @Override
    public void partialUpdate(AppointmentUpdateDTO dto, AppointmentEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getDateTime() != null ) {
            entity.setDateTime( dto.getDateTime() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
    }

    private Long entityClientId(AppointmentEntity appointmentEntity) {
        ClientEntity client = appointmentEntity.getClient();
        if ( client == null ) {
            return null;
        }
        return client.getId();
    }

    private String entityClientName(AppointmentEntity appointmentEntity) {
        ClientEntity client = appointmentEntity.getClient();
        if ( client == null ) {
            return null;
        }
        return client.getName();
    }

    private Long entityProfessionalId(AppointmentEntity appointmentEntity) {
        ProfessionalEntity professional = appointmentEntity.getProfessional();
        if ( professional == null ) {
            return null;
        }
        return professional.getId();
    }

    private String entityProfessionalName(AppointmentEntity appointmentEntity) {
        ProfessionalEntity professional = appointmentEntity.getProfessional();
        if ( professional == null ) {
            return null;
        }
        return professional.getName();
    }

    private Long entityServiceId(AppointmentEntity appointmentEntity) {
        ServiceEntity service = appointmentEntity.getService();
        if ( service == null ) {
            return null;
        }
        return service.getId();
    }

    private String entityServiceName(AppointmentEntity appointmentEntity) {
        ServiceEntity service = appointmentEntity.getService();
        if ( service == null ) {
            return null;
        }
        return service.getName();
    }

    private Double entityServicePrice(AppointmentEntity appointmentEntity) {
        ServiceEntity service = appointmentEntity.getService();
        if ( service == null ) {
            return null;
        }
        return service.getPrice();
    }
}
