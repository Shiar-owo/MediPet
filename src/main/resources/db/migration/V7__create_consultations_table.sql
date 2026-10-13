CREATE TABLE consultations (
    id UUID PRIMARY KEY,
    appointment_id UUID NOT NULL REFERENCES appointments(id),
    pet_id UUID NOT NULL REFERENCES pets(id),
    client_id UUID NOT NULL REFERENCES clients(id),
    veterinarian_id UUID NOT NULL REFERENCES users(id),
    weight NUMERIC(6,2),
    temperature NUMERIC(4,1),
    symptoms TEXT,
    diagnosis TEXT,
    treatment TEXT,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_consultations_pet_id ON consultations(pet_id);
CREATE INDEX idx_consultations_appointment_id ON consultations(appointment_id);
