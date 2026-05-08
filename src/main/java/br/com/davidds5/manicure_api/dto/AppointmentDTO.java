package br.com.davidds5.manicure_api.dto;

import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;
    private Long clientId;
    private String clientName;
    private Long professionalId;
    private String professionalName;
    private Long serviceId;
    private String serviceName;
    private Double servicePrice;
    private LocalDateTime dateTime;
    private AppointmentEntity.AppointmentStatus status;


    public enum AppointmentStatus{
        SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
    }
}
