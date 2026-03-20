package br.com.davidds5.manicure_api.data;

import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionalData extends JpaRepository<ProfessionalEntity, Long> {

    List<ProfessionalEntity> findByActiveTrue();

    @Query("SELECT p FROM ProfessionalEntity p WHERE LOWER(p.specialty) = LOWER(:specialty)")
    List<ProfessionalEntity> findBySpecialty(@Param("specialty") String specialty);

    @Query("SELECT COUNT(p) FROM ProfessionalEntity p WHERE p.active = true")
    long countActiveProfessionals();
}