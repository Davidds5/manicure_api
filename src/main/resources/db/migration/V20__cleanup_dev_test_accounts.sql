-- V20: Limpeza segura de contas de teste injetadas em ambiente de desenvolvimento
-- Preserva os dados caso o salão ou profissional já possuam agendamentos reais em produção
DELETE FROM services 
WHERE tenant_id IN (
    SELECT id FROM tenants 
    WHERE slug = 'studio-bella' 
      AND NOT EXISTS (SELECT 1 FROM appointments WHERE appointments.tenant_id = tenants.id)
);

DELETE FROM subscriptions 
WHERE tenant_id IN (
    SELECT id FROM tenants 
    WHERE slug = 'studio-bella' 
      AND NOT EXISTS (SELECT 1 FROM appointments WHERE appointments.tenant_id = tenants.id)
);

DELETE FROM professionals 
WHERE email = 'teste@salao.com' 
  AND NOT EXISTS (SELECT 1 FROM appointments WHERE appointments.professional_id = professionals.id);

DELETE FROM tenants 
WHERE slug = 'studio-bella' 
  AND NOT EXISTS (SELECT 1 FROM appointments WHERE appointments.tenant_id = tenants.id);

