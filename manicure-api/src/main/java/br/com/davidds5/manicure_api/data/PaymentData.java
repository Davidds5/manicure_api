package br.com.davidds5.manicure_api.data;

import br.com.davidds5.manicure_api.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentData extends JpaRepository<PaymentEntity, Long> {

    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE MONTH(p.paidAt) = :month")
    BigDecimal totalPaymentsByMonth(@Param("month") int month);

    @Query("SELECT COUNT(p) FROM PaymentEntity p WHERE p.paymentMethod = :method")
    Long countByPaymentMethod(@Param("method") String method);

    List<PaymentEntity> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}