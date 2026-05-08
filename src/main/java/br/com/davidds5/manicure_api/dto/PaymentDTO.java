package br.com.davidds5.manicure_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private Long AppointmentId;
    private Double amount;
    private String paymentMethod;
    private LocalDateTime paiAt;
}
