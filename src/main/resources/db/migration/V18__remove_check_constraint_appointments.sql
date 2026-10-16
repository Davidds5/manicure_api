-- V18: Remover a constraint chk_date_time da tabela appointments
-- Essa constraint impedia o UPDATE de agendamentos no passado (ex: marcar como COMPLETED ou registrar pagamento)
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS chk_date_time;
