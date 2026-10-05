# Feature Specification: Alta de pedido (cliente + líneas)

**Feature Branch**: `0001-alta-pedido`

**Created**: 2026-10-04

**Status**: Draft — **pendiente de aprobación humana (gate de fase 2)**

**Input**: User description: "Primera feature del piloto: alta de pedido con cliente y líneas. Objetivo real: validar el flujo completo spec → implementación → tests → PR → staging con trazabilidad."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Crear un pedido desde el formulario (Priority: P1)

Como operador del back-office, relleno el formulario de alta con los datos del cliente (nombre y email) y al menos una línea (descripción, cantidad, precio unitario). Al enviar, el backend crea el pedido, calcula el total y lo devuelve.

**Why this priority**: es el núcleo de la feature y del piloto: sin alta no hay nada que consultar ni desplegar.

**Independent Test**: se puede probar solo con `POST /api/pedidos` (curl) y verificando 201 + total calculado; entrega valor sin frontend.

**Acceptance Scenarios**:

1. **Given** el servicio arriba, **When** `POST /api/pedidos` con cliente válido y 2 líneas, **Then** 201 con `id`, `estado=ABIERTO` y `total = Σ(cantidad × precioUnitario)`.
2. **Given** el servicio arriba, **When** `POST /api/pedidos` sin líneas, **Then** 400 ProblemDetail con error de campo `lineas`.
3. **Given** el servicio arriba, **When** `POST /api/pedidos` con cantidad `0`, **Then** 400 ProblemDetail con error en `lineas[0].cantidad`.

---

### User Story 2 - Consultar pedidos (Priority: P2)

Como operador, consulto la lista de pedidos y el detalle de uno por su id (para verificar que el alta quedó persistida).

**Why this priority**: sin lectura no hay evidencia de persistencia; es la base de cualquier E2E posterior.

**Independent Test**: dado un pedido creado, `GET /api/pedidos/{id}` devuelve 200 con ese pedido y `GET /api/pedidos` lo lista ordenado.

**Acceptance Scenarios**:

1. **Given** existe el pedido 1, **When** `GET /api/pedidos/1`, **Then** 200 con el pedido completo.
2. **Given** existe el pedido 1 y otro inexistente, **When** `GET /api/pedidos/999`, **Then** 404 ProblemDetail.
3. **Given** 3 pedidos creados, **When** `GET /api/pedidos?page=0&size=10`, **Then** 200 con los 3 en orden cronológico descendente.

---

### User Story 3 - Alta y consulta desde la web (Priority: P3)

Como operador, uso la página web: relleno el formulario, envío, y el pedido aparece en el listado con su total.

**Why this priority**: da la demostración visible del piloto y valida el stack completo (React ↔ API), pero la API sin UI ya tiene valor.

**Independent Test**: desde el navegador, alta + listado sin tocar la API a mano.

**Acceptance Scenarios**:

1. **Given** la app web, **When** relleno el formulario válido y envío, **Then** el pedido nuevo aparece en el listado con su total.
2. **Given** la app web, **When** envío el formulario con email inválido o línea con cantidad 0, **Then** el formulario muestra el error por campo y no se crea el pedido.

---

### Edge Cases

- Email con formato inválido → 400 con error de campo `clienteEmail` (validación en backend y reflejo en frontend).
- Cantidad no entera o < 1; precio ≤ 0 o con más de 2 decimales → 400 ProblemDetail.
- Líneas duplicadas en el mismo pedido → se aceptan (son renglones independientes; sin dedupe en v1).
- Pedido inexistente (GET id) → 404 con ProblemDetail, no 500.
- Total manipulado por el cliente en el body → se ignora; el backend recalcula siempre.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear un pedido con `clienteNombre`, `clienteEmail` y **1 o más** líneas (`descripcion`, `cantidad` ≥ 1 entero, `precioUnitario` > 0 con máx. 2 decimales).
- **FR-002**: El sistema MUST calcular `total` **siempre en el servidor** = Σ(cantidad × precioUnitario); el valor enviado por el cliente se ignora.
- **FR-003**: El sistema MUST validar la entrada y devolver **400 con ProblemDetail (RFC 7807)** con el detalle por campo ante cualquier violación.
- **FR-004**: El sistema MUST persistir en **Postgres** con esquema gestionado por Flyway (migración V1 incluida en esta feature).
- **FR-005**: El sistema MUST exponer `GET /api/pedidos/{id}` (200 | 404 ProblemDetail) y `GET /api/pedidos` (paginado `page`/`size`, orden cronológico descendente).
- **FR-006**: El frontend MUST implementar el formulario de alta con validación por campo, mostrar el total tras el alta correcta y listar los pedidos; ante 400 del backend muestra los errores por campo.
- **FR-007**: Los tests MUST cubrir: unitarios de cálculo/validación, **integración de API contra Postgres real (Testcontainers)** para FR-001..005, y unitarios del formulario (Vitest). Cada FR crítico tiene al menos un test que **falla si se rompe** la regla.

### Key Entities *(include if feature involves data)*

- **Pedido**: identidad, cliente (`clienteNombre`, `clienteEmail`), `estado` (nace `ABIERTO`), `total`, fecha de creación. Relación 1..* con líneas.
- **LineaPedido**: `descripcion`, `cantidad` ≥ 1, `precioUnitario` > 0; no existe sin su Pedido (borrado en cascada).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un pedido de hasta 20 líneas se da de alta correctamente (201 y total exacto) en < 2 s en local.
- **SC-002**: `mise exec -- ./mvnw verify` ejecuta los tests de integración **contra Postgres real** y termina en verde; el log demuestra que corrieron (no solo que no falló).
- **SC-003**: La batería de gates completa (`mvnw verify` + `lint && typecheck && test && build` + gitleaks) pasa en < 5 minutos desde checkout limpio.
- **SC-004**: Romper deliberadamente una regla de FR-001/FR-002 en el código hace fallar al menos un test (trazabilidad spec → test).

## Assumptions

- **Sin autenticación en v1**: piloto supervisado y local; roles/autorización quedan fuera (si se añaden, los tests negativos son obligatorios).
- **Cliente denormalizado**: no existe entidad `Cliente` en v1; los datos del cliente viven en el Pedido.
- **Fuera de alcance v1**: edición/borrado/cierre de pedidos, pagos, stock, notificaciones, multi-usuario, API pública.
- **E2E con Playwright**: postergado a la siguiente feature (v1 se valida con integración real + UI con Vitest).
- **Entorno**: Postgres 18 (Testcontainers en tests; `docker-compose` en dev), huso UTC-5.
