package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.dto.AppointmentUpdateDTO;
import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import br.com.davidds5.manicure_api.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Agendamentos")

public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Criar agendamento", description = "Marca um novo horário vinculando cliente, profissional e serviço.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação (ex: horário já ocupado ou cliente/profissional inativo)", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<AppointmentDTO> create(@Valid @RequestBody AppointmentCreateDTO dto) {
        return ResponseEntity.status(201).body(appointmentService.createAppointment(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar agendamentos", description = "Retorna todos os agendamentos, podendo ser filtrados por status, data inicial e data final")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Requer token de autenticação"),
        @ApiResponse(responseCode = "403", description = "Token inválido ou sem permissão de ADMIN"),
        @ApiResponse(responseCode = "400", description = "Erro de validação", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })

    public ResponseEntity<Page<AppointmentDTO>> findAll(@PageableDefault(size = 10) Pageable pageable, 
    @RequestParam(required = false) AppointmentEntity.AppointmentStatus status,
    @RequestParam(required = false) LocalDateTime startDate,
    @RequestParam(required = false) LocalDateTime endDate) {
        return ResponseEntity.ok(appointmentService.findAll(pageable, status, startDate, endDate));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityValidator.isAppointmentOwnerOrAdmin(#id, authentication)")
    @Operation(summary = "Buscar agendamento", description = "Retorna os detalhes de um agendamento específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Agendamentos do cliente", description = "Busca todo historico de agendamentos de um cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamentos encontrados"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })

    @PreAuthorize("@securityValidator.isSelfOrAdmin(#clientId, authentication)")
    public ResponseEntity<Page<AppointmentDTO>> findByClient(
            @PathVariable("clientId") Long clientId,
            Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByClientId(clientId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityValidator.isAppointmentOwnerOrAdmin(#id, authentication)")
    @Operation(summary = "Atualizar agendamento", description = "Alterar os dados de um agendamento (ex: data, hora, status, serviço)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Conflito de horario ou erro de validacao", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        ),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<AppointmentDTO> update(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("@securityValidator.isAppointmentOwnerOrAdmin(#id, authentication)")
    @Operation(summary = "Cancelar agendamento", description = "Muda o status do agendamento para CANCELADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Agendamento cancelado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Agendamento ja cancelado ou concluido", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        ),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirmar agendamento", description = "Muda o status do agendamento para CONFIRMADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento confirmado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Agendamento ja cancelado ou concluido", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        ),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<AppointmentDTO> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }
}