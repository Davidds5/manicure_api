package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.PlatformMetricsDTO;
import br.com.davidds5.manicure_api.dto.TenantResponseDTO;
import br.com.davidds5.manicure_api.dto.TenantStatusUpdateDTO;
import br.com.davidds5.manicure_api.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tenants")
@Tag(name = "SaaS Admin (Super Admin)", description = "Endpoints administrativos para donos da plataforma SaaS")
@SecurityRequirement(name = "bearer-key")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class SuperAdminTenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    @Operation(summary = "Lista todos os salões cadastrados na plataforma com paginação")
    public ResponseEntity<Page<TenantResponseDTO>> listAllTenants(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tenantService.listAllTenants(pageable));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Métricas gerais da plataforma (Total de tenants, status, MRR estimado)")
    public ResponseEntity<PlatformMetricsDTO> getPlatformMetrics() {
        return ResponseEntity.ok(tenantService.getPlatformMetrics());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de um salão (ex: suspender ou ativar)")
    public ResponseEntity<TenantResponseDTO> updateTenantStatus(
            @PathVariable Long id,
            @RequestBody @Valid TenantStatusUpdateDTO dto
    ) {
        return ResponseEntity.ok(tenantService.updateTenantStatus(id, dto.status()));
    }
}
