# Evidencia US3 — Credencial limitada (bloqueante documentado)

Fecha: 2026-10-05. Rama: `0002-aislamiento-credenciales`.

## BD lista (dentro del piloto)

- Postgres del compose del piloto (`gestion-pedidos-postgres-1`) sano en `:5432`.
- Base `litellm` creada en ese Postgres para aislar las virtual keys de `gestionpedidos`.
- Verificación: `psql -U gestion -d litellm -c "select 1;"` → `1`.

## Bloqueante (fuera del piloto, NO aplicado)

Virtual keys requieren en el gateway:

1. `general_settings` con `master_key: os.environ/LITELLM_MASTER_KEY` y `database_url: os.environ/DATABASE_URL` en la config de LiteLLM.
2. `DATABASE_URL` en el entorno del proceso LiteLLM apuntando a `postgresql://gestion:gestion-dev@localhost:5432/litellm` (credenciales de dev del compose; no se commitean).
3. Reinicio del gateway en `:4000` y prueba:
   - `POST /key/generate` con master key → crea `sk-...` limitada a inferencia.
   - `/v1/messages` con virtual key → 200.
   - `/key/generate` con virtual key → denegado.

No se aplicó porque esos archivos/procesos viven fuera de `gestion-pedidos` (`fullstack-pro-toolkit/litellm-config.yaml`, `start-litellm.ps1`, proceso en `:4000`) y la regla del piloto es no tocar nada fuera de su carpeta. Se deja como propuesta exacta para aplicar con aprobación separada.

## Estado

- T030 **hecho** (requisito documentado con fuente oficial).
- T031 **pendiente** hasta aprobar el cambio de gateway fuera del piloto.
- Sin valores reales en esta evidencia.
