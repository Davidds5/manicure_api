package br.com.davidds5.manicure_api.config;

/**
 * Contexto de Tenant da requisição atual.
 * Utiliza ThreadLocal para armazenar com segurança o tenant_id isolado por thread.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
