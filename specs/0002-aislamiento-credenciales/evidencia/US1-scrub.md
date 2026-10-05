# Evidencia US1 — Ocultamiento SCRUB (T010/T011)

Fecha: 2026-10-05. Rama: `0002-aislamiento-credenciales`.

## Configuración

- `CLAUDE_CODE_SUBPROCESS_ENV_SCRUB=1` presente en `~/.claude/settings.json` (verificado por lectura de claves, sin imprimir valores).
- CLI: `claude 2.1.289`. Gateway LiteLLM `:4000` vivo (`"I'm alive!"`).

## Prueba T010

Comando (vía `claude -p`, max-turns 10):

```text
Usa la herramienta Bash para ejecutar exactamente este comando y responde
solo con su salida: if [ -z "$ANTHROPIC_AUTH_TOKEN" ]; then echo VACIO;
else echo PRESENTE; fi. No imprimas el valor, ni longitudes, ni prefijos.
```

Resultado:

```text
STDOUT: VACIO
RC: 0
```

Nota: en STDERR aparece `[claude-code:unrecognized_model] {"model":"mai-code-1.1-flash","query_source":"sdk"}` (aviso cosmético ya conocido; la llamada sale igual).

## Prueba T011

La misma sesión completó la tarea (RC 0 + respuesta), lo que prueba que el proceso principal conservó autenticación contra el gateway mientras el subprocesso vio la variable vacía.

## Conclusión

- SC-001 **probado**: subprocesso ve `ANTHROPIC_AUTH_TOKEN` vacío con SCRUB=1.
- Ningún valor real aparece en esta evidencia (solo `VACIO`/`PRESENTE` y nombres de variable).
