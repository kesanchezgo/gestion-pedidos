# Research — 0002-aislamiento-credenciales (Fase 0)

## R1 — Virtual keys de LiteLLM requieren BD

- **Fuente:** `https://docs.litellm.ai/docs/proxy/virtual_keys` (consultada 2026-10-05).
- **Requisito:** `DATABASE_URL=postgresql://...` + `master_key` (`sk-...`) en `general_settings` o `LITELLM_MASTER_KEY`; sin BD no hay `/key/generate` persistente.
- **Estado actual:** `litellm-config.yaml` no tiene `database_url` ni `general_settings`; el gateway arranca solo con `LITELLM_MASTER_KEY` desde `HKCU\Environment` (ver `start-litellm.ps1`).
- **Decisión:** US3 queda como **bloqueante documentado** salvo que se apruebe añadir Postgres + `DATABASE_URL` y reiniciar el gateway. En este PR se cierra US1+US2; US3 se deja en `tasks.md` como pendiente con criterio exacto.

## R2 — SCRUB solo afecta subprocessos

- `CLAUDE_CODE_SUBPROCESS_ENV_SCRUB=1` limpia credenciales en Bash/hooks/MCP stdio, conserva auth en el proceso principal.
- **Prueba válida:** `echo $ANTHROPIC_AUTH_TOKEN` en Bash del agente debe salir vacío; la llamada API principal debe seguir autenticada.
- **Regla:** nunca imprimir valores; solo salidas vacías/denegadas y nombres de variable.

## R3 — Señuelos sin claves reales

- Valores ficticios únicos, fuera del repo o en ruta bloqueada, nunca commiteados.
- Capacidad del scanner se prueba con `gitleaks dir` sobre copia temporal, no con push.
- `.gitignore` del piloto ya cubre `.env`, `.env.*` (menos `.env.example`) y `settings.local.json`.

## Riesgos

| Riesgo | Mitigación |
|---|---|
| Exponer master key en logs/traces | Solo nombres de variable; grep de verificación busca valores señuelo, no reales |
| Virtual keys sin BD | No forzar; documentar bloqueante y probar alcance solo si hay BD |
| Señuelo commiteado por error | Crear fuera del repo; `git status` limpio antes de commit |
