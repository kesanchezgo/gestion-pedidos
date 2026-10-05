# gestión-pedidos Constitution

## Core Principles

### I. Spec-first (NON-NEGOTIABLE)

Ninguna línea de código de producto sin una spec aprobada por un humano. Todo cambio funcional es una spec versionada en `specs/NNNN-*` con criterios de aceptación ejecutables. Las specs son el contrato; el código las implementa, no las redefine.

### II. Test-First y verificación con infraestructura real (NON-NEGOTIABLE)

Los tests acompañan o preceden al código. La integración **no se simula con mocks de la BD**: los tests de integración corren contra **Postgres real vía Testcontainers**. Un cambio sin sus tests (y sin los gates verdes) no existe.

### III. CI es el juez (el agente no se auto-verifica)

La verificación vive fuera del alcance del agente: CI desde checkout limpio, ramas protegidas, el agente no edita `.github/workflows/` ni tests críticos sin revisión, no se aprueba ni hace merge de su PR. Prohibido "arreglar" un fallo borrando el test, bajando umbrales o añadiendo exclusiones sin justificación.

### IV. Privilegio mínimo y secretos fuera del contexto

Secretos nunca en el repo ni en logs (gitleaks en pre-commit y CI). Pruebas de aislamiento con **valores señuelo**, nunca claves reales. La master key del gateway es una excepción documentada (§11 del flujo): mientras se use, no se declara cumplido el mínimo de privilegio.

### V. Simplicidad con intención (YAGNI + ADR)

Empezar simple: la estructura mínima que satisfaga la spec. Cualquier decisión estructural (patrón, esquema, dependencia nueva de peso) se registra como ADR en `docs/adr/` antes de implementarse. La complejidad se gana, no se acumula.

## Additional Constraints — Seguridad y calidad

- **Stack fijado**: Spring Boot 4.1.1 (JDK 25), React 19 + Vite 8 + TS, Postgres 18, pin por `.mise.toml` y lockfiles (`pnpm-lock.yaml`, Maven wrapper).
- **Validación de entrada** en la frontera (jakarta.validation) y **errores con ProblemDetail (RFC 7807)**; nunca exponer stack traces.
- **Autorización**: si la feature toca recursos de usuario, los tests incluyen el caso negativo (no-dueño denegado). En el piloto actual no hay auth (ver supuestos de la spec).
- **Dependencias**: escaneo de secretos + Trivy en CI; actualizar solo con revisión.

## Development Workflow — Gates de calidad

1. `spec-kit`: constitution (1×) → `/speckit-specify` → **aprobación humana de la spec** → plan → tasks.
2. Rama por cambio coherente = 1 PR (>400 líneas = alerta, no criterio). Conventional Commits.
3. Gates obligatorios antes del PR: backend `mise exec -- ./mvnw verify` (Failsafe ejecuta los `*IT`) · frontend `pnpm lint && pnpm typecheck && pnpm test && pnpm build` · `gitleaks dir .`.
4. Revisión humana proporcional al riesgo; el agente nunca se auto-aprueba.

## Governance

- Esta constitución supone toda otra práctica del proyecto; sus enmiendas requieren diff, aprobación humana y fecha.
- Cada PR declara su cumplimiento (reviewer lo verifica); la complejidad añadida debe justificarse contra el principio V.
- La guía de runtime para el agente vive en `AGENTS.md` (política de carga: §4.5 del flujo).

**Version**: 1.0.0 | **Ratified**: 2026-10-04 | **Last Amended**: 2026-10-04
