-- V20: Limpeza de contas de teste injetadas em ambiente de desenvolvimento
DELETE FROM services WHERE tenant_id IN (SELECT id FROM tenants WHERE slug = 'studio-bella');
DELETE FROM professionals WHERE email = 'teste@salao.com';
DELETE FROM subscriptions WHERE tenant_id IN (SELECT id FROM tenants WHERE slug = 'studio-bella');
DELETE FROM tenants WHERE slug = 'studio-bella';
