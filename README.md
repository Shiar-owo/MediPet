# MediPet

[![CI](https://github.com/Shiar-owo/MediPet/actions/workflows/ci.yml/badge.svg)](https://github.com/Shiar-owo/MediPet/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Shiar-owo_MediPet&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Shiar-owo_MediPet)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Shiar-owo_MediPet&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Shiar-owo_MediPet)
[![GHCR](https://img.shields.io/badge/ghcr.io-medipet-2496ED?logo=docker&logoColor=white)](https://github.com/Shiar-owo/MediPet/pkgs/container/medipet)

Veterinary Clinic Management System — a modular monolith built with **hexagonal architecture** (ports & adapters), Spring Boot 4.0.7, PostgreSQL, and React.

---

## Status

| Module | Backend | Tests | Status |
|--------|---------|-------|--------|
| Shared infrastructure | Complete | 6 passing | ✅ |
| User/Auth | Complete | 56 passing | ✅ |
| Client | Complete | 31 passing | ✅ |
| Pet (+ photo upload) | Complete | 42 passing | ✅ |
| Appointment | Complete | 68 passing | ✅ |
| Clinical | Complete | 16 passing | ✅ |
| Dashboard | Complete | 7 passing | ✅ |
| Frontend (React) | Not started | — | 🔲 |
| Deploy (Render + Vercel) | Not started | — | 🔲 |

---

## Key Architectural Decisions

### Hexagonal Architecture (Ports & Adapters)

Each module follows the same internal structure:

```
module/
├── domain/
│   ├── model/              # Domain entities & value objects (pure Java)
│   └── port/
│       ├── inbound/        # Use case interfaces
│       └── outbound/       # Persistence & external service ports
├── application/            # Use case implementations
├── infrastructure/
│   ├── inbound/
│   │   ├── rest/           # Controllers, DTOs, mappers
│   │   └── security/       # JWT filter, authorization adapters
│   └── outbound/
│       ├── persistence/    # JPA entities, repos, adapters, mappers
│       └── security/       # JWT provider, BCrypt
└── config/                 # Module wiring
```

### Modular Monolith

All modules live under `com.medipet.medipet`:

| Module | Responsibility |
|--------|---------------|
| `shared` | Cross-cutting: ApiResponse, exceptions, GlobalExceptionHandler, audit, CORS, OpenAPI config, FileStoragePort |
| `user` | Auth, users, roles, JWT (access + refresh tokens) |
| `client` | Pet owners — CRUD, search by name/DNI |
| `pet` | Pets — CRUD, search by name/microchip/owner, photo upload |
| `appointment` | Scheduling — CRUD, cancel, overlap prevention, role-based access |
| `clinical` | Medical consultations — create linked to completed appointments, vitals, diagnosis, treatment, pet history |
| `dashboard` | Dashboard stats — client/pet/appointment counts, upcoming appointments |

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

### Role-Based Access Control

Three roles defined in `Role` enum:

| Role | Permissions |
|------|------------|
| `ADMIN` | Full access to all modules, user management |
| `VETERINARIO` | Create/update/cancel own appointments only. Can view all appointments. |
| `RECEPCIONISTA` | Full CRUD on clients, pets, and all appointments |

### Appointment Authorization Rules

Authorization is enforced in the **application layer** (use cases), not in the controller:

- **Create**: Vets can only create appointments for themselves (`CreateAppointmentUseCaseImpl.resolveVetId()`)
- **Update**: Vets can only update their own appointments (`UpdateAppointmentUseCaseImpl.enforceVeterinarianOwnership()`)
- **Cancel**: Vets can only cancel their own appointments (`CancelAppointmentUseCaseImpl.enforceVeterinarianOwnership()`)
- **View**: All roles can view any appointment (no read restrictions)
- **Overlap prevention**: `POST` and `PUT` check for time overlaps per veterinarian (`findOverlapping` query)

### Appointment Status Flow

```
PENDIENTE → CONFIRMADA → EN_ATENCION → COMPLETADA
    ↓                                    ↑
CANCELADA ←─────────────────────────────┘
```

Statuses: `PENDIENTE`, `CONFIRMADA`, `EN_ATENCION`, `COMPLETADA`, `CANCELADA`

### Cloudinary Photo Upload

Pet photos are stored in Cloudinary (not local filesystem):

- **Port**: `FileStoragePort` (domain interface)
- **Adapter**: `CloudinaryAdapter` (infrastructure)
- **Config**: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` env vars
- **Endpoint**: `POST /api/pets/{id}/photo` (multipart, max 5MB, JPEG/PNG/GIF/WebP)
- **Accepted types**: `FileStorageAdapter` validates content type and size before upload

---

## Tech Stack

- **Backend:** Spring Boot 4.0.7, Spring Security, Spring Data JPA, Hibernate
- **Database:** PostgreSQL 18, Flyway migrations (V1–V7)
- **Auth:** JWT (jjwt 0.12.6), BCrypt, stateless sessions, refresh token rotation
- **File storage:** Cloudinary (`cloudinary-core:2.3.0`, `cloudinary-http5:2.3.0`)
- **API docs:** SpringDoc OpenAPI 3.0 (`/swagger-ui.html`)
- **Testing:** JUnit 5, Mockito, Testcontainers, MockMvc
- **CI/CD:** GitHub Actions (Testcontainers-native, includes Docker build step)
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

No default credentials are stored in the repository. The initial admin is created
at startup by `AdminBootstrap` (an `ApplicationRunner`) from environment variables,
only when they are set **and** the account does not already exist:

```bash
MEDIPET_ADMIN_EMAIL=admin@medipet.com
MEDIPET_ADMIN_PASSWORD=<a-strong-password>
```

The runner executes once, after Flyway migrations have run. If the variables are
not set, no admin is created (and a warning is logged). Once the admin exists you
can remove these variables from the environment. Creating additional users still
requires the `ADMIN` role via `POST /api/auth/register`.

**Why bootstrap at startup instead of a Flyway seed?**

- **No secrets in source control**: A seeded admin would embed a password hash in a
  migration file, which is committed to the repository.
- **Bootstrap problem**: The `POST /api/auth/register` endpoint requires an existing
  `ADMIN`, so at least one admin must be created out-of-band. `AdminBootstrap` fills
  that gap using credentials supplied by the environment.
- **Idempotent & environment-agnostic**: The same mechanism works for local, CI and
  production; it simply does nothing when the variables are absent or the user exists.

### Required Environment Variables

```bash
# Database (for prod, Render provides DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD automatically)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/medipet
SPRING_DATASOURCE_USERNAME=medipet
SPRING_DATASOURCE_PASSWORD=medipet

# JWT
JWT_SECRET=your-secret-key
JWT_ACCESS_EXPIRATION=3600000    # 1 hour
JWT_REFRESH_EXPIRATION=604800000 # 7 days

# Initial admin bootstrap (optional; only needed to create the first admin)
MEDIPET_ADMIN_EMAIL=admin@medipet.com
MEDIPET_ADMIN_PASSWORD=a-strong-password

# CORS (comma-separated origins, defaults to localhost for dev)
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000

# Cloudinary (for photo upload)
CLOUDINARY_CLOUD_NAME=your-cloud
CLOUDINARY_API_KEY=your-key
CLOUDINARY_API_SECRET=your-secret
```

**Render-specific:**

```bash
# Profile (activates application-prod.yaml which reads DB_* vars)
SPRING_PROFILES_ACTIVE=prod

# CORS (set to your Vercel URL)
CORS_ALLOWED_ORIGINS=https://your-app.vercel.app
```

---

## API Endpoints

### Auth (`/api/auth`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | Public | Login, returns access + refresh tokens + role |
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

### Clients (`/api/clients`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/clients` | Create client (DNI unique, returns 201) |
| PUT | `/api/clients/{id}` | Update client (partial) |
| GET | `/api/clients/{id}` | Get client by ID |
| GET | `/api/clients/dni/{dni}` | Get client by DNI |
| GET | `/api/clients?q=&page=&size=` | List/search clients by name (paginated) |

### Pets (`/api/pets`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/pets` | Register pet (microchip unique, returns 201) |
| PUT | `/api/pets/{id}` | Update pet (partial) |
| GET | `/api/pets/{id}` | Get pet by ID |
| GET | `/api/pets/microchip/{microchipId}` | Get pet by microchip |
| GET | `/api/pets/client/{clientId}?page=&size=` | List pets by owner (paginated) |
| GET | `/api/pets?q=&page=&size=` | List/search pets by name (paginated) |
| POST | `/api/pets/{id}/photo` | Upload pet photo (multipart, max 5MB) |

### Consultations (`/api/consultations`) — VETERINARIO create, all roles view

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/consultations` | Create consultation from completed appointment (201) |
| GET | `/api/consultations/{id}` | Get consultation by ID |
| GET | `/api/consultations/appointment/{appointmentId}` | Get consultation by appointment |
| GET | `/api/consultations/pet/{petId}?page=&size=` | Get consultation history by pet (paginated) |

### Dashboard (`/api/dashboard`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | System-wide stats (totals + upcoming appointments) |

### Appointments (`/api/appointments`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Create appointment (201, overlap check) |
| PUT | `/api/appointments/{id}` | Update appointment (re-checks overlap) |
| PATCH | `/api/appointments/{id}/cancel` | Cancel appointment (with optional reason) |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| GET | `/api/appointments/client/{clientId}?page=&size=` | List by client |
| GET | `/api/appointments/pet/{petId}?page=&size=` | List by pet |
| GET | `/api/appointments/veterinarian/{vetId}?page=&size=` | List by veterinarian |
| GET | `/api/appointments?date=&page=&size=` | List all (optional date filter) |

---

## Project Structure

```
medipet/
├── .github/workflows/        # CI/CD (GitHub Actions)
├── documentation/            # Requirements, plan, rubric
├── src/main/java/
│   └── com/medipet/medipet/
│       ├── shared/           # ApiResponse, exceptions, audit, CORS, OpenAPI, FileStoragePort
│       ├── user/             # Auth & user management (roles: ADMIN, VETERINARIO, RECEPCIONISTA)
│       ├── client/           # Client CRUD & search
│       ├── pet/              # Pet CRUD & search, photo upload
│       ├── appointment/      # Scheduling, overlap prevention, role-based access
│       ├── clinical/         # Medical consultations (vitals, diagnosis, treatment, pet history)
│       └── dashboard/        # Dashboard stats (totals + upcoming appointments)
├── src/main/resources/
│   ├── application.yaml      # Config (Cloudinary, JWT, Swagger)
│   └── db/migration/         # V1–V7 (users, tokens, admin, clients, pets, appointments, consultations)
├── src/test/                 # 228 tests (unit + integration + context)
├── compose.yaml
├── Dockerfile                # Multi-stage build (Maven → JRE)
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

**Test coverage (228 tests):**

| Module | Test classes | Tests |
|--------|-------------|-------|
| User/Auth | 8 classes | 56 |
| Client | 5 classes | 31 |
| Pet | 5 classes | 38 |
| Photo upload | 1 class | 4 |
| Appointment | 8 classes | 68 |
| Clinical | 4 classes | 16 |
| Dashboard | 2 classes | 7 |
| Shared | 2 classes | 6 |
| Context | 1 class | 1 |
| **Total** | **36 classes** | **228** |

---

## Deployment

### Backend (Render)

The `Dockerfile` uses a multi-stage build (Maven build → JRE runtime). Deploy as a Render Web Service with a Managed PostgreSQL instance.

### Frontend (Vercel)

React SPA served as a static site. API base URL configured via `VITE_API_URL` environment variable.

### CI/CD (GitHub Actions)

- Testcontainers-native pipeline (no external Postgres service needed)
- Includes Docker build verification step
- Runs on every push and PR to `main`
