-- 1. Inserir o primeiro Tenant padrão (legado) para associar aos dados já existentes
INSERT INTO tenants (name, slug, plan, status, brand_color, created_at)
VALUES ('Salão Matriz', 'salao-matriz', 'PRO', 'ACTIVE', '#000000', NOW())
ON CONFLICT (slug) DO NOTHING;

-- 2. Adicionar coluna tenant_id nas tabelas existentes permitindo temporariamente nulo para popular
ALTER TABLE clients ADD COLUMN IF NOT EXISTS tenant_id BIGINT;
ALTER TABLE professionals ADD COLUMN IF NOT EXISTS tenant_id BIGINT;
ALTER TABLE services ADD COLUMN IF NOT EXISTS tenant_id BIGINT;
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS tenant_id BIGINT;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS tenant_id BIGINT;

-- 3. Migrar os dados existentes para o Tenant 1 (Salão Matriz)
UPDATE clients SET tenant_id = (SELECT id FROM tenants WHERE slug = 'salao-matriz' LIMIT 1) WHERE tenant_id IS NULL;
UPDATE professionals SET tenant_id = (SELECT id FROM tenants WHERE slug = 'salao-matriz' LIMIT 1) WHERE tenant_id IS NULL;
UPDATE services SET tenant_id = (SELECT id FROM tenants WHERE slug = 'salao-matriz' LIMIT 1) WHERE tenant_id IS NULL;
UPDATE appointments SET tenant_id = (SELECT id FROM tenants WHERE slug = 'salao-matriz' LIMIT 1) WHERE tenant_id IS NULL;
UPDATE payments SET tenant_id = (SELECT id FROM tenants WHERE slug = 'salao-matriz' LIMIT 1) WHERE tenant_id IS NULL;

-- 4. Definir owner_id do Salão Matriz como o primeiro professional/admin existente
UPDATE tenants SET owner_id = (SELECT id FROM professionals ORDER BY id ASC LIMIT 1)
WHERE slug = 'salao-matriz' AND owner_id IS NULL;

-- 5. Tornar tenant_id NOT NULL e adicionar Foreign Keys
ALTER TABLE clients ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE clients ADD CONSTRAINT fk_clients_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

ALTER TABLE professionals ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE professionals ADD CONSTRAINT fk_professionals_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

ALTER TABLE services ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE services ADD CONSTRAINT fk_services_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

ALTER TABLE appointments ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

ALTER TABLE payments ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE payments ADD CONSTRAINT fk_payments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

-- 6. Índices para performance e otimização das consultas multi-tenant
CREATE INDEX idx_clients_tenant_id ON clients(tenant_id);
CREATE INDEX idx_professionals_tenant_id ON professionals(tenant_id);
CREATE INDEX idx_services_tenant_id ON services(tenant_id);
CREATE INDEX idx_appointments_tenant_id ON appointments(tenant_id);
CREATE INDEX idx_payments_tenant_id ON payments(tenant_id);
