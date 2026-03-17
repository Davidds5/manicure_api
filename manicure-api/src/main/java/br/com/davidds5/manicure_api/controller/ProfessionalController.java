package br.com.davidds5.manicure_api.controller;


import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import com.manicure.service.ProfessionalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/professionals")
@Tag(name = "Profissionais", description = "Gerenciamento de profissionais")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping
    @Operation(summary = "Criar novo profissional")
    public ResponseEntity<EntityModel<ProfessionalDTO>> create(@Valid @RequestBody ProfessionalCreatedDTO dto) {
        ProfessionalDTO professional = professionalService.createProfessional(dto);

        EntityModel<ProfessionalDTO> resource = EntityModel.of(professional);
        resource.add(linkTo(methodOn(ProfessionalController.class).findById(professional.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ProfessionalController.class).findAllActive()).withRel("all-active"));

        return ResponseEntity.status(201).body(resource);
    }

    @GetMapping
    @Operation(summary = "Listar profissionais ativos")
    public ResponseEntity<List<ProfessionalDTO>> findAllActive() {
        return ResponseEntity.ok(professionalService.findAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por ID")
    public ResponseEntity<EntityModel<ProfessionalDTO>> findById(@PathVariable Long id) {
        ProfessionalDTO professional = professionalService.findById(id);

        EntityModel<ProfessionalDTO> resource = EntityModel.of(professional);
        resource.add(linkTo(methodOn(ProfessionalController.class).findAllActive()).withRel("all-active"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar profissional")
    public ResponseEntity<EntityModel<ProfessionalDTO>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ProfessionalCreateDTO dto) {
        ProfessionalDTO professional = professionalService.updateProfessional(id, dto);

        EntityModel<ProfessionalDTO> resource = EntityModel.of(professional);
        resource.add(linkTo(methodOn(ProfessionalController.class).findById(professional.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar profissional")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
}