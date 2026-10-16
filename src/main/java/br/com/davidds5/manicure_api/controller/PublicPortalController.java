package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.*;
import br.com.davidds5.manicure_api.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/{tenantSlug}")
@Tag(name = "Portal Público da Cliente", description = "Endpoints públicos do portal de agendamento isolados por slug do salão")
@RequiredArgsConstructor
public class PublicPortalController {

    private final TenantService tenantService;
    private final ServiceService serviceService;
    private final ProfessionalService professionalService;
    private final AppointmentService appointmentService;
    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "Obtém informações públicas do salão pelo slug")
    public ResponseEntity<TenantResponseDTO> getSalonInfo(@PathVariable String tenantSlug) {
        return ResponseEntity.ok(tenantService.getPublicTenantBySlug(tenantSlug));
    }

    @GetMapping("/services")
    @Operation(summary = "Lista todos os serviços ativos do salão informado")
    public ResponseEntity<List<ServiceDTO>> getServices(@PathVariable String tenantSlug) {
        return ResponseEntity.ok(serviceService.findByTenantSlug(tenantSlug));
    }

    @GetMapping("/professionals")
    @Operation(summary = "Lista todos os profissionais ativos do salão informado")
    public ResponseEntity<List<ProfessionalDTO>> getProfessionals(@PathVariable String tenantSlug) {
        return ResponseEntity.ok(professionalService.findByTenantSlug(tenantSlug));
    }

    @GetMapping("/occupied-slots")
    @Operation(summary = "Consulta horários ocupados de um profissional em uma data específica")
    public ResponseEntity<List<OccupiedSlotDTO>> getOccupiedSlots(
            @PathVariable String tenantSlug,
            @RequestParam Long professionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(appointmentService.getOccupiedSlotsBySlug(tenantSlug, professionalId, date));
    }

    @PostMapping("/appointments")
    @Operation(summary = "Cria um agendamento público pelo portal do cliente")
    public ResponseEntity<AppointmentDTO> createPublicAppointment(
            @PathVariable String tenantSlug,
            @RequestBody @Valid PublicAppointmentCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createPublicAppointmentBySlug(tenantSlug, dto));
    }

    @PostMapping("/clients")
    @Operation(summary = "Realiza cadastro de um novo cliente no salão informado")
    public ResponseEntity<ClientDTO> createPublicClient(
            @PathVariable String tenantSlug,
            @RequestBody @Valid ClientCreatedDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createPublicClient(tenantSlug, dto));
    }
}
