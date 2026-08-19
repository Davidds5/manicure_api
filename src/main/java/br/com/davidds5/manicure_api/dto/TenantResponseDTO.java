package br.com.davidds5.manicure_api.dto;

import br.com.davidds5.manicure_api.enums.TenantPlan;
import br.com.davidds5.manicure_api.enums.TenantStatus;
import java.time.LocalDateTime;

public record TenantResponseDTO(
    Long id,
    String name,
    String slug,
    TenantPlan plan,
    TenantStatus status,
    String logoUrl,
    String brandColor,
    Long ownerId,
    LocalDateTime createdAt
) {}
