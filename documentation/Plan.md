# MediPet — Implementation & Deployment Plan

## Project Summary

MediPet is a Veterinary Clinic Management System — a **modular monolith** built with **hexagonal architecture** (ports & adapters), using Spring Boot 4.0.7, PostgreSQL, and a React frontend. It covers user/role management, clients, pets, appointments, medical records, vaccines, prescriptions, inventory, billing, reports, and dashboards — 47 functional requirements and 10 non-functional requirements.

**Current state**: Blank scaffold — Spring Boot entry point only, no business logic.

---

## Architecture

### Hexagonal Architecture (Ports & Adapters)

Each module is structured internally as:

```
module/
├── domain/
│   ├── model/          # Domain entities / value objects (pure Java, no framework annotations)
│   └── port/
│       ├── inbound/    # Use case interfaces (driven by the application)
│       └── outbound/   # Port interfaces for persistence, messaging, etc.
├── application/        # Use case implementations (orchestrate domain objects)
├── adapter/
│   ├── inbound/        # Controllers, REST DTOs, request/response mappers
│   └── outbound/
│       ├── persistence/ # JPA repositories, entities, mappers
│       └── messaging/   # Email sender, event publishers, etc.
└── config/             # Spring beans, module wiring
```

**Key rules:**
- Domain layer has **zero** framework dependencies (no Spring, no JPA annotations).
- Outbound ports define what persistence/messaging the domain needs — adapters implement them.
- Inbound ports define use cases — adapters (REST controllers) drive them.
- Modules communicate through their **inbound ports** (never directly call other modules' repositories).

### Modular Monolith

The application is split into **independent modules** inside one deployable artifact:

| Module | Domain |
|---|---|
| `user` | Users, roles, authentication |
| `client` | Clients (pet owners) |
| `pet` | Pets, medical history |
| `appointment` | Appointments, scheduling, calendar |
| `clinical` | Consultations, vaccines, prescriptions |
| `inventory` | Products, stock, alerts |
| `billing` | Invoices, payments |
| `report` | Dashboard, reports, exports |

Modules interact via **inbound port interfaces** (application services), keeping domain logic decoupled from infrastructure.

### Base Package Structure

```
com.medipet.medipet/
├── shared/                          # Cross-cutting: exception handling, pagination, audit
│   ├── exception/
│   ├── dto/
│   └── config/
├── module/
│   ├── user/
│   │   ├── domain/
│   │   ├── application/
│   │   ├── adapter/
│   │   └── config/
│   ├── client/
│   │   ├── domain/
│   │   ├── application/
│   │   ├── adapter/
│   │   └── config/
│   ├── pet/
│   ├── appointment/
│   ├── clinical/
│   ├── inventory/
│   ├── billing/
│   └── report/
└── MedipetApplication.java
```

---

## Phase 1 — Foundation

1. Configure `application.yaml` (datasource, JPA, JWT, actuator, server port)
2. Create base package structure with shared cross-cutting concerns:
   - Global exception handler (`@ControllerAdvice`) with structured error responses
   - Pagination support via `Pageable` on all list endpoints
   - Audit logging support (who did what, when)
3. Define base domain interfaces (outbound port for persistence)
4. Set up module package scaffolding for all 8 modules

## Phase 2 — Auth & Users (RF01–RF05)

5. **user** module — domain model (User, Role), use cases, JPA adapter, REST adapter
6. Security config — `SecurityFilterChain`, BCrypt password encoder, stateless sessions
7. JWT — token generation, validation, refresh token (adapter in `user.adapter.outbound`)
8. User CRUD with role assignment (Admin, Veterinario, Recepcionista)
9. Password change and email recovery (outbound port + email adapter)

## Phase 3 — Core Domain (RF06–RF21)

10. **client** module — CRUD, search by name/DNI
11. **pet** module — CRUD, link to client, "fallecida" status
12. **appointment** module — CRUD, status workflow, calendar view, overlap prevention
13. Email reminder — 24h-before-appointment notification (outbound port + scheduler adapter)

## Phase 4 — Clinical Features (RF22–RF33)

14. **clinical** module:
    - Medical records — created from completed appointments, file attachments (file storage port + adapter)
    - Vaccines — registration, 7-day expiry alert, vaccination card
    - Prescriptions — linked to consultation, medication details

## Phase 5 — Business (RF34–RF42)

15. **inventory** module — product CRUD, stock auto-deduction, low-stock & expiry alerts
16. **billing** module — invoice generation from consultations + products, payment states, PDF export (PDF port + adapter)

## Phase 6 — Reports & Dashboard (RF43–RF47)

17. **report** module — dashboard endpoints, report endpoints, PDF/Excel export
18. Read-only use cases that aggregate data from other modules via their outbound ports

## Phase 7 — Frontend (RNF02)

19. React SPA — login, dashboard, all CRUD views, responsive design

## Phase 8 — Deploy to Render

### Backend (Render Web Service)

20. `Dockerfile` (multi-stage: Maven build → JRE runtime) ✅ done
21. Add `render.yaml` (Blueprint IaC) for the web service
22. Use **Render Managed PostgreSQL** (not Docker Compose in prod)
23. Environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, JWT secret, SMTP credentials
24. Build command: `./mvnw clean package -DskipTests`
25. Start command: `java -jar target/medipet-0.0.1-SNAPSHOT.jar`

### Frontend (Render Static Site)

26. Static React build served from Render Static Site
27. API base URL as env var pointing to the backend service URL

### Database (Render PostgreSQL)

28. Create via Render dashboard or `render.yaml`
29. Connection string wired as env var to the backend service

---

## Open Questions

1. Backend first or backend + frontend together?
2. SMTP provider for email features (Gmail, SendGrid, etc.)?
3. `render.yaml` (IaC) or manual Render dashboard setup?
4. Flyway/Liquibase migrations or JPA auto-DDL for now?
