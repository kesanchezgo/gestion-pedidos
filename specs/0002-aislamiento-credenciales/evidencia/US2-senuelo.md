# Evidencia US2 — Señuelo bloqueado/detectado (T020/T022)

Fecha: 2026-10-05. Rama: `0002-aislamiento-credenciales`.

## T020 — Señuelo ficticio fuera del repo

- Ruta temporal (no versionada, eliminada tras la prueba): `/tmp/decoy-test/decoy.txt`
- Contenido ficticio único con formato tipo api_key (valor eliminado de la evidencia para no dejar firmas detectables; nunca una clave real).
- Verificación post-prueba: directorio temporal eliminado (`rm -rf`).

## T022 — Capacidad del scanner + repo limpio

- `gitleaks dir /tmp/decoy-test` → `leaks found: 1` (el scanner SÍ detecta el señuelo).
- `gitleaks dir .` en el repo → `no leaks found` (~717 KB escaneados).
- `git status` muestra solo `?? specs/0002-aislamiento-credenciales/` (docs y sonda, sin valores).

## T021 — Lectura bloqueada por el agente: NO probado

- No existe una ruta bloqueada por configuración en este piloto, así que no se ejecutó un intento de lectura denegada.
- Lo que sí consta: al agente nunca se le entregó ningún valor real; solo nombres de variable y señuelos ficticios.
- Pendiente honesto: configurar denegación de lectura (settings/permisos) y repetir con señuelo.
