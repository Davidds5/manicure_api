package br.com.davidds5.manicure_api.repository;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long>, JpaSpecificationExecutor<ClientEntity> {

    @org.springframework.data.jpa.repository.Query("select c from ClientEntity c where c.id = :id")
    Optional<ClientEntity> findById(@org.springframework.data.repository.query.Param("id") Long id);

    Optional<ClientEntity> findByEmail(String email);

    Page<ClientEntity> findByNameContaining(String name, Pageable pageable);
}