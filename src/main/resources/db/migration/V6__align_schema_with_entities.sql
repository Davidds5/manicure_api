ALTER TABLE services ADD COLUMN IF NOT EXISTS description VARCHAR(255) DEFAULT '';
ALTER TABLE services ADD COLUMN IF NOT EXISTS duration_minutes INTEGER DEFAULT 0;
ALTER TABLE services ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

-- PostgreSQL requires TYPE when changing column types
ALTER TABLE services
    ALTER COLUMN price TYPE DOUBLE PRECISION
    USING price::double precision;

ALTER TABLE payments
    ALTER COLUMN amount TYPE DOUBLE PRECISION
    USING amount::double precision;
