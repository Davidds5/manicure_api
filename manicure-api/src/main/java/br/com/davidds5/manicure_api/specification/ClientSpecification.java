package br.com.davidds5.manicure_api.specification;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecification {

public static Specification<ClientEntity> nameContains(String name){
   return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
     "%"+name.toLowerCase()+"%");

}
}

 