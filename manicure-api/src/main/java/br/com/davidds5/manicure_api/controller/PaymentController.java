package br.com.davidds5.manicure_api.controller;



import br.com.davidds5.manicure_api.dto.PaymentCreateDTO;
import br.com.davidds5.manicure_api.dto.PaymentDTO;
import br.com.davidds5.manicure_api.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Pagamentos", description = "Gerenciamento de pagamentos")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/appointments/{appointmentId}")
    @Operation(summary = "Registrar pagamento de agendamento")
    public ResponseEntity<EntityModel<PaymentDTO>> registerPayment(@PathVariable Long appointmentId,
                                                                   @Valid @RequestBody PaymentCreateDTO dto) {
        PaymentDTO payment = paymentService.registerPayment(appointmentId, dto);

        EntityModel<PaymentDTO> resource = EntityModel.of(payment);
        resource.add(linkTo(methodOn(PaymentController.class).registerPayment(appointmentId, dto)).withSelfRel());

        return ResponseEntity.status(201).body(resource);
    }
}