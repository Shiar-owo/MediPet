# MediPet

Veterinary Clinic Management System — a modular monolith built with **hexagonal architecture** (ports & adapters), Spring Boot 4.0.7, PostgreSQL, and React.

---

## Status

| Module | Backend | Tests | Status |
|--------|---------|-------|--------|
| Shared infrastructure | Complete | — | ✅ |
| User/Auth | Complete | 56 passing | ✅ |
| Client | Pending | — | 🔲 |
| Pet | Pending | — | 🔲 |
| Appointment | Pending | — | 🔲 |
| Clinical | Pending | — | 🔲 |
| File upload | Pending | — | 🔲 |
| Dashboard stats | Pending | — | 🔲 |
| Frontend (React) | Not started | — | 🔲 |
| Deploy (Render) | Not started | — | 🔲 |

---

## Architecture

### Hexagonal Architecture (Ports & Adapters)

Each module follows the same internal structure:

```
module/
├── domain/
│   ├── model/              # Domain entities & value objects (pure Java)
│   └── port/
│       ├── inbound/        # Use case interfaces
│       └── outbound/       # Persistence & security ports
├── application/            # Use case implementations
├── infrastructure/
│   ├── inbound/
│   │   ├── rest/           # Controllers, DTOs, mappers
│   │   ├── security/       # JWT filter
│   │   └── config/         # SecurityConfig
│   └── outbound/
│       ├── persistence/    # JPA entities, repos, adapters, mappers
│       └── security/       # JWT provider, BCrypt, UserDetailsService
```

### Modular Monolith

All modules live under `com.medipet.medipet`:

| Module | Responsibility |
|--------|---------------|
| `user` | Auth, users, roles, JWT |
| `client` | Pet owners (planned) |
| `pet` | Pets (planned) |
| `appointment` | Scheduling (planned) |
| `clinical` | Consultations (planned) |
| `shared` | Cross-cutting: ApiResponse, exceptions, audit, CORS |

### API Response Standard

All endpoints return a unified `ApiResponse<T>` wrapper:

```json
// Success
{ "success": true, "data": { ... } }

// Paginated
{ "success": true, "data": { "content": [...], "page": 0, "size": 20, "totalElements": 100, "totalPages": 5 } }

// Error
{ "success": false, "error": { "status": 404, "code": "NOT_FOUND", "message": "...", "path": "/api/...", "timestamp": "..." } }
```

---

## Tech Stack

- **Backend:** Spring Boot 4.0.7, Spring Security, Spring Data JPA, Hibernate
- **Database:** PostgreSQL, Flyway migrations
- **Auth:** JWT (jjwt 0.12.6), BCrypt, stateless sessions
- **Testing:** JUnit 5, Mockito, Testcontainers, MockMvc
- **CI/CD:** GitHub Actions
- **Deploy:** Render (Web Service + PostgreSQL), Vercel (frontend)
- **Frontend:** React + Vite (planned)

---

## Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven (or use `./mvnw`)

### Run with Docker Compose

```bash
docker compose up
```

This starts:
- PostgreSQL on `localhost:5432`
- The app on `localhost:8080`

### Run locally

```bash
# Start PostgreSQL
docker compose up -d postgres

# Run the app
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### Default Admin User

Seeded via Flyway migration (`V3__seed_admin.sql`):

- **Email:** `admin@medipet.com`
- **Password:** `admin123`

Change the password on first login. The `POST /api/auth/register` endpoint is protected and requires `ADMIN` role — only the admin can create new user accounts.

---

## API Endpoints

### Auth (`/api/auth`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | Public | Login, returns access + refresh tokens |
| POST | `/api/auth/register` | ADMIN | Create new user |
| POST | `/api/auth/refresh` | Public | Exchange refresh token for new pair |
| POST | `/api/auth/logout` | Public | Invalidate refresh token |

### Users (`/api/users`) — ADMIN only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users?page=&size=` | List users (paginated) |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user profile |
| PATCH | `/api/users/{id}/activate` | Activate user |
| PATCH | `/api/users/{id}/deactivate` | Deactivate user |

---

## Project Structure

```
medipet/
├── .github/workflows/      # CI/CD
├── documentation/           # Requirements, plan, rubric
├── src/main/java/
│   └── com/medipet/medipet/
│       ├── shared/          # ApiResponse, exceptions, audit, CORS
│       ├── user/            # Auth & user management (complete)
│       ├── client/          # (planned)
│       ├── pet/             # (planned)
│       ├── appointment/     # (planned)
│       └── clinical/        # (planned)
├── src/main/resources/
│   ├── application.yaml
│   ├── application-dev.yaml
│   └── db/migration/        # V1–V3 (users, refresh_tokens, admin seed)
├── src/test/                # 56 tests (unit + integration)
├── compose.yaml
├── Dockerfile
└── pom.xml
```

---

## Testing

```bash
# Run all tests (requires Docker for Testcontainers)
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AuthControllerTest

# Run a specific test method
./mvnw test -Dtest=AuthControllerTest#shouldLoginSuccessfully
```

**Test coverage:**

| Test class | Type | Tests |
|------------|------|-------|
| CreateUserUseCaseImplTest | Unit | 2 |
| ChangePasswordUseCaseImplTest | Unit | 3 |
| AuthServiceImplTest | Unit | 6 |
| JwtTokenProviderTest | Unit | 7 |
| SecurityUserDetailsServiceTest | Unit | 3 |
| BcryptPasswordAdapterTest | Unit | 4 |
| JwtAuthenticationFilterTest | Unit | 5 |
| AuthControllerTest | WebMvc | 5 |
| UserControllerTest | WebMvc | 6 |
| UserPersistenceAdapterTest | Integration | 8 |
| RefreshTokenPersistenceAdapterTest | Integration | 6 |
| MedipetApplicationTests | Context | 1 |
| **Total** | | **56** |

---

## Deployment

### Backend (Render)

The `Dockerfile` uses a multi-stage build (Maven build → JRE runtime). Deploy as a Render Web Service.

### Frontend (Vercel)

React SPA served as a static site. API base URL configured via `VITE_API_URL` environment variable.

### Database (Render PostgreSQL)

Managed PostgreSQL instance. Connection string wired as env vars to the backend.
