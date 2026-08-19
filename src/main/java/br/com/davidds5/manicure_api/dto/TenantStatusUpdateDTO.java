package br.com.davidds5.manicure_api.dto;

import br.com.davidds5.manicure_api.enums.TenantStatus;
import jakarta.validation.constraints.NotNull;

public record TenantStatusUpdateDTO(
    @NotNull(message = "O status é obrigatório")
    TenantStatus status
) {}
