package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.dto.ServiceUpdateDTO;
import br.com.davidds5.manicure_api.service.ServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Serviços", description = "Gerenciamento de serviços")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @Operation(summary = "Criar novo serviço", description = "Adicionar um novo tipo de servico ao catalogo (ex: Manicure, Depilação etc)")
   @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Requer token de autenticação"),
        @ApiResponse(responseCode = "403", description = "Token inválido ou sem permissão de ADMIN"),
        @ApiResponse(responseCode = "400", description = "Erro de validação (nome duplicado, valor negativo, etc)", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<EntityModel<ServiceDTO>> create(@Valid @RequestBody ServiceCreateDTO dto) {
        ServiceDTO service = serviceService.createService(dto);

        EntityModel<ServiceDTO> resource = EntityModel.of(service);
        resource.add(linkTo(methodOn(ServiceController.class).findById(service.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ServiceController.class).findAll()).withRel("all-services"));

        return ResponseEntity.status(201).body(resource);
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços",description = "Retorna uma lista paginada de todos os servicos cadastrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviços listados com sucesso")
    })
    public ResponseEntity<CollectionModel<EntityModel<ServiceDTO>>> findAll() {
        List<EntityModel<ServiceDTO>> services = serviceService.findAll()
                .stream()
                .map(service -> {
                    EntityModel<ServiceDTO> resource = EntityModel.of(service);
                    resource.add(linkTo(methodOn(ServiceController.class)
                            .findById(service.getId())).withSelfRel());
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ServiceDTO>> collection =
                CollectionModel.of(services);

        collection.add(linkTo(methodOn(ServiceController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Retorna os detalhes de um serviço específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Serviço nao encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<EntityModel<ServiceDTO>> findById(@PathVariable Long id) {
        ServiceDTO service = serviceService.findById(id);

        EntityModel<ServiceDTO> resource = EntityModel.of(service);
        resource.add(linkTo(methodOn(ServiceController.class).findAll()).withRel("all-services"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço", description = "Atualizar os dados (como preco ou duracao) de um servico especifico pelo ID")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        ),
        @ApiResponse(responseCode = "401", description = "Requer token de autenticação"),
        @ApiResponse(responseCode = "403", description = "Token inválido ou sem permissão de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Serviço nao encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<EntityModel<ServiceDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceUpdateDTO dto) {

        ServiceDTO service = serviceService.updateService(id, dto);

        EntityModel<ServiceDTO> resource = EntityModel.of(service);
        resource.add(linkTo(methodOn(ServiceController.class)
                .findById(service.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço", description ="Remove um servico do catalogo.")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Requer token de autenticação"),
        @ApiResponse(responseCode = "403", description = "Token inválido ou sem permissão de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Serviço nao encontrado", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}