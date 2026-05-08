package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalCreatedDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;  

    @NotBlank(message = "Especialidade é obrigatória")
    @Size(min = 3, max = 50, message = "Especialidade deve ter entre 3 a 50 caracteres")
    private String specialty;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 20, message = "Senha deve ter entre 6 e 20 caracteres")
    private String password;

    @NotBlank(message = "Email é obrigatório")
    @Size(min = 3, max = 100, message = "Email deve ter entre 3 e 100 caracteres")
    private String email;

    private boolean active = true; 
}