-- V15: Índices compostos de alta performance para o SaaS Multi-Tenant

-- 1. Consultas de agendamentos por tenant e intervalo de data/hora
CREATE INDEX IF NOT EXISTS idx_appointments_tenant_datetime 
ON appointments(tenant_id, date_time);

-- 2. Conflito de horários por profissional dentro do tenant
CREATE INDEX IF NOT EXISTS idx_appointments_tenant_prof_datetime 
ON appointments(tenant_id, professional_id, date_time);

-- 3. Agendamentos filtrados por status e tenant
CREATE INDEX IF NOT EXISTS idx_appointments_tenant_status 
ON appointments(tenant_id, status);

-- 4. Profissionais ativos por tenant
CREATE INDEX IF NOT EXISTS idx_professionals_tenant_active 
ON professionals(tenant_id, active);

-- 5. Serviços ativos por tenant
CREATE INDEX IF NOT EXISTS idx_services_tenant_active 
ON services(tenant_id, active);

-- 6. Busca de clientes por tenant e nome
CREATE INDEX IF NOT EXISTS idx_clients_tenant_name 
ON clients(tenant_id, name);
