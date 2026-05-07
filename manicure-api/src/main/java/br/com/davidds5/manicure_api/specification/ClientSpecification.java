package br.com.davidds5.manicure_api.specification;

import br.com.davidds5.manicure_api.entity.ClientEntity;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecification {

    public static Specification<ClientEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<ClientEntity> emailContains(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
    
    public static Specification<ClientEntity> phoneContains(String phone) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }
}

 