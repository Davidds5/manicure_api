package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.Size;

public record TenantUpdateDTO(
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
    String name,

    String logoUrl,

    @Size(max = 10, message = "A cor deve ser um código hex válido (ex: #FF0000)")
    String brandColor
) {}
