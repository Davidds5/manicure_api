-- V14: Ajustar constraint de email para ser unica por tenant (e nao global)
-- Permite que um mesmo cliente possa se cadastrar em saloes (tenants) diferentes

-- 1. Remove a constraint unica global criada originalmente na tabela clients
ALTER TABLE clients DROP CONSTRAINT IF EXISTS clients_email_key;

-- 2. Adiciona a constraint unica composta por tenant_id e email
ALTER TABLE clients ADD CONSTRAINT uk_clients_tenant_email UNIQUE (tenant_id, email);
