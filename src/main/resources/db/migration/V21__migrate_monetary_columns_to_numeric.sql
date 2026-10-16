-- V21: Migrar colunas de valores monetários de DOUBLE PRECISION para NUMERIC(10,2)
ALTER TABLE services
    ALTER COLUMN price TYPE NUMERIC(10,2)
    USING ROUND(price::numeric, 2);

ALTER TABLE payments
    ALTER COLUMN amount TYPE NUMERIC(10,2)
    USING ROUND(amount::numeric, 2);
