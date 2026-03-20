package br.com.davidds5.manicure_api.controller;


import br.com.davidds5.manicure_api.dto.ClientCreatedDTO;
import br.com.davidds5.manicure_api.dto.ClientDTO;

import br.com.davidds5.manicure_api.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @Operation(summary = "Criar novo cliente")
    public ResponseEntity<EntityModel<ClientDTO>> create(@Valid @RequestBody ClientCreatedDTO dto) {
        ClientDTO client = clientService.createClient(dto);

        EntityModel<ClientDTO> resource = EntityModel.of(client);
        resource.add(linkTo(methodOn(ClientController.class).findById(client.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ClientController.class).findAll(null)).withRel("all-clients"));

        return ResponseEntity.status(201).body(resource);
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public ResponseEntity<Page<ClientDTO>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(clientService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<EntityModel<ClientDTO>> findById(@PathVariable Long id) {
        ClientDTO client = clientService.findById(id);

        EntityModel<ClientDTO> resource = EntityModel.of(client);
        resource.add(linkTo(methodOn(ClientController.class).findAll(null)).withRel("all-clients"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    public ResponseEntity<EntityModel<ClientDTO>> update(@PathVariable Long id,
                                                         @Valid @RequestBody ClientCreatedDTO dto) {
        ClientDTO client = clientService.updateClient(id, dto);

        EntityModel<ClientDTO> resource = EntityModel.of(client);
        resource.add(linkTo(methodOn(ClientController.class).findById(client.getId())).withSelfRel());

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}