package br.com.davidds5.manicure_api.data;

import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentData extends JpaRepository<AppointmentEntity, Long> {

    Page<AppointmentEntity> findByClientId(Long clientId, Pageable pageable);

    Page<AppointmentEntity> findByProfessionalId(Long professionalId, Pageable pageable);

    List<AppointmentEntity> findByProfessionalIdAndDateTime(Long professionalId, LocalDateTime dateTime);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.dateTime BETWEEN :start AND :end")
    List<AppointmentEntity> findByDateRange(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM AppointmentEntity a WHERE a.professional.id = :professionalId AND MONTH(a.dateTime) = :month")
    Long countByProfessionalAndMonth(@Param("professionalId") Long professionalId,
                                     @Param("month") int month);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.status = 'SCHEDULED' AND a.dateTime < CURRENT_TIMESTAMP")
    List<AppointmentEntity> findOverdueAppointments();
}