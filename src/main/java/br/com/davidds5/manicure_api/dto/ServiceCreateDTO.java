package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCreateDTO {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    @NotNull(message = "Preço e obrigatorio")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero ")
    private Double price;

    @NotNull(message = "Duração e obrigatoria")
    @Min(value = 1, message = "Duração deve ser maior que zero")
    private Integer duration;
}
