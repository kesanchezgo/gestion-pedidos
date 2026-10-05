---
description: Task list for feature 0001-alta-pedido
---

# Tasks: Alta de pedido (cliente + líneas)

**Input**: Design documents from `/specs/0001-alta-pedido/`

**Prerequisites**: plan.md ✓, spec.md ✓ (aprobada), research.md ✓, data-model.md ✓, contracts/ ✓, quickstart.md ✓

**Tests**: **Requeridos explícitamente por FR-007 de la spec** → incluidos y escritos PRIMERO (deben FALLAR antes de implementar).

**Organization**: agrupados por user story (US1=P1, US2=P2, US3=P3) para implementación y validación independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: infra de build compartida que exige el plan (D6)

- [ ] T001 Añadir `maven-failsafe-plugin` (integration-test/verify, includes `**/*IT.java`) en `backend/pom.xml`
- [ ] T002 [P] Añadir `server.proxy` (`/api` → `:8080`) en `frontend/vite.config.ts` (D5)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: esquema y piezas base de las que cuelga toda user story

**⚠️ CRITICAL**: ninguna US puede empezar hasta completar esta fase

- [ ] T003 Migración `backend/src/main/resources/db/migration/V1__init.sql` (DDL de data-model.md)
- [ ] T004 [P] Entidad `backend/src/main/java/com/gestionpedidos/pedido/Pedido.java`
- [ ] T005 [P] Entidad `backend/src/main/java/com/gestionpedidos/pedido/LineaPedido.java`
- [ ] T006 [P] Excepción `backend/src/main/java/com/gestionpedidos/pedido/PedidoNotFoundException.java`
- [ ] T007 [P] Advice ProblemDetail `backend/src/main/java/com/gestionpedidos/pedido/ApiExceptionHandler.java` (RFC 7807, mapa `errors`)
- [ ] T008 [P] DTOs `PedidoRequest`/`LineaRequest`/`PedidoResponse`/`LineaResponse` (records con validación jakarta) en `backend/src/main/java/com/gestionpedidos/pedido/`
- [ ] T009 Repositorio `backend/src/main/java/com/gestionpedidos/pedido/PedidoRepository.java` (depende de T004/T005)

**Checkpoint**: fundación lista → las US pueden empezar

---

## Phase 3: User Story 1 - Crear un pedido (Priority: P1) 🎯 MVP

**Goal**: `POST /api/pedidos` crea el pedido con total calculado en servidor (FR-001..003)

**Independent Test**: curl POST con 2 líneas → 201 + `total=34.30`; sin líneas → 400 con `errors.lineas`

### Tests para US1 (FR-007 — ESCRIBIR PRIMERO y verlos FALLAR) ⚠️

- [ ] T010 [P] [US1] Unit test de cálculo e invariantes en `backend/src/test/java/com/gestionpedidos/pedido/PedidoTest.java` (total Σ, escala 2 HALF_UP, total del request ignorado) — **debe FALLAR sin la entidad/servicio**
- [ ] T011 [P] [US1] IT de alta en `backend/src/test/java/com/gestionpedidos/pedido/PedidosApiIT.java`: POST → 201 + total correcto; sin líneas → 400 `errors.lineas`; cantidad 0 → 400 `errors.lineas[0].cantidad` (Postgres real) — **debe FALLAR sin endpoint**

### Implementation for US1

- [ ] T012 [US1] `PedidoService.crearPedido(PedidoRequest)` en `backend/src/main/java/com/gestionpedidos/pedido/PedidoService.java` (recalcula total, persiste) — depende de T003..T009
- [ ] T013 [US1] `PedidoController` POST `/api/pedidos` → 201 + `Location` en `backend/src/main/java/com/gestionpedidos/pedido/PedidoController.java`
- [ ] T014 [US1] Verificar T010/T011 en verde (`mise exec -- ./mvnw verify`)

**Checkpoint**: API de alta funcional y testeable sin frontend

---

## Phase 4: User Story 2 - Consultar pedidos (Priority: P2)

**Goal**: `GET /api/pedidos/{id}` y `GET /api/pedidos` paginado (FR-005)

**Independent Test**: crear 3 pedidos → lista ordenada desc; id 999 → 404 ProblemDetail

### Tests para US2 (FR-007 — primero, en rojo) ⚠️

- [ ] T015 [P] [US2] Añadir a `PedidosApiIT.java`: GET 200 con pedido completo; GET 999 → 404 `type/problem+json`; lista con `totalElements` y orden `creadoEn` desc

### Implementation for US2

- [ ] T016 [US2] `PedidoService.obtener(id)` + `listar(Pageable)` en `PedidoService.java` (404 vía T006)
- [ ] T017 [US2] `PedidoController` GET `/api/pedidos/{id}` y GET `/api/pedidos` en `PedidoController.java`
- [ ] T018 [US1+US2] Verificar `mise exec -- ./mvnw verify` completo (unit + IT Failsafe)

**Checkpoint**: backend completo — US1 y US2 funcionan independientemente

---

## Phase 5: User Story 3 - Alta y consulta desde la web (Priority: P3)

**Goal**: formulario React con validación por campo + listado con total (FR-006)

**Independent Test**: en `http://localhost:5173`, alta válida → aparece en listado; email inválido → error en campo sin enviar

### Tests para US3 (FR-007 — primero, en rojo) ⚠️

- [ ] T019 [P] [US3] Vitest en `frontend/src/pages/FormularioAlta.test.tsx`: render del formulario + validación cliente bloquea envío con email inválido/cantidad 0
- [ ] T020 [P] [US3] Vitest en `frontend/src/App.test.tsx` (o `ListadoPedidos.test.tsx`): con `fetch` simulado, el alta correcta actualiza el listado mostrando el total

### Implementation for US3

- [ ] T021 [P] [US3] Cliente API `frontend/src/api/cliente.ts` (`crearPedido`, `listarPedidos`, parseo de `errors` de ProblemDetail)
- [ ] T022 [US3] Componente `frontend/src/pages/FormularioAlta.tsx` (líneas dinámicas, errores por campo del backend)
- [ ] T023 [US3] Componente `frontend/src/pages/ListadoPedidos.tsx`
- [ ] T024 [US3] Cablear en `frontend/src/App.tsx` (alta → refresca listado)
- [ ] T025 [US3] `pnpm lint && pnpm typecheck && pnpm test && pnpm build` en verde

**Checkpoint**: las 3 US funcionan de forma independiente

---

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T026 Validar `quickstart.md` completo: curl del escenario 201/400/404 y recorrido web
- [ ] T027 [P] Prueba SC-004: romper deliberadamente `calcularTotal()` → confirmar que falla T010/T011 → **revertir**
- [ ] T028 Batería final de gates: `mise exec -- ./mvnw verify` (comprobar log de Failsafe = los `*IT` corrieron) + gates frontend + `gitleaks dir .`
- [ ] T029 Actualizar checkboxes de este `tasks.md` y commit de la feature (Conventional Commits)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: sin dependencias → arranca ya
- **Foundational (Phase 2)**: depende de Setup (T001 antes de correr ITs; T002 no bloquea backend) — **BLOQUEA todas las US**
- **US1 (Phase 3)** → **US2 (Phase 4)** → **US3 (Phase 5)**: por prioridad P1→P2→P3 (US2/US3 podrían paralelizarse tras Fundacional)
- **Polish (Phase 6)**: requiere las 3 US completas

### Within Each User Story

- Tests PRIMERO y en rojo (FR-007) → implementación → checkpoint con gates verdes
- Entidades → repositorio → servicio → controlador

### Parallel Opportunities

- T004..T008 en paralelo (archivos distintos)
- T010 + T011 en paralelo; T015 aparte; T019 + T020 en paralelo
- T021 en paralelo con el backend de US2 (frontend ≠ backend)

---

## Implementation Strategy

**MVP primero**: Setup → Fundacional → US1 (checkpoint: curl 201/400) → US2 (checkpoint: 404/lista) → US3 (checkpoint: web) → Polish (gates + SC-004 + commit). En cada checkpoint se puede parar y demostrar el incremento.
