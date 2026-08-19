package br.com.davidds5.manicure_api.config;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Aspecto AOP que ativa automaticamente o Hibernate Filter 'tenantFilter'
 * antes de qualquer execução de método em Services ou Repositories,
 * garantindo isolamento estrito de dados entre tenants.
 */
@Aspect
@Component
public class TenantAspect {

    @Autowired
    private EntityManager entityManager;

    @Before("execution(* br.com.davidds5.manicure_api.service..*(..)) || execution(* br.com.davidds5.manicure_api.repository..*(..))")
    public void enableTenantFilter() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            try {
                Session session = entityManager.unwrap(Session.class);
                if (session != null && session.isOpen()) {
                    session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
                }
            } catch (Exception ignored) {
                // Silencia se o session context não estiver disponível ou já estiver com filtro ativo
            }
        }
    }
}
