package br.com.davidds5.manicure_api.repository;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByEmail(String email);

    Page<ClientEntity> findByNameContaining(String name, Pageable pageable);
}