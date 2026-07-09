CREATE TABLE refresh_tokens (
    id              UUID            PRIMARY KEY,
    token           VARCHAR(500)    NOT NULL UNIQUE,
    user_email      VARCHAR(255)    NOT NULL,
    expires_at      TIMESTAMP       NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);
