package br.com.davidds5.manicure_api.dto;

import br.com.davidds5.manicure_api.entity.AppointmentEntity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentUpdateDTO {
    private LocalDateTime dateTime;
    private AppointmentStatus status; // agora é o mesmo enum da entity
}