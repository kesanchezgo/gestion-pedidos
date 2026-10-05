# Tasks: Aislamiento y credenciales con señuelo (0002)

**Input**: `spec.md` + `plan.md` + `research.md` (esta feature)

**Prerequisites**: spec aprobada por humano 2026-10-05

**Tests**: pruebas de control manuales con comandos reproducibles (FR-006). Solo señuelos, nunca claves reales.

## Phase 1: Setup

- [x] T001 Verificar `CLAUDE_CODE_SUBPROCESS_ENV_SCRUB=1` activo en la configuración efectiva (sin imprimir valores)
- [x] T002 Confirmar `.gitignore` bloquea `.env`, `.env.*` y `settings.local.json`; `git status` limpio

## Phase 2: US1 — Ocultamiento en subprocessos (P1)

- [x] T010 [US1] Ejecutar `echo $ANTHROPIC_AUTH_TOKEN` en Bash del agente y registrar salida vacía + ausencia del valor en logs
- [x] T011 [US1] Confirmar que la llamada API del proceso principal sigue autenticada (solo estado, sin valores)
- [x] T012 [US1] Guardar evidencia redactada en `specs/0002-aislamiento-credenciales/evidencia/US1-scrub.md`

## Phase 3: US2 — Señuelo bloqueado (P2)

- [x] T020 [US2] Crear señuelo ficticio único fuera del repo versionado
- [ ] T021 [US2] Intentar lectura por el agente y registrar denegación sin exponer el valor — NO PROBADO (sin ruta bloqueada configurada)
- [x] T022 [US2] Probar `gitleaks dir` sobre copia temporal y registrar detección; verificar repo limpio
- [x] T023 [US2] Guardar evidencia redactada en `specs/0002-aislamiento-credenciales/evidencia/US2-senuelo.md`

## Phase 4: US3 — Credencial limitada (P3, bloqueante documentado)

- [x] T030 [US3] Documentar que virtual keys requieren `DATABASE_URL` + BD (research R1); no forzar sin BD aprobada
- [ ] T031 [US3] Solo si hay BD: generar virtual key de inferencia y probar `/v1/messages` OK + `/key/generate` denegado; si no, dejar pendiente con criterio exacto

## Phase 5: Polish

- [x] T040 Actualizar checkboxes de este `tasks.md`, commit `test:`/`docs:` en rama `0002-aislamiento-credenciales`, sin push aún
- [ ] T041 Registrar en §11/§14 solo con evidencia obtenida (sin inflar estados)

## Dependencies

- T001-T002 bloquean US1/US2; US3 independiente pero bloqueada por requisito de BD
- US1 y US2 en paralelo posible; US3 solo si se aprueba BD
