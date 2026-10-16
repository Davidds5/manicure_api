package br.com.davidds5.manicure_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ServiceUpdateDTO {

    private String name;
    private String description;
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Preço inválido")
    private BigDecimal price;

    private Integer duration;

    private Boolean active;
}