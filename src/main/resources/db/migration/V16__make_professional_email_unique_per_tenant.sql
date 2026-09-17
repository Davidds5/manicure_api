-- V16: Ajustar constraint de email para ser única por tenant em professionals
-- Permite que um mesmo e-mail de profissional/administrador possa atuar em salões (tenants) distintos sem conflito global

-- 1. Remove a constraint única global em professionals (se existir)
ALTER TABLE professionals DROP CONSTRAINT IF EXISTS professionals_email_key;

-- 2. Adiciona a constraint única composta por tenant_id e email
ALTER TABLE professionals ADD CONSTRAINT uk_professionals_tenant_email UNIQUE (tenant_id, email);
