INSERT INTO users (id, email, password, first_name, last_name, role, active, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin@medipet.com',
    '$2b$10$SFA4L/OM4xp6M7FQPY7hhu38Cz1SR4SPk1UDlUJj/SilSKVSn8u7.',
    'Admin',
    'System',
    'ADMIN',
    true,
    NOW(),
    NOW()
);
