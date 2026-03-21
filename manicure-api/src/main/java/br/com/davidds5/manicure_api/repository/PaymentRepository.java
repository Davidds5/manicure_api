package br.com.davidds5.manicure_api.repository;

import br.com.davidds5.manicure_api.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByAppointmentId(Long appointmentId);
}