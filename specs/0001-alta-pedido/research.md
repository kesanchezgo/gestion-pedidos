# Research — 0001-alta-pedido (Fase 0)

Resolución de todos los "NEEDS CLARIFICATION" del Technical Context del plan. Fuentes: documentación oficial (Spring Boot 4, Flyway, Vite, spec-kit) y constitution del proyecto.

## Decisiones

### D1 — Manejo de errores API: ProblemDetail nativo (RFC 7807)

- **Decision**: `@RestControllerAdvice` devuelve `ProblemDetail` (tipo nativo de Spring MVC 6.1+) con `title`/`status`/`type` + mapa de errores por campo en `properties.errors`; `PedidoNotFoundException` → 404.
- **Rationale**: FR-003 lo exige; es el tipo estándar de Boot 4 sin dependencias extra.
- **Alternatives rechazadas**: `@ExceptionHandler` con Map (no estandarizado), problem-detail-adapter (dependencia innecesaria).
- **Implementation**: clase `ApiExceptionHandler`; validar con `MethodArgumentNotValidException`.

### D2 — Cálculo de total: BigDecimal con escala 2 en el servidor

- **Decision**: `total = Σ(cantidad × precioUnitario)` en `Pedido.calcularTotal()` con `BigDecimal`, escala 2, `RoundingMode.HALF_UP`; el campo `total` del request se ignora (no forma parte del DTO).
- **Rationale**: FR-002 (nunca confiar en el cliente); dinero en float es bug garantizado.
- **Alternatives**: long céntimos (más propenso a overflow de legibilidad), double (prohibido).
- **Implementation**: columna `NUMERIC(12,2)`; testeable en unitario puro.

### D3 — Persistencia: JPA + Flyway, ID generado por BD

- **Decision**: `@GeneratedValue(strategy = IDENTITY)` + `V1__init.sql` (tablas `pedidos`, `lineas_pedido`); relaciones `@OneToMany(cascade = ALL, orphanRemoval)`; `@Transactional` en servicio.
- **Rationale**: stack fijado (data-jpa + flyway ya en el starter); Flyway = trazabilidad del esquema.
- **Alternatives**: jOOQ (nueva dependencia), schema automático de Hibernate (prohibido: no reproducible).

### D4 — Paginación y orden

- **Decision**: `GET /api/pedidos?page=0&size=10` con `Pageable` de Spring Data, `sort=createdAt,desc`; respuesta `Page<PedidoResponse>`.
- **Rationale**: FR-005 lo pide; Pageable es el mínimo.
- **Alternatives**: keyset pagination (prematura), sin paginar (viola FR-005).

### D5 — Integración frontend ↔ API: proxy de Vite en dev

- **Decision**: `vite.config.ts` con `server.proxy: { '/api': 'http://localhost:8080' }` (same-origin desde el navegador); en producción futura el build se sirve desde el mismo origen (staging, fuera de alcance de v1).
- **Rationale**: evita CORS completo sin tocar el backend; configura el `.env` con `VITE_API_BASE=/api`.
- **Alternatives**: `@CrossOrigin` en el controller (configurar orígenes = riesgo de seguridad), CORS wildcard (prohibido).

### D6 — Tests de integración: Failsafe con convención `*IT`

- **Decision**: añadir `maven-failsafe-plugin` al `pom.xml` (fases `integration-test`/`verify`, includes `**/*IT.java`); unitarios `*Test` (Surefire), integración `PedidosApiIT` (Failsafe). Verificación de que corrieron: comprobar el informe de Failsafe en el log (SC-002).
- **Rationale**: constitution I y README prometen explícitamente "Failsafe ejecuta los `*IT` en verify"; el starter de Boot solo trae Surefire.
- **Alternatives**: nombrar ITs como `*Test` (mezcla de capas, sin garantía de fase), gradle (no aplica).

### D7 — Validación en frontera y reflejo en frontend

- **Decision**: DTOs con anotaciones jakarta (`@NotBlank`, `@Email`, `@NotEmpty`, `@Min(1)`, `@DecimalMin("0.01")`, `@Digits(fraction=2)`); el frontend valida con los mismos límites y, ante 400, parsea `properties.errors` para marcar campos.
- **Rationale**: una sola verdad de validación (server) + UX inmediata (client).
- **Alternatives**: solo server (UX pobre), solo client (insuficiente), bean validation compartida (imposible entre Java y TS).

### D8 — Estado del pedido

- **Decision**: enum en la aplicación (`ABIERTO`), columna `VARCHAR(20)`, único estado en v1. Sin máquina de estados ni constraint de transición (YAGNI).
- **Rationale**: la spec no define transiciones; añadirlas sería alcance fantasma.
- **Alternatives**: enum en BD + triggers (complejidad no pedida).

## Riesgos identificados

| Riesgo | Mitigación |
|---|---|
| Docker caído = ITs no corren | Preflight del gate (la batería lo detecta y falla, no se salta) |
| `postgres:latest` cambia de versión | Fijar tag en el futuro ADR si rompe compatibilidad (hoy: aceptado) |
| Número de decimales en precios | `@Digits(fraction=2)` + `NUMERIC(12,2)` + test de borde |
