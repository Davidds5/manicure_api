package br.com.davidds5.manicure_api.repository;

import br.com.davidds5.manicure_api.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, Long> {
    boolean existsBySlug(String slug);
    Optional<TenantEntity> findBySlug(String slug);
}
