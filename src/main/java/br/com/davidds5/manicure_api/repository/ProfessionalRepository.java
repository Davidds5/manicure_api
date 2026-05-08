package br.com.davidds5.manicure_api.repository;


import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<ProfessionalEntity, Long> {
    List<ProfessionalEntity> findByActiveTrue();
    Optional<ProfessionalEntity> findByEmail(String email);
}
