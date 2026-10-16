-- V22: Adiciona coluna deleted_at na tabela tenants para soft-delete
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- Altera ON DELETE CASCADE para RESTRICT nas tabelas financeiras e de histórico crítico
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS fk_appointments_tenant;
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_tenant
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE RESTRICT;

ALTER TABLE payments DROP CONSTRAINT IF EXISTS fk_payments_tenant;
ALTER TABLE payments ADD CONSTRAINT fk_payments_tenant
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE RESTRICT;
