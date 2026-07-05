# MediPet

## Authentication

### Default Admin User

The first admin user is seeded via Flyway migration (`V3__seed_admin.sql`):

- **Email:** `admin@medipet.com`
- **Password:** `admin123`

The password hash was pre-computed with BCrypt (`$2b$10$...`) and inserted directly in the SQL migration. This avoids needing startup scripts or public registration endpoints.

Change the password on first login. The `POST /api/auth/register` endpoint is protected and requires `ADMIN` role — only the admin can create new user accounts.
