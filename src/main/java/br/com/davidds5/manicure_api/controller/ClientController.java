package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;
import br.com.davidds5.manicure_api.dto.ClientUpdateDTO;
import br.com.davidds5.manicure_api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Criar novo cliente", description = "Cadrasta um novo cliente no sistema. Acesso publico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validacao (email ja cadastrado, telefone invalido, etc)", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<EntityModel<ClientDTO>> create(@Valid @RequestBody ClientCreatedDTO dto) {
        ClientDTO client = clientService.createClient(dto);

        EntityModel<ClientDTO> resource = buildResource(client);

        URI location = linkTo(methodOn(ClientController.class)
                .findById(client.getId())).toUri();

        return ResponseEntity.created(location).body(resource);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista paginada. Permite filtro por nome. Requer permissão de ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso"),
        @ApiResponse(responseCode = "401", description = "Acesso negado - Faltou token ou você nao e ADMIN")
    })
    public ResponseEntity<Page<ClientDTO>> findAll(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String phone,
            @PageableDefault(size = 10) Pageable pageable) { 
                
        return ResponseEntity.ok(clientService.findAll(name,email,phone, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityValidator.isSelfOrAdmin(#id, authentication)")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os detalhes de um cliente específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente nao encontrado com o id informado.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<EntityModel<ClientDTO>> findById(@PathVariable Long id) {
        ClientDTO client = clientService.findById(id);
        return ResponseEntity.ok(buildResource(client));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityValidator.isSelfOrAdmin(#id, authentication)")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validacao (email ja cadastrado, telefone invalido, etc)", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        ),
        @ApiResponse(responseCode = "404", description = "Cliente nao encontrado com o id informado.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<EntityModel<ClientDTO>> update(@PathVariable Long id,
                                                         @Valid @RequestBody ClientUpdateDTO dto) {
        ClientDTO client = clientService.updateClient(id, dto);
        return ResponseEntity.ok(buildResource(client));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar cliente", description = "Remove um cliente do sistema. Requer permissão de ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
        @ApiResponse(responseCode = "401", description = "Acesso negado - Faltou token ou você nao e ADMIN"),
        @ApiResponse(responseCode = "404", description = "Cliente nao encontrado com o id informado.", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseException.class))
        )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }


    private EntityModel<ClientDTO> buildResource(ClientDTO client) {
        return EntityModel.of(client,
                linkTo(methodOn(ClientController.class).findById(client.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).findAll("","","", Pageable.unpaged())).withRel("all-clients")
        );
    }
}
 