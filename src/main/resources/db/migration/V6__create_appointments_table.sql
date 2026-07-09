CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    cancel_reason VARCHAR(500),
    pet_id UUID NOT NULL REFERENCES pets(id),
    client_id UUID NOT NULL REFERENCES clients(id),
    veterinarian_id UUID NOT NULL REFERENCES users(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
