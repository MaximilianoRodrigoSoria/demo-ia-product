# Feature: Endpoint GET /api/v1/products

## 1. Descripción General

Implementar un nuevo endpoint público (sin autenticación) `GET /api/v1/products` que retorna una colección de productos desde la tabla `product` (PostgreSQL). Si no existen registros, debe responder 404 con un error estándar. El objetivo es exponer el catálogo para su consumo por el usuario de la web PGO.

---

## 2. Alcance

### 2.1. Incluye

- ✅ Exposición del endpoint REST `GET /api/v1/products`
- ✅ Capa Controller, Service, Repository (Spring Data/JPA) y Mapper (DTO ↔ Entity)
- ✅ Serialización de la salida en JSON
- ✅ Respuestas estándar: 200, 404 (colección vacía), 500 (error interno)
- ✅ Uso de la tabla `product` existente y su trigger de actualización de `updated_at`

### 2.2. Excluye

- ❌ Filtros, paginación, ordenamiento, búsqueda por criterios
- ❌ Autenticación/autorización
- ❌ Cache, rate limiting, ETag, control de concurrencia vía headers
- ❌ Operaciones de escritura (POST/PUT/PATCH/DELETE)

---

## 3. Objetivo

Permitir que la web PGO liste los productos disponibles consultando el backend a través del endpoint `/api/v1/products`.

---

## 4. Actores Involucrados

| Actor | Descripción |
|-------|-------------|
| **Usuario web PGO** | Front-end de PGO que consume la API |
| **API de Productos** | Servicio expuesto por este requerimiento |
| **Base de Datos PostgreSQL** | Origen de datos de la tabla `product` |

---

## 5. Flujo Funcional

### 5.1. Flujo Principal

1. El Usuario web PGO invoca `GET /api/v1/products`
2. La API consulta todos los registros en `product`
3. **Si hay uno o más registros:** La API retorna 200 con la colección JSON
4. **Si no hay registros:** La API retorna 404 con error estándar

### 5.2. Flujos Alternos

**N/A** - No hay filtros ni parámetros

### 5.3. Flujo de Excepción

Si ocurre un fallo inesperado (por ejemplo, indisponibilidad de DB, excepción no controlada), la API responde 500 con el error estándar.

---

## 6. Datos y Contratos

### 6.1. Entradas

**Sin parámetros** - Endpoint de colección sin filtros

### 6.2. Salidas

#### 6.2.1. Respuesta 200 OK

```json
{
  "data": [
    {
      "id": 1,
      "sku": "SKU-001",
      "name": "Producto de ejemplo",
      "description": "Descripción inicial de producto",
      "price": 199.90,
      "currency": "ARS",
      "stock": 100,
      "active": true,
      "createdAt": "2025-01-01T12:00:00Z",
      "updatedAt": "2025-01-01T12:00:00Z",
      "version": 0
    }
  ],
  "count": 1
}
```

#### 6.2.2. Respuesta 404 Not Found (colección vacía)

```json
{
  "code": "APP-002",
  "message": "No se encontraron productos",
  "detail": "La consulta no devolvió resultados",
  "traceId": "f1a2b3c4d5"
}
```

#### 6.2.3. Respuesta 500 Internal Server Error

```json
{
  "code": "APP-005",
  "message": "Error interno",
  "detail": "Se produjo un error inesperado",
  "traceId": "e9f8a7b6c5"
}
```

### 6.3. Modelo de Datos

**Origen:** Tabla `product`

| Campo | Tipo | Constraints | Descripción |
|-------|------|-------------|-------------|
| `id` | BIGSERIAL | PK | Identificador único |
| `sku` | VARCHAR(50) | UNIQUE, NOT NULL | Código de producto |
| `name` | VARCHAR(255) | NOT NULL | Nombre del producto |
| `description` | TEXT | NULL | Descripción detallada |
| `price` | NUMERIC(14,2) | NOT NULL, CHECK >= 0 | Precio del producto |
| `currency` | CHAR(3) | NOT NULL, DEFAULT 'USD' | Moneda (ISO 4217) |
| `stock` | INTEGER | NOT NULL, DEFAULT 0, CHECK >= 0 | Stock disponible |
| `active` | BOOLEAN | NOT NULL, DEFAULT TRUE | Estado activo/inactivo |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Fecha de creación |
| `updated_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Fecha de actualización (trigger) |
| `version` | BIGINT | NOT NULL, DEFAULT 0 | Control optimista |

### 6.4. Catálogo de Errores

| Código | HTTP | Mensaje | Causa | Acción Recomendada | Retriable |
|--------|------|---------|-------|-------------------|-----------|
| `APP-002` | 404 | No se encontraron productos | Colección vacía | Verificar carga de datos | No |
| `APP-005` | 500 | Error interno | Excepción no controlada | Reintentar / contactar soporte | Sí |

> **Nota:** Se reserva `APP-001/003/004` para futuros casos (parámetros, rangos, rate limit).

### 6.5. Versionado API

- **Prefijo de versión en path:** `/api/v1/products`
- **Cambios incompatibles:** Crearán `/api/v2/...`

---

## 7. Validaciones y Reglas de Negocio

### 7.1. Reglas Principales

1. **Si la consulta devuelve cero registros:** Responder 404
2. **No hay validaciones de entrada:** No se reciben parámetros
3. **No aplicar reglas sobre `active`, `stock` o `currency`:** Se devuelven tal como estén en DB

---

## 8. Requisitos No Funcionales (NFR)

### 8.1. Rendimiento

- **p95 < 150 ms** (en red interna) con hasta 1000 registros
- **Tamaño de respuesta típico:** < 1 MB

### 8.2. Disponibilidad y Resiliencia

- **Objetivo:** 99.9%
- **Manejo de timeouts** a DB
- **Reintentos limitados** a nivel de pool si aplica

### 8.3. Seguridad

- **Autenticación:** Sin autenticación
- **Protocolo:** Solo HTTPS
- **PII:** Sin datos personales sensibles

### 8.4. Auditoría y Trazabilidad

- **Incluir `traceId`** en respuesta de error
- **Header `X-Trace-Id`** en todas las respuestas

### 8.5. Observabilidad

#### Logs
- **Formato:** JSON
- **Nivel INFO:** Para operaciones exitosas
- **Nivel ERROR:** Con stacktrace completo

#### Métricas
- `products.list.requests` - Contador de peticiones
- `products.list.errors` - Contador de errores
- Timer de latencia

#### Trazas Distribuidas
- **Framework:** OpenTelemetry
- **Span:** Para consulta a DB

### 8.6. Cumplimiento Normativo

**N/A**

### 8.7. Accesibilidad / i18n

- **Mensajes de error:** En español (estándar interno)

---

## 9. Integraciones

| Sistema | Propósito | Entorno |
|---------|-----------|---------|
| **PostgreSQL** | Base de datos principal | Producción |
| **H2** | Base de datos en memoria | Tests de integración |

**Framework sugerido:** Spring Boot + Spring Data JPA + MapStruct

---

## 10. Supuestos, Dependencias y Restricciones

### 10.1. Supuestos

- ✓ El cliente espera 404 cuando la colección está vacía
- ✓ Se devuelven todos los productos sin filtrar por `active`

### 10.2. Dependencias

- Conectividad a la base PostgreSQL
- Existencia de la tabla `product` y su trigger

### 10.3. Restricciones

- Sin controles de tasa
- **Recomendación:** Exponer con prudencia detrás de gateway si aplica

---

## 11. Experiencia de Usuario

### 11.1. UI Frontend

El frontend mostrará la grilla/listado con los campos principales:
- `sku`
- `name`
- `price`
- `currency`
- `stock`
- `active`

### 11.2. Mensajes de Error

En caso de 404, mostrar mensaje:
> "No hay productos disponibles"

---

## 12. Criterios de Aceptación

### Escenario 1 – Lista con Resultados

**Dado que** existen productos en la tabla `product`  
**Cuando** se invoca `GET /api/v1/products`  
**Entonces** la API responde 200 y el cuerpo contiene una lista con al menos un elemento y `count ≥ 1`

### Escenario 2 – Lista Vacía

**Dado que** no existen productos en la tabla `product`  
**Cuando** se invoca `GET /api/v1/products`  
**Entonces** la API responde 404 con `code=APP-002` y mensaje "No se encontraron productos"

### Escenario 3 – Error Interno

**Dado que** ocurre una excepción en la lectura de datos  
**Cuando** se invoca `GET /api/v1/products`  
**Entonces** la API responde 500 con `code=APP-005`

---

## 13. Plan de Pruebas

### 13.1. Tests Unitarios

#### Service
- ✓ Retorna lista cuando repositorio trae datos
- ✓ Lanza `NotFoundException` si lista vacía

#### Mapper
- ✓ Mapeo correcto Entity → DTO
- ✓ Validar campos numéricos y fechas

### 13.2. Tests de Integración (H2)

- ✓ 200 con al menos un producto (`SKU-001`)
- ✓ 404 con base vacía
- ✓ 500 simulando excepción (mock del repositorio)

### 13.3. Tests de Contratos

- ✓ Validar estructura JSON (`data[]`, `count`)

### 13.4. Tests de Performance

- ✓ Smoke test de latencia con 1000 filas

---

## 14. Plan de Despliegue y Activación

### 14.1. Despliegue

- Despliegue estándar del microservicio (pipeline CI/CD)
- **Sin migraciones** si la tabla ya existe
- **Si no existe:** Aplicar DDL provista

### 14.2. Monitoreo Post-Release

- Errores
- Latencia

### 14.3. Rollback

- Revertir despliegue a versión previa (stateless)

---

## 15. Métricas y KPIs

| Métrica | Descripción |
|---------|-------------|
| **Tasa de éxito** | Porcentaje de respuestas 2xx vs errores |
| **Latencia** | p50, p95, p99 |
| **Respuestas 404** | Porcentaje de colección vacía |
| **Throughput** | Requests por segundo (RPS) |

---

## 16. Riesgos y Mitigaciones

### R1: Respuesta 404 en Lista Vacía

**Riesgo:** La decisión de 404 en lista vacía puede sorprender a consumidores  
**Mitigación:** Documentación clara y contrato estable; evaluar 200 vacía en versión futura

### R2: Tamaños de Respuesta Grandes

**Riesgo:** Si la tabla crece, las respuestas pueden ser muy grandes  
**Mitigación:** Introducir paginación en una versión posterior

### R3: Dependencia de DB (SPOF)

**Riesgo:** Base de datos como punto único de falla  
**Mitigación:** Pooling, timeouts y alertas

---

## 17. Gobierno y Responsabilidades (RACI)

| Rol | Responsable |
|-----|-------------|
| **R** (Responsible) | Equipo Backend Productos |
| **A** (Accountable) | Líder Técnico / Arquitecto |
| **C** (Consulted) | Equipo Frontend PGO, QA |
| **I** (Informed) | Soporte/Operaciones |

---

## 18. Entregables

- ✅ Código fuente: Controller, Service, Repository, Mapper, DTOs
- ✅ Definición OpenAPI (YAML/JSON)
- ✅ Tests unitarios e integración (H2)
- ⬜ Pipeline CI/CD y dashboards básicos de observabilidad
- ✅ Script DDL (si fuera necesario aplicarlo)

---

## 19. Glosario

| Término | Definición |
|---------|-----------|
| **DTO** | Data Transfer Object |
| **Mapper** | Componente que transforma Entity ↔ DTO |
| **NFR** | Requisitos No Funcionales |
| **SPOF** | Single Point of Failure (Punto Único de Falla) |
| **p50/p95/p99** | Percentiles de latencia (50%, 95%, 99%) |
| **RPS** | Requests Per Second (Peticiones por segundo) |

---

## 20. Anexos

### 20.1. DDL Base

Tabla `product`, índices y trigger `updated_at` disponibles en:
- `src/main/resources/sql/product-api-db.sql`

### 20.2. Datos Semilla Sugeridos

Mínimo: `SKU-001` como producto de ejemplo

---

**Estado:** ✅ Implementado  
**Fecha de Implementación:** 2025-11-19  
**Versión:** 1.0  
**Responsable:** Equipo Backend Productos
