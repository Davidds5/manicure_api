package br.com.davidds5.manicure_api.dto;

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

    @Positive(message = "O preço deve ser maior que zero")
    private Double price;

    private Integer durationMinutes;

    private Boolean active;
}