package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.TenantResponseDTO;
import br.com.davidds5.manicure_api.dto.TenantSignupDTO;
import br.com.davidds5.manicure_api.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
@Tag(name = "Tenants", description = "Endpoints para gerenciamento de Tenants (Salões SaaS)")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping("/signup")
    @Operation(summary = "Cadastro self-service de um novo salão (cria Tenant + Admin/Owner)")
    public ResponseEntity<TenantResponseDTO> signup(@RequestBody @Valid TenantSignupDTO dto) {
        TenantResponseDTO response = tenantService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtém os dados, branding e limites do salão autenticado")
    public ResponseEntity<br.com.davidds5.manicure_api.dto.TenantDetailsDTO> getMyTenant() {
        return ResponseEntity.ok(tenantService.getMyTenant());
    }

    @PatchMapping("/me")
    @Operation(summary = "Atualiza nome, logo e cor de marca do salão autenticado")
    public ResponseEntity<TenantResponseDTO> updateMyTenant(@RequestBody @Valid br.com.davidds5.manicure_api.dto.TenantUpdateDTO dto) {
        return ResponseEntity.ok(tenantService.updateMyTenant(dto));
    }
}
