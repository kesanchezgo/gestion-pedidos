# Implementation Plan: Alta de pedido (cliente + líneas)

**Branch**: `0001-alta-pedido` | **Date**: 2026-10-04 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/0001-alta-pedido/spec.md`

## Summary

API REST de alta y consulta de pedidos (cliente + 1..* líneas, total calculado en servidor) + página React de alta/listado. Enfoque: Spring Boot 4.1.1 con validación en frontera y ProblemDetail (RFC 7807), esquema Flyway sobre Postgres 18 real en los tests (Testcontainers), y frontend Vite/React con proxy de desarrollo. Es la feature 1 del piloto: su objetivo real es validar el flujo spec → implementación → tests → PR con trazabilidad.

## Technical Context

**Language/Version**: Java 25 (Spring Boot 4.1.1) · TypeScript ~6.0 (React 19.2, Vite 8.3)

**Primary Dependencies**: spring-boot-starter-webmvc, -validation, -data-jpa, -flyway, -actuator, testcontainers-postgresql · react, @testing-library/react, vitest, oxlint

**Storage**: PostgreSQL 18 — Testcontainers (`postgres:latest`) en tests de integración; `docker-compose` en dev; Flyway gestiona el esquema (V1)

**Testing**: JUnit 5 + MockMvc + Testcontainers (`*IT` → Failsafe en `verify`) · Vitest + jsdom (frontend) · gitleaks

**Target Platform**: desarrollo Windows 11 (mise) · CI Linux (GitHub Actions) · navegador (chromium/firefox vía dev server)

**Project Type**: monorepo web-service + web frontend (`backend/` + `frontend/`)

**Performance Goals**: alta de pedido < 2 s (SC-001); batería de gates < 5 min desde checkout limpio (SC-003)

**Constraints**: total **solo en servidor** (FR-002) · errores ProblemDetail RFC 7807 con detalle por campo (FR-003) · sin autenticación en v1 (supuesto de la spec) · sin secretos en repo/logs (gitleaks)

**Scale/Scope**: 1 feature · 2 entidades · 3 endpoints · 1 formulario + 1 listado · sin carga esperada (piloto local)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio (constitution v1.0.0) | Veredicto | Evidencia |
|---|---|---|
| I. Spec-first | ✅ PASS | Spec 0001 aprobada por humano (commit `cd7d5d3`); no hay código previo |
| II. Test-First + infra real | ✅ PASS | Tareas ordenadas con tests antes/acompañando; ITs contra Postgres real (Testcontainers) |
| III. CI es el juez | ✅ PASS | `ci.yml` existe con jobs test-java/test-front/secret-scan; rama protegida al hacer push; el agente no edita workflows |
| IV. Privilegio mínimo y secretos | ✅ PASS | `.env` gitignorado, gitleaks pre-commit/CI, señuelos en pruebas de aislamiento |
| V. Simplicidad (YAGNI + ADR) | ✅ PASS | Sin dependencias nuevas de peso; sin ADR nuevo (decisiones estructurales ya fijadas por constitution/stack) |

**Resultado: 0 violaciones → Complexity Tracking vacío.** Re-ejecutado tras la Fase 1 (research, data-model, contracts, quickstart): sigue en **0 violaciones** — el diseño no añade dependencias, mantiene Postgres real en tests y no introduce secretos ni alcance fantasma.

## Project Structure

### Documentation (this feature)

```text
specs/0001-alta-pedido/
├── plan.md              # Este archivo
├── research.md          # Fase 0 — decisiones técnicas
├── data-model.md        # Fase 1 — entidades y esquema
├── quickstart.md        # Fase 1 — cómo ejecutar/demostrar
├── contracts/
│   └── pedidos-api.yaml # Fase 1 — contrato OpenAPI 3.1
├── spec.md              # Aprobada (gate de fase 2)
└── tasks.md             # Fase 2 — /speckit-tasks (NO creado por este comando)
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/gestionpedidos/
│   ├── pedido/          # entidad, repositorio, servicio, controlador, DTOs, excepción
│   └── BackendApplication.java
├── src/main/resources/
│   ├── db/migration/V1__init.sql   # Flyway: pedidos + lineas_pedido
│   └── application.properties
└── src/test/java/com/gestionpedidos/
    ├── pedido/          # *Test unitarios + PedidosApiIT (integración)
    └── TestcontainersConfiguration.java

frontend/
├── src/
│   ├── api/cliente.ts   # fetch contra /api (proxy de dev)
│   ├── pages/           # FormularioAlta, ListadoPedidos
│   └── App.tsx
├── vite.config.ts       # proxy /api → :8080 (evita CORS)
└── src/*.test.tsx       # Vitest
```

**Structure Decision**: Opción 2 (web application) — monorepo `backend/` + `frontend/` ya creado en la fase 0; esta feature añade el paquete `pedido` en backend y `pages/api` en frontend. No se crean proyectos nuevos.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| *(sin violaciones — 0 filas)* | — | — |
