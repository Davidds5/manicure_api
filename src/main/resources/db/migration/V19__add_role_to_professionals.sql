-- V19: Adiciona coluna role na tabela professionals e define roles adequadas
ALTER TABLE professionals ADD COLUMN IF NOT EXISTS role VARCHAR(30) NOT NULL DEFAULT 'PROFESSIONAL';

-- 1. Promove a TENANT_ADMIN o profissional mais antigo (proprietário/primeiro criado) de cada tenant
UPDATE professionals p
SET role = 'TENANT_ADMIN'
WHERE p.id IN (
    SELECT MIN(p2.id)
    FROM professionals p2
    GROUP BY p2.tenant_id
);

-- 2. Define SUPER_ADMIN para o administrador global da plataforma
UPDATE professionals
SET role = 'SUPER_ADMIN'
WHERE email = 'admin@salao.com';
