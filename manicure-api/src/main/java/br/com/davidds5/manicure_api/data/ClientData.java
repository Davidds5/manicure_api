package br.com.davidds5.manicure_api.data;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientData extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByEmail(String email);

    @Query("SELECT c FROM ClientEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ClientEntity> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM ClientEntity c ORDER BY c.createdAt DESC")
    Page<ClientEntity> findRecentClients(Pageable pageable);

    long countByEmail(String email);
}