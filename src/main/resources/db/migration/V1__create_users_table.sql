CREATE TABLE users (
    id              UUID            PRIMARY KEY,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    role            VARCHAR(50)     NOT NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    phone           VARCHAR(50),
    address         TEXT,
    date_of_birth   DATE,
    avatar          VARCHAR(500),
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);
