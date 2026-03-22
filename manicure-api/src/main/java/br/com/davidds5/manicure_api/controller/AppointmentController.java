package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.dto.AppointmentUpdateDTO;
import br.com.davidds5.manicure_api.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "📅 Agendamentos")

public class AppointmentController {

    private final AppointmentService appointmentService; // ✅ Injetado pelo Lombok

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Criar agendamento")
    public ResponseEntity<AppointmentDTO> create(@Valid @RequestBody AppointmentCreateDTO dto) {
        return ResponseEntity.status(201).body(appointmentService.createAppointment(dto));
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos")
    public ResponseEntity<Page<AppointmentDTO>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento")
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Agendamentos do cliente")
    public ResponseEntity<Page<AppointmentDTO>> findByClient(
            @PathVariable("clientId") Long clientId,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByClientId(clientId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento")
    public ResponseEntity<AppointmentDTO> update(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar agendamento")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirmar agendamento")
    public ResponseEntity<AppointmentDTO> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }
}