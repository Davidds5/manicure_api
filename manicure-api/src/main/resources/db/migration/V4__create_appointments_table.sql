CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    professional_id BIGINT NOT NULL REFERENCES professionals(id),
    service_id BIGINT NOT NULL REFERENCES services(id),
    date_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT chk_date_time CHECK (date_time > CURRENT_TIMESTAMP)
);