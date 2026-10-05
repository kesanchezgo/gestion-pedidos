# Implementation Plan: Aislamiento y credenciales con señuelo (0002)

**Branch**: `0002-aislamiento-credenciales` | **Date**: 2026-10-05 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/0002-aislamiento-credenciales/spec.md`

## Summary

Cerrar el bloque 1 de evidencia: comprobar ocultamiento SCRUB en subprocessos, bloqueo/detección con señuelos (sin claves reales) y alcance de credencial limitada. Enfoque: solo valores ficticios únicos, nada commiteado, evidencia reproducible con comandos y salidas.

## Technical Context

**Language/Version**: PowerShell 5.1+ / Python 3.10+ para sondas; Spring Boot 4.1.1 y React 19 sin cambios de producto

**Primary Dependencies**: LiteLLM 1.104.0 (gateway :4000), gmifree :6660, gitleaks 8.x

**Storage**: N/A (sin cambios de datos; señuelos solo en memoria/archivos temporales fuera del repo)

**Testing**: Comandos de verificación manual + `gitleaks dir`, sin framework nuevo

**Target Platform**: Windows 11 local, repo `gestion-pedidos`

**Project Type**: web application existente (backend/ + frontend/), este bloque no añade código de producto

**Performance Goals**: N/A (pruebas de control, no de carga)

**Constraints**: Solo señuelos, nunca claves reales; ningún log/trace con valores; no modificar protección de `main`; no commitear señuelos

**Scale/Scope**: 1 rama, 0 cambios de producto, 3 historias de evidencia

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio | Veredicto | Evidencia |
|---|---|---|
| I. Spec-first | PASS | spec 0002 aprobada por humano antes de este plan |
| II. Test-First + infra real | PASS adaptado | son pruebas de control con comandos reproducibles, no mocks de BD |
| III. CI es el juez | PASS | la evidencia final se valida en CI/PR, no solo local |
| IV. Privilegio mínimo y secretos | PASS | solo señuelos; nada real en repo/logs |
| V. Simplicidad | PASS | sin dependencias nuevas, sin ADR nuevo |

**Resultado: 0 violaciones.**

## Project Structure

### Documentation (this feature)

```text
specs/0002-aislamiento-credenciales/
├── spec.md              # aprobada
├── plan.md              # este archivo
├── research.md          # Fase 0 (requisito BD virtual keys + estado gateway)
├── tasks.md             # Fase 2 (lista ejecutable)
└── evidencia/           # salidas redactadas (sin valores)
```

### Source Code (repository root)

Sin cambios de producto en esta feature. Solo docs + scripts de sonda temporales fuera del repo versionado.

**Structure Decision**: no se crea código de producto; la evidencia vive en `specs/0002-aislamiento-credenciales/evidencia/` (redactada) y los comandos quedan en `quickstart`-estilo dentro de `tasks.md`.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| *(sin violaciones)* | — | — |
