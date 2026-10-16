package br.com.davidds5.manicure_api.repository;


import br.com.davidds5.manicure_api.entity.ProfessionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<ProfessionalEntity, Long> {
    List<ProfessionalEntity> findByActiveTrue();
    List<ProfessionalEntity> findByTenantIdAndActiveTrue(Long tenantId);
    List<ProfessionalEntity> findByTenantId(Long tenantId);
    Optional<ProfessionalEntity> findByEmail(String email);
    Optional<ProfessionalEntity> findByEmailAndTenantId(String email, Long tenantId);
    Optional<ProfessionalEntity> findByEmailAndRole(String email, br.com.davidds5.manicure_api.enums.ProfessionalRole role);
    long countByTenantIdAndActiveTrue(Long tenantId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT p FROM ProfessionalEntity p WHERE p.id = :id")
    Optional<ProfessionalEntity> findByIdWithLock(@org.springframework.data.repository.query.Param("id") Long id);
}
