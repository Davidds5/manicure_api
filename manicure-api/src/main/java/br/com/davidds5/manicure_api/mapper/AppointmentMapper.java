package br.com.davidds5.manicure_api.mapper;

import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.entity.ClientEntity;
import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import br.com.davidds5.manicure_api.entity.ServiceEntity;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Component;



@Component
public class AppointmentMapper {

    private final Mapper mapper;

    public AppointmentMapper(Mapper dozerMapper) {
        this.mapper = dozerMapper;
    }

    public AppointmentDTO toDTO(AppointmentEntity entity) {
        AppointmentDTO dto = mapper.map(entity, AppointmentDTO.class);
        if (entity.getClient() != null) {
            dto.setClientId(entity.getClient().getId());
            dto.setClientName(entity.getClient().getName());
        }
        if (entity.getProfessional() != null) {
            dto.setProfessionalId(entity.getProfessional().getId());
            dto.setProfessionalName(entity.getProfessional().getName());
        }
        if (entity.getService() != null) {
            dto.setServiceId(entity.getService().getId());
            dto.setServiceName(entity.getService().getName());
            dto.setServicePrice(entity.getService().getPrice());
        }
        return dto;
    }

    public AppointmentEntity toEntity(AppointmentCreateDTO dto, ClientEntity client,
                                      ProfessionalEntity professional, ServiceEntity service) {
        AppointmentEntity entity = mapper.map(dto, AppointmentEntity.class);
        entity.setClient(client);
        entity.setProfessional(professional);
        entity.setService(service);
        entity.setStatus(AppointmentEntity.AppointmentStatus.SCHEDULED);
        return entity;
    }
}