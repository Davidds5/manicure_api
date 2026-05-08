package br.com.davidds5.manicure_api.data;

import br.com.davidds5.manicure_api.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceData extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByPriceBetween(Double minPrice, Double maxPrice);

    @Query("SELECT s FROM ServiceEntity s ORDER BY s.price ASC")
    List<ServiceEntity> findCheapestFirst();

    @Query("SELECT AVG(s.price) FROM ServiceEntity s")
    Double getAveragePrice();
}