# MediPet — Implementation & Deployment Plan

## Project Summary

MediPet is a Veterinary Clinic Management System with a Spring Boot 4.0.7 backend, PostgreSQL database, and React frontend. It covers user/role management, clients, pets, appointments, medical records, vaccines, prescriptions, inventory, billing, reports, and dashboards — 47 functional requirements and 10 non-functional requirements.

**Current state**: Blank scaffold — Spring Boot entry point only, no business logic.

---

## Phase 1 — Foundation

1. Configure `application.yaml` (datasource, JPA, JWT, actuator, server port)
2. Create package structure: `controller/`, `service/`, `repository/`, `model/`, `dto/`, `config/`, `security/`, `exception/`
3. Define JPA entities: User, Client, Pet, Appointment, MedicalRecord, Vaccine, Prescription, Product, Invoice, InvoiceItem
4. Global exception handler (`@ControllerAdvice`) with structured error responses
5. Pagination support via `Pageable` on all list endpoints

## Phase 2 — Auth & Users (RF01–RF05)

6. Security config — `SecurityFilterChain`, BCrypt password encoder, stateless sessions
7. JWT — token generation, validation, refresh token
8. User CRUD with role assignment (Admin, Veterinario, Recepcionista)
9. Password change and email recovery (Spring Mail / SMTP)

## Phase 3 — Core Domain (RF06–RF21)

10. Client module — CRUD, search by name/DNI
11. Pet module — CRUD, link to client, "fallecida" status
12. Appointment module — CRUD, status workflow, calendar view, overlap prevention
13. Email reminder — 24h-before-appointment notification

## Phase 4 — Clinical Features (RF22–RF33)

14. Medical records — created from completed appointments, file attachments
15. Vaccines — registration, 7-day expiry alert, vaccination card
16. Prescriptions — linked to consultation, medication details

## Phase 5 — Business (RF34–RF42)

17. Inventory — product CRUD, stock auto-deduction, low-stock & expiry alerts
18. Billing — invoice generation from consultations + products, payment states, PDF export

## Phase 6 — Reports & Dashboard (RF43–RF47)

19. Dashboard endpoints — daily appointments, monthly income, pets attended
20. Report endpoints — revenue by date, appointments by vet, most-used products
21. PDF/Excel export (JasperReports / Apache POI)

## Phase 7 — Frontend (RNF02)

22. React SPA — login, dashboard, all CRUD views, responsive design

## Phase 8 — Deploy to Render

### Backend (Render Web Service)

23. Add `Dockerfile` (multi-stage: Maven build → JRE runtime)
24. Add `render.yaml` (Blueprint IaC) for the web service
25. Use **Render Managed PostgreSQL** (not Docker Compose in prod)
26. Environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, JWT secret, SMTP credentials
27. Build command: `./mvnw clean package -DskipTests`
28. Start command: `java -jar target/medipet-0.0.1-SNAPSHOT.jar`

### Frontend (Render Static Site)

29. Static React build served from Render Static Site
30. API base URL as env var pointing to the backend service URL

### Database (Render PostgreSQL)

31. Create via Render dashboard or `render.yaml`
32. Connection string wired as env var to the backend service

---

## Open Questions

1. Backend first or backend + frontend together?
2. SMTP provider for email features (Gmail, SendGrid, etc.)?
3. `render.yaml` (IaC) or manual Render dashboard setup?
4. Flyway/Liquibase migrations or JPA auto-DDL for now?
