package br.com.davidds5.manicure_api.dto;


import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentUpdateDTO {
    private LocalDateTime dateTime;
    private AppointmentDTO.AppointmentStatus status;
}