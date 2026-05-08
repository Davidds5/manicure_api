package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateDTO {

    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private Double amount;

    @NotBlank(message = "Metodo de pagamento e obrigatorio")
    @Size(max = 50, message = "Metodo de pagamento muito longo")
    private String paymentMethod;

}
