CREATE TABLE pets (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    date_of_birth DATE,
    sex VARCHAR(10),
    color VARCHAR(50),
    weight DECIMAL(5,2),
    microchip_id VARCHAR(50) UNIQUE,
    tattoo_number VARCHAR(50),
    photo_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    client_id UUID NOT NULL REFERENCES clients(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
