package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-21T20:29:48-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public AppointmentDTO toDTO(AppointmentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AppointmentDTO.AppointmentDTOBuilder appointmentDTO = AppointmentDTO.builder();

        appointmentDTO.id( entity.getId() );
        appointmentDTO.clientId( entityClientId( entity ) );
        appointmentDTO.clientName( entityClientName( entity ) );
        appointmentDTO.professionalId( entityProfessionalId( entity ) );
        appointmentDTO.professionalName( entityProfessionalName( entity ) );
        appointmentDTO.serviceId( entityServiceId( entity ) );
        appointmentDTO.serviceName( entityServiceName( entity ) );
        appointmentDTO.servicePrice( entityServicePrice( entity ) );
        appointmentDTO.dateTime( entity.getDateTime() );
        appointmentDTO.status( entity.getStatus() );

        return appointmentDTO.build();
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
