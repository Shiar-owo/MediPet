# MediPet — Implementation & Deployment Plan

## Project Summary

MediPet is a Veterinary Clinic Management System — a **modular monolith** built with **hexagonal architecture** (ports & adapters), using Spring Boot 4.0.7, PostgreSQL, and a React frontend.

---

## Current Status

| Module | Backend | Tests | Status |
|--------|---------|-------|--------|
| Shared infrastructure | Complete | — | ✅ |
| User/Auth | Complete | 56 passing | ✅ |
| Client | Complete | 31 passing | ✅ |
| Pet (+ photo upload) | Complete | 42 passing | ✅ |
| Appointment | Complete | 68 passing | ✅ |
| Clinical | Complete | 16 passing | ✅ |
| Dashboard | Complete | 7 passing | ✅ |
| CI/CD (Sonar, OWASP, JMeter, GHCR) | Complete | — | ✅ |
| Frontend (React) | Not started | — | 🔲 |
| Deploy | Not started | — | 🔲 |

---

## Architecture

### Hexagonal Architecture (Ports & Adapters)

Each module is structured internally as:

```
module/
├── domain/
│   ├── model/          # Domain entities / value objects (pure Java)
│   └── port/
│       ├── inbound/    # Use case interfaces
│       └── outbound/   # Persistence & security ports
├── application/        # Use case implementations
├── infrastructure/
│   ├── inbound/
│   │   ├── rest/       # Controllers, DTOs, mappers
│   │   └── security/   # JWT filter
│   └── outbound/
│       ├── persistence/ # JPA entities, repos, adapters, mappers
│       └── security/   # JWT provider, BCrypt
└── config/             # SecurityConfig, module wiring
```

### Modular Monolith

All modules live under `com.medipet.medipet`:

| Module | Responsibility |
|--------|---------------|
| `shared` | Cross-cutting: ApiResponse, exceptions, audit, CORS |
| `user` | Auth, users, roles, JWT |
| `client` | Pet owners — CRUD, search by name/DNI |
| `pet` | Pets — CRUD, search by name, microchip, owner |
| `appointment` | Appointments — scheduling, calendar, overlap prevention |
| `clinical` | Consultations, vaccines, prescriptions (MVP simplified) |

### API Response Standard

All endpoints return a unified `ApiResponse<T>` wrapper:

```json
{ "success": true, "data": { ... } }
{ "success": true, "data": { "content": [...], "page": 0, "size": 20, "totalElements": 100, "totalPages": 5 } }
{ "success": false, "error": { "status": 404, "code": "NOT_FOUND", "message": "...", "path": "/api/...", "timestamp": "..." } }
```

---

## 5-Day MVP Plan

### Day 1 — Appointment Backend + Frontend Foundation

**Backend: Appointment Module (full)**
- Domain: `AppointmentId`, `AppointmentStatus` enum, `Appointment` model
- Ports: `CreateAppointmentUseCase`, `UpdateAppointmentUseCase`, `CancelAppointmentUseCase`, `AppointmentQueryService`
- Application: Implementations with overlap prevention (RF20)
- Persistence: `V6__create_appointments_table.sql`, `AppointmentEntity`, `AppointmentJpaRepository`, `AppointmentEntityMapper`, `AppointmentPersistenceAdapter`
- REST: `AppointmentController` with 8 endpoints (CRUD, cancel, query by client/pet/vet/date)
- Tests: ~30 tests (unit + integration + controller)

**Frontend: Foundation**
- Vite + React setup
- Project folder structure: `components/`, `pages/`, `hooks/`, `services/`, `context/`
- `AuthContext` — login/logout, JWT storage in localStorage, auto-attach token to requests
- Login page with form validation
- Protected route wrapper
- Axios/fetch service with base URL from `.env`

### Day 2 — Frontend Core Pages

**Client Pages**
- Client list (paginated, search by name)
- Create client form
- Edit client form
- Client detail → list of pets

**Pet Pages**
- Pet list (paginated, search by name)
- Create pet form (with client selector)
- Edit pet form
- Pet detail

**Shared Components**
- `DataTable` (reusable table with pagination)
- `FormInput` (reusable form field)
- `LoadingSpinner`, `ErrorMessage`
- `Modal` for confirmations

### Day 3 — Appointment Frontend + Clinical Backend

**Appointment Pages**
- Appointment list (paginated, filter by date)
- Create appointment form (client/pet/vet selectors, date/time picker)
- Edit appointment form
- Cancel appointment (with confirmation)
- Status badges (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)

**Backend: Clinical Module (simplified)**
- Domain: `Consultation` model (appointment link, symptoms, diagnosis, treatment, notes)
- Ports: `CreateConsultationUseCase`, `ConsultationQueryService`
- Persistence: `V7__create_consultations_table.sql`
- REST: `POST /api/consultations`, `GET /api/consultations/appointment/{appointmentId}`
- Tests: ~15 tests

### Day 4 — Dashboard + Polish

**Dashboard**
- Backend: Dashboard stats endpoint (`GET /api/dashboard/stats`)
  - Total clients, pets, appointments (this month), active appointments
  - Upcoming appointments (next 7 days)
- Frontend: Dashboard page with stat cards and upcoming appointments list

**Responsive Design**
- Tailwind CSS or CSS media queries
- Mobile-friendly navigation
- Responsive tables and forms
- Breakpoints: mobile (<640px), tablet (640-1024px), desktop (>1024px)

**Polish**
- Error handling on all API calls
- Loading states on all pages
- Empty states ("No clients found", etc.)
- Toast notifications for success/error actions

### Day 5 — Deploy + Final Testing

**Backend Deploy (Render)**
- Verify `Dockerfile` builds correctly
- Create Render Web Service
- Create Render Managed PostgreSQL
- Set environment variables (JWT secret, DB connection)
- Verify API endpoints via Swagger/OpenAPI

**Frontend Deploy (Vercel)**
- Configure `VITE_API_URL` in `.env`
- Build and deploy to Vercel
- Verify all pages work end-to-end

**Final Testing**
- Run full test suite (target: 180+ tests)
- Verify CORS works between Vercel frontend and Render backend
- Test login flow end-to-end
- Test CRUD operations for clients, pets, appointments
- Fix any remaining bugs

---

## API Endpoints (Completed)

### Auth (`/api/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login, returns tokens |
| POST | `/api/auth/register` | Create user (ADMIN) |
| POST | `/api/auth/refresh` | Refresh token |
| POST | `/api/auth/logout` | Invalidate token |

### Users (`/api/users`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List users |
| GET | `/api/users/{id}` | Get user |
| PUT | `/api/users/{id}` | Update user |
| PATCH | `/api/users/{id}/activate` | Activate |
| PATCH | `/api/users/{id}/deactivate` | Deactivate |

### Clients (`/api/clients`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/clients` | Create (201) |
| PUT | `/api/clients/{id}` | Update |
| GET | `/api/clients/{id}` | Get by ID |
| GET | `/api/clients/dni/{dni}` | Get by DNI |
| GET | `/api/clients` | List/search |

### Pets (`/api/pets`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/pets` | Register (201) |
| PUT | `/api/pets/{id}` | Update |
| GET | `/api/pets/{id}` | Get by ID |
| GET | `/api/pets/microchip/{microchipId}` | Get by microchip |
| GET | `/api/pets/client/{clientId}` | List by owner |
| GET | `/api/pets` | List/search |

## API Endpoints (Planned — Day 1)

### Appointments (`/api/appointments`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Create (201, overlap check) |
| PUT | `/api/appointments/{id}` | Update |
| PATCH | `/api/appointments/{id}/cancel` | Cancel |
| GET | `/api/appointments/{id}` | Get by ID |
| GET | `/api/appointments/client/{clientId}` | List by client |
| GET | `/api/pets/pet/{petId}` | List by pet |
| GET | `/api/appointments/veterinarian/{vetId}` | List by vet |
| GET | `/api/appointments?date=&page=&size=` | List by date range |

## API Endpoints (Planned — Day 3)

### Consultations (`/api/consultations`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/consultations` | Create from appointment |
| GET | `/api/consultations/appointment/{appointmentId}` | Get by appointment |

### Dashboard (`/api/dashboard`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | System-wide stats |

---

## Flyway Migrations

| Version | File | Description |
|---------|------|-------------|
| V1 | `V1__create_users_table.sql` | Users table |
| V2 | `V2__create_refresh_tokens_table.sql` | Refresh tokens |
| V3 | `V3__seed_admin.sql` | Default admin user |
| V4 | `V4__create_clients_table.sql` | Clients table |
| V5 | `V5__create_pets_table.sql` | Pets table |
| V6 | `V6__create_appointments_table.sql` | Appointments (Day 1) |
| V7 | `V7__create_consultations_table.sql` | Consultations (Day 3) |

---

## Deployment

### Backend (Render)
- `Dockerfile` — multi-stage build (Maven → JRE)
- Render Web Service + Managed PostgreSQL
- Env vars: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, JWT secret

### Frontend (Vercel)
- Vite + React SPA
- `VITE_API_URL` env var pointing to Render backend
- Auto-deploy from GitHub

---

## Test Coverage (Current: 126 tests)

| Test Class | Type | Tests |
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
| CreateClientUseCaseImplTest | Unit | 2 |
| UpdateClientUseCaseImplTest | Unit | 3 |
| ClientQueryServiceImplTest | Unit | 7 |
| ClientPersistenceAdapterTest | Integration | 7 |
| ClientControllerTest | WebMvc | 12 |
| CreatePetUseCaseImplTest | Unit | 3 |
| UpdatePetUseCaseImplTest | Unit | 5 |
| PetQueryServiceImplTest | Unit | 8 |
| PetPersistenceAdapterTest | Integration | 9 |
| PetControllerTest | WebMvc | 14 |
| MedipetApplicationTests | Context | 1 |
| **Total** | | **126** |

**Target after Day 5:** 180+ tests
