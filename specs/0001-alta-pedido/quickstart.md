# Quickstart — feature 0001-alta-pedido

Cómo ejecutar y demostrar esta feature en local (mismos gates que CI).

## Requisitos

- Docker Desktop encendido (`D:\Apps\Docker`) — los ITs levantan Postgres con Testcontainers.
- `mise` en PATH (pone Java 25 / Node 24 / Maven 3.10).

## Backend

```bash
cd backend
mise exec -- ./mvnw verify        # unit (*Test) + integración (*IT, Postgres real)
mise exec -- ./mvnw spring-boot:run   # API en :8080 (o con compose: docker compose up -d postgres)
```

## Frontend

```bash
cd frontend
pnpm install
pnpm dev                           # http://localhost:5173 (proxy /api → :8080)
```

## Demo / comprobación manual (SC-001)

```bash
curl -s -X POST http://localhost:8080/api/pedidos -H "Content-Type: application/json" \
  -d '{"clienteNombre":"Ana Pérez","clienteEmail":"ana@ejemplo.com","lineas":[
        {"descripcion":"Café en grano","cantidad":2,"precioUnitario":9.90},
        {"descripcion":"Taza","cantidad":1,"precioUnitario":14.50}]}'
# esperado: 201, total = 2*9.90 + 14.50 = 34.30, estado ABIERTO

curl -s "http://localhost:8080/api/pedidos?page=0&size=10"
curl -s http://localhost:8080/api/pedidos/999   # esperado: 404 problem+json
```

En el navegador: `http://localhost:5173` → alta con validación por campo → el pedido aparece en el listado con su total.

## Batería de gates (antes de cualquier PR)

```bash
cd gestion-pedidos
mise exec -- ./mvnw -f backend/pom.xml verify     # o desde backend/: mise exec -- ./mvnw verify
(cd frontend && pnpm lint && pnpm typecheck && pnpm test && pnpm build)
gitleaks dir . --redact --no-banner
```

**Recordatorio (SC-002)**: en el log de `verify` debe verse la ejecución de los `*IT` (Failsafe) — no basta con que salga verde.

## Escenarios de aceptación de la spec → cómo verificarlos

| Spec | Verificación |
|---|---|
| US1 / FR-001..003 | POST con 2 líneas → 201 y total 34.30; sin líneas → 400; cantidad 0 → 400 con `errors.lineas[0].cantidad` |
| US2 / FR-005 | GET /{id} 200/404; GET lista ordenado desc |
| US3 / FR-006 | Formulario con email inválido → error en campo y no se envía |
| SC-004 | Romper `calcularTotal()` en el servidor → falla `PedidosApiIT` (y unitario) |
