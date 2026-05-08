package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCreateDTO {

    @NotNull(message = "Cliente é obrigatório")
    private Long clientId;

    @NotNull(message = "Profissional é obrigatório")
    private Long professionalId;

    @NotNull(message = "Serviço é obrigatório")
    private Long serviceId;

    @NotNull(message = "Data e hora são obrigatórios")
    @Future(message = "Data deve ser futura")
    private LocalDateTime dateTime;
}