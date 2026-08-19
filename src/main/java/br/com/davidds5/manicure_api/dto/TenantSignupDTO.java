package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TenantSignupDTO(
    @NotBlank(message = "O nome do salão é obrigatório")
    @Size(min = 3, max = 120, message = "O nome do salão deve ter entre 3 e 120 caracteres")
    String salonName,

    @NotBlank(message = "O slug do salão é obrigatório")
    @Size(min = 3, max = 120, message = "O slug deve ter entre 3 e 120 caracteres")
    String slug,

    @NotBlank(message = "O nome do proprietário é obrigatório")
    String ownerName,

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    String ownerEmail,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String ownerPassword,

    String specialty,
    String logoUrl,
    String brandColor
) {}
