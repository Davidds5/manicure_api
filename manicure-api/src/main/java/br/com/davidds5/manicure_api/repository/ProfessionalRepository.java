package br.com.davidds5.manicure_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProfessionalRepository extends JpaRepository<ProfessionalRepository, Long> {
    List<ProfessionalRepository> findByActiveTrue();

}
