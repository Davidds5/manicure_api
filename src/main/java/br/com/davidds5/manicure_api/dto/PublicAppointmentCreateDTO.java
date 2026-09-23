package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicAppointmentCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String clientName;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 8, max = 25, message = "Telefone inválido")
    private String clientPhone;

    @NotBlank(message = "Email é obrigatório")
    @Size(min = 3, max = 100, message = "Email deve ter entre 3 e 100 caracteres")
    private String clientEmail;

    @NotNull(message = "Profissional é obrigatório")
    private Long professionalId;

    @NotNull(message = "Serviço é obrigatório")
    private Long serviceId;

    @NotNull(message = "Data e horário são obrigatórios")
    @Future(message = "Data do agendamento deve ser futura")
    private LocalDateTime dateTime;

    private String tenantSlug;

    private String notes;
}
