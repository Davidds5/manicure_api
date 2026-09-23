-- V17: Adicionar coluna pix_key na tabela tenants
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS pix_key VARCHAR(150);
