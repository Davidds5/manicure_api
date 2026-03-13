package br.com.davidds5.manicure_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientRepository, Long> {

    Optional<ClientRepository> findByEmail(String email);
    Page<ClientRepository> findByNameContaining(String name, Pageable pageable);
}
