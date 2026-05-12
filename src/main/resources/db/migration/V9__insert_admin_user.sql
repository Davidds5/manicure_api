ALTER TABLE professionals 
ADD COLUMN IF NOT EXISTS email VARCHAR(100) NOT NULL DEFAULT 'sem-email@exemplo.com' UNIQUE;

INSERT INTO professionals(name, specialty, active, email, password)
VALUES ('Admin', 'Admin', true, 'admin@salao.com', '$2a$12$WSj/vl6nPeQEnC5GjW3cH.L02EeF6ehmVoekXQQA.Ru.xnYJKRR9.');