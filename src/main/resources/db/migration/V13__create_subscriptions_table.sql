CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    plan VARCHAR(30) NOT NULL DEFAULT 'FREE',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    max_professionals INT NOT NULL DEFAULT 1,
    max_appointments_per_month INT NOT NULL DEFAULT 50,
    started_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    next_billing_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_subscriptions_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

CREATE INDEX idx_subscriptions_tenant_id ON subscriptions(tenant_id);

-- Cria assinatura inicial para o Tenant 1 (Salão Matriz)
INSERT INTO subscriptions (tenant_id, plan, status, max_professionals, max_appointments_per_month, started_at)
SELECT id, 'PRO', 'ACTIVE', 10, 1000, NOW()
FROM tenants WHERE slug = 'salao-matriz'
ON CONFLICT (tenant_id) DO NOTHING;
