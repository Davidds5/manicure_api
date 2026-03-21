package br.com.davidds5.manicure_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private ProfessionalEntity professional; // ✅ CORRIGIDO: professional (não professionalEntity)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; // ✅ CORRIGIDO: AppointmentStatus (enum interno)

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private PaymentEntity payment;

    // ✅ ENUM INTERNO (acessível como AppointmentEntity.AppointmentStatus.SCHEDULED)
    public enum AppointmentStatus {
        SCHEDULED, CONFIRMED, COMPLETED, CANCELLED
    }
}