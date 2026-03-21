package br.com.davidds5.manicure_api.controller;

import br.com.davidds5.manicure_api.dto.ServiceCreateDTO;
import br.com.davidds5.manicure_api.dto.ServiceDTO;
import br.com.davidds5.manicure_api.dto.ServiceUpdateDTO;
import br.com.davidds5.manicure_api.service.ServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Criar novo serviço")
    public ResponseEntity<EntityModel<ServiceDTO>> create(@Valid @RequestBody ServiceCreateDTO dto) {
        ServiceDTO service = serviceService.createService(dto);

        EntityModel<ServiceDTO> resource = EntityModel.of(service);
        resource.add(linkTo(methodOn(ServiceController.class).findById(service.getId())).withSelfRel());
        resource.add(linkTo(methodOn(ServiceController.class).findAll()).withRel("all-services"));

        return ResponseEntity.status(201).body(resource);
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços")
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
    @Operation(summary = "Buscar serviço por ID")
    public ResponseEntity<EntityModel<ServiceDTO>> findById(@PathVariable Long id) {
        ServiceDTO service = serviceService.findById(id);

        EntityModel<ServiceDTO> resource = EntityModel.of(service);
        resource.add(linkTo(methodOn(ServiceController.class).findAll()).withRel("all-services"));

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço")
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
    @Operation(summary = "Deletar serviço")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}