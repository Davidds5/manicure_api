package br.com.davidds5.manicure_api.repository;


import br.com.davidds5.manicure_api.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentRepository, Long> {


    @Query("SELECT a FROM AppointmentEntity a WHERE a.client.id = :clientId")
    Page<AppointmentEntity> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.professional.id = :professionalId")
    Page<AppointmentEntity> findByProfessionalId(@Param("professionalId") Long professionalId, Pageable pageable);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.professional.id = :professionalId AND a.dateTime = :dateTime AND a.status != 'CANCELLED'")
    List<AppointmentEntity> findByProfessionalAndDateTime(@Param("professionalId") Long professionalId,
                                                          @Param("dateTime") LocalDateTime dateTime);
}

