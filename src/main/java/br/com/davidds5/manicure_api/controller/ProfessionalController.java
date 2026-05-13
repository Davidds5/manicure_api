package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.ProfessionalCreatedDTO;
import br.com.davidds5.manicure_api.dto.ProfessionalDTO;
import br.com.davidds5.manicure_api.service.ProfessionalService;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/professionals")
@Tag(name = "Profissionais", description = "Gerenciamento de profissionais")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo profissional", description = "Cadastra um novo profissional no sistema. Requer permissão de ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        ),
        @ApiResponse(responseCode = "401", description = "Requer token de autenticação"),
        @ApiResponse(responseCode = "403", description = "Token inválido ou sem permissão de ADMIN")
    })
    public ResponseEntity<EntityModel<ProfessionalDTO>> create(@Valid @RequestBody ProfessionalCreatedDTO dto) {
        ProfessionalDTO professional = professionalService.createProfessional(dto);

        EntityModel<ProfessionalDTO> resource = EntityModel.of(professional);
        resource.add(linkTo(methodOn(ProfessionalController.class).findById(professional.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ProfessionalController.class).findAllActive()).withRel("all-active"));

        return ResponseEntity.status(201).body(resource);
    }

    @GetMapping
    @Operation(summary = "Listar profissionais ativos", description = "Retorna apenas os profissionais ativos no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profissionais listados com sucesso")
    })
    public ResponseEntity<List<ProfessionalDTO>> findAllActive() {
        return ResponseEntity.ok(professionalService.findAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por ID", description = "Retorna os dados de um profissional pelo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profissional encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Profissional nao encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class)))
    })
    public ResponseEntity<EntityModel<ProfessionalDTO>> findById(@PathVariable Long id) {
        ProfessionalDTO professional = professionalService.findById(id);

        EntityModel<ProfessionalDTO> resource = EntityModel.of(professional);
        resource.add(linkTo(methodOn(ProfessionalController.class).findAllActive()).withRel("all-active"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar profissional", description = "Atualiza os dados de um profissional existente. Requer ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Nao autorizado"),
        @ApiResponse(responseCode = "403", description = "Proibido"),
        @ApiResponse(responseCode = "404", description = "Profissional nao encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class)))
    })
    public ResponseEntity<EntityModel<ProfessionalDTO>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ProfessionalCreatedDTO dto) {
        ProfessionalDTO professional = professionalService.updateProfessional(id, dto);

        EntityModel<ProfessionalDTO> resource = EntityModel.of(professional);
        resource.add(linkTo(methodOn(ProfessionalController.class).findById(professional.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar profissional", description = "Demitir/Desativar um profissional existente. Requer ADMIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Profissional demitido/desativado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Acesso negado - Requer permissão de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Profissional nao encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }
}