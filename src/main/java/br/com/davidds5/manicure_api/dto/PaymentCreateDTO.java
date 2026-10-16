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

    @jakarta.validation.constraints.NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @jakarta.validation.constraints.Digits(integer = 8, fraction = 2, message = "Valor inválido")
    private java.math.BigDecimal amount;

    @NotBlank(message = "Metodo de pagamento e obrigatorio")
    @Size(max = 50, message = "Metodo de pagamento muito longo")
    private String paymentMethod;

}
