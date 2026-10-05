# AGENTS.md — gestión-pedidos

Reglas permanentes del agente. Léelas antes de cualquier cambio.
(Cargado vía `CLAUDE.md` con `@AGENTS.md`: existe `chat/CLAUDE.md` encima en la ruta, así que la lectura directa de este fichero quedaría tapada — política única del flujo.)

## Qué es

CRUD de pedidos: **Spring Boot 4.1 (JDK 25)** + **React 19 (Vite, TS)** + Postgres.
Spec-driven con spec-kit (constitution en `.specify/memory/constitution.md`).

## Orden de trabajo

1. **Sin spec aprobada no hay código**: `/speckit-specify → plan → tasks` (constitution 1× por proyecto).
2. **Un cambio coherente y revisable = una rama = un PR** (>400 líneas = alerta, no criterio).
3. **Loop acotado**: máx. 3 reparaciones del mismo fallo, presupuesto de tiempo/consumo, escalar si no hay progreso. **Prohibido relajar gates para terminar.**

## Gates (obligatorios)

- Backend: `./mvnw verify` — debe **ejecutar** los tests de verdad (Failsafe: `*IT` en integration-test/verify); comprobar en el log que corrieron, no solo que salió verde.
- Frontend: `pnpm lint && pnpm typecheck && pnpm test --run && pnpm build`.
- Secretos: `.env` gitignorado y bloqueado; **nunca** leer `HKCU\Environment` ni señuelos.
- Prohibido: borrar tests para pasar, bajar umbrales, editar `.github/workflows/` sin revisión.

## Convenciones

- Java: paquete `com.gestionpedidos`, `record` para DTOs, validación de entrada con jakarta.validation, errores con ProblemDetail (RFC 7807).
- Tests: JUnit 5 + **Testcontainers (Postgres real)** para integración; ArchUnit para arquitectura. Front: Vitest + Testing Library; Playwright para E2E crítico.
- Commits: Conventional Commits (`feat`/`fix`/`chore`…).
- Identidad: opera con credenciales de automatización limitadas; **nunca** se auto-aprueba ni hace merge de su PR.

## Decisiones ya tomadas (no rediscutir)

- Gateway: LiteLLM :4000 → gmifree :6660 (nunca gmifree directo desde la CLI).
- Stack: Spring Boot 4.1.1 + JDK 25 + Node 24 (pin en `.mise.toml`); Maven Failsafe para `*IT`.
