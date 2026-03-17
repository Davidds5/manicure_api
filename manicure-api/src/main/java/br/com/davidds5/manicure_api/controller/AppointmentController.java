package br.com.davidds5.manicure_api.controller;


import br.com.davidds5.manicure_api.dto.AppointmentCreateDTO;
import br.com.davidds5.manicure_api.dto.AppointmentDTO;
import br.com.davidds5.manicure_api.dto.AppointmentUpdateDTO;
import br.com.davidds5.manicure_api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Criar novo agendamento")
    public ResponseEntity<EntityModel<AppointmentDTO>> create(@Valid @RequestBody AppointmentCreateDTO dto) {
        AppointmentDTO appointment = appointmentService.createAppointment(dto);

        EntityModel<AppointmentDTO> resource = EntityModel.of(appointment);
        resource.add(linkTo(methodOn(AppointmentController.class).findById(appointment.getId())).withSelfRel());
        resource.add(linkTo(methodOn(AppointmentController.class).findAll(null)).withRel("all-appointments"));

        return ResponseEntity.status(201).body(resource);
    }

    @GetMapping
    @Operation(summary = "Listar todos os agendamentos")
    public ResponseEntity<Page<AppointmentDTO>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    public ResponseEntity<EntityModel<AppointmentDTO>> findById(@PathVariable Long id) {
        AppointmentDTO appointment = appointmentService.findById(id);

        EntityModel<AppointmentDTO> resource = EntityModel.of(appointment);
        resource.add(linkTo(methodOn(AppointmentController.class).findAll(null)).withRel("all-appointments"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar agendamentos de um cliente")
    public ResponseEntity<Page<AppointmentDTO>> findByClientId(@PathVariable Long clientId,
                                                               @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByClientId(clientId, pageable));
    }

    @GetMapping("/professional/{professionalId}")
    @Operation(summary = "Listar agendamentos de um profissional")
    public ResponseEntity<Page<AppointmentDTO>> findByProfessionalId(@PathVariable Long professionalId,
                                                                     @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findByProfessionalId(professionalId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento")
    public ResponseEntity<EntityModel<AppointmentDTO>> update(@PathVariable Long id,
                                                              @Valid @RequestBody AppointmentUpdateDTO dto) {
        AppointmentDTO appointment = appointmentService.updateAppointment(id, dto);

        EntityModel<AppointmentDTO> resource = EntityModel.of(appointment);
        resource.add(linkTo(methodOn(AppointmentController.class).findById(appointment.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar agendamento")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        // Aqui você pode chamar um método específico de cancelamento no service
        appointmentService.updateAppointment(id, AppointmentUpdateDTO.builder()
                .status(AppointmentDTO.AppointmentStatus.CANCELLED)
                .build());
        return ResponseEntity.noContent().build();
    }
}