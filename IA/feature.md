## 1. Descripción general

Implementar un nuevo endpoint público (sin autenticación) **GET `/api/v1/products`** que retorna **una colección de productos** desde la tabla `product` (PostgreSQL). Si no existen registros, **debe responder 404** con un error estándar. El objetivo es exponer el catálogo para su consumo por el **usuario de la web PGO**.

## 2. Alcance

* **Incluye:**

    * Exposición del endpoint REST GET `/api/v1/products`.
    * Capa **Controller**, **Service**, **Repository** (Spring Data/JPA) y **Mapper** (DTO ↔ Entity).
    * Serialización de la salida en JSON.
    * Respuestas estándar: **200**, **404** (colección vacía), **500** (error interno).
    * Uso de la tabla `product` existente y su trigger de actualización de `updated_at`.
* **Excluye:**

    * Filtros, paginación, ordenamiento, búsqueda por criterios.
    * Autenticación/autorización.
    * Cache, rate limiting, ETag, control de concurrencia vía headers.
    * Operaciones de escritura (POST/PUT/PATCH/DELETE).

## 3. Objetivo

Permitir que la web PGO **liste los productos** disponibles consultando el backend a través del endpoint `/api/v1/products`.

## 4. Actores involucrados

* **Usuario web PGO** (front-end de PGO).
* **API de Productos** (servicio expuesto por este requerimiento).
* **Base de Datos PostgreSQL** (origen de datos de la tabla `product`).

## 5. Flujo funcional

* **Principal**

    1. El Usuario web PGO invoca `GET /api/v1/products`.
    2. La API consulta todos los registros en `product`.
    3. Si hay uno o más registros, la API retorna **200** con la colección JSON.
    4. Si no hay registros, la API retorna **404** con error estándar.

* **Alternos**

    * N/A (no hay filtros ni parámetros).

* **Excepción**

    * Si ocurre un fallo inesperado (por ejemplo, indisponibilidad de DB, excepción no controlada), la API responde **500** con el error estándar.

## 6. Datos y contratos

* **Entradas (campos, tipos, obligatoriedad, ejemplos)**

    * **Sin parámetros** (endpoint de colección sin filtros).

* **Salidas (estructura, estados, ejemplos)**

    * **200 OK**

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
    * **404 Not Found** (colección vacía)

      ```json
      {
        "code": "APP-002",
        "message": "No se encontraron productos",
        "detail": "La consulta no devolvió resultados",
        "traceId": "f1a2b3c4d5"
      }
      ```
    * **500 Internal Server Error**

      ```json
      {
        "code": "APP-005",
        "message": "Error interno",
        "detail": "Se produjo un error inesperado",
        "traceId": "e9f8a7b6c5"
      }
      ```

* **Modelo de datos** (origen: tabla `product`)

    * `id BIGSERIAL PK`
    * `sku VARCHAR(50) UNIQUE NOT NULL`
    * `name VARCHAR(255) NOT NULL`
    * `description TEXT`
    * `price NUMERIC(14,2) NOT NULL CHECK (price >= 0)`
    * `currency CHAR(3) NOT NULL DEFAULT 'USD'`
    * `stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0)`
    * `active BOOLEAN NOT NULL DEFAULT TRUE`
    * `created_at TIMESTAMPTZ NOT NULL DEFAULT now()`
    * `updated_at TIMESTAMPTZ NOT NULL DEFAULT now()` (actualizado por trigger)
    * `version BIGINT NOT NULL DEFAULT 0`

* **Catálogo de errores**
  | Código | HTTP | Mensaje                         | Causa                         | Acción recomendada          | Retriable |
  |---|---:|---|---|---|:--:|
  | APP-002 | 404 | No se encontraron productos     | Colección vacía               | Verificar carga de datos    | No |
  | APP-005 | 500 | Error interno                   | Excepción no controlada       | Reintentar / contactar soporte | Sí |

> Nota: Se reserva **APP-001/003/004** para futuros casos (parámetros, rangos, rate limit).

* **Versionado API**

    * Prefijo de versión en path: `/api/v1/products`. Cambios incompatibles crearán `/api/v2/...`.

## 7. Validaciones y reglas de negocio

* **Regla principal:** Si la consulta devuelve **cero registros**, responder **404**.
* No hay validaciones de entrada (no se reciben parámetros).
* No aplicar reglas sobre `active`, `stock` o `currency` más allá de la persistencia; se devuelven tal como estén en DB.

## 8. Requisitos no funcionales (NFR)

* **Rendimiento**: p95 < 150 ms (en red interna) con hasta 1000 registros; tamaño de respuesta típico < 1 MB.
* **Disponibilidad y resiliencia**: Objetivo 99.9%. Manejo de timeouts a DB, reintentos limitados a nivel de pool si aplica.
* **Seguridad (authz/authn, cifrado, PII)**: Sin autenticación; **solo HTTPS**; sin PII sensible.
* **Auditoría y trazabilidad**: Incluir `traceId` en respuesta de error y header `X-Trace-Id` en todas las respuestas.
* **Observabilidad (logs, métricas, alertas)**:

    * Logs JSON con nivel INFO para éxito y ERROR con stacktrace.
    * Métrica de contador `products.list.requests`, `products.list.errors`, timer de latencia.
    * Trazas distribuidas (OpenTelemetry) con span para consulta a DB.
* **Cumplimiento normativo**: N/A.
* **Accesibilidad / i18n**: Mensajes de error en español (estándar interno).

## 9. Integraciones

* **Base de datos**: PostgreSQL (producción). H2 en tests de integración.
* **Framework sugerido**: Spring Boot + Spring Data JPA + MapStruct.

## 10. Supuestos, dependencias y restricciones

* **Supuestos**

    * El cliente espera **404** cuando la colección está vacía.
    * Se devuelven **todos** los productos sin filtrar por `active`.
* **Dependencias**

    * Conectividad a la base PostgreSQL y existencia de la tabla `product` y su trigger.
* **Restricciones**

    * Sin controles de tasa; exponer con prudencia detrás de gateway si aplica.

## 11. Experiencia de usuario (si aplica)

* El frontend mostrará la grilla/listado con los campos principales (`sku`, `name`, `price`, `currency`, `stock`, `active`).
* En caso de 404, mostrar mensaje “No hay productos disponibles”.

## 12. Criterios de aceptación

* **Escenario 1 – Lista con resultados**

    * **Dado** que existen productos en la tabla `product`
    * **Cuando** se invoca `GET /api/v1/products`
    * **Entonces** la API responde **200** y el cuerpo contiene una lista con al menos un elemento y `count` ≥ 1
* **Escenario 2 – Lista vacía**

    * **Dado** que no existen productos en la tabla `product`
    * **Cuando** se invoca `GET /api/v1/products`
    * **Entonces** la API responde **404** con `code=APP-002` y mensaje “No se encontraron productos”
* **Escenario 3 – Error interno**

    * **Dado** que ocurre una excepción en la lectura de datos
    * **Cuando** se invoca `GET /api/v1/products`
    * **Entonces** la API responde **500** con `code=APP-005`

## 13. Plan de pruebas

* **Unitarias**

    * Service: retorna lista cuando repo trae datos; lanza `NotFoundException` si lista vacía.
    * Mapper: mapeo Entity→DTO (campos numéricos y fechas).
* **Integración** (H2, datos semilla)

    * 200 con al menos un producto (`SKU-001`).
    * 404 con base vacía.
    * 500 simulando excepción (mock del repositorio).
* **Contratos**

    * Validar estructura JSON (data[], count).
* **Performance**

    * Smoke test de latencia con 1000 filas.

## 14. Plan de despliegue y activación

* Despliegue estándar del microservicio (pipeline CI/CD).
* Sin migraciones si la tabla ya existe; si no, aplicar DDL provista.
* Monitoreo post-release (errores, latencia).
* **Rollback**: revertir despliegue a versión previa (stateless).

## 15. Métricas y KPIs

* Tasa de éxito (2xx) vs errores.
* Latencia p50/p95/p99.
* Porcentaje de respuestas **404** (colección vacía).
* Throughput (RPS).

## 16. Riesgos y mitigaciones

* **R1**: La decisión de 404 en lista vacía puede sorprender a consumidores.

    * *Mitigación*: Documentación clara y contrato estable; evaluar 200 vacía en versión futura.
* **R2**: Tamaños de respuesta grandes si la tabla crece.

    * *Mitigación*: Introducir paginación en una versión posterior.
* **R3**: Dependencia de DB (SPOF).

    * *Mitigación*: Pooling, timeouts y alertas.

## 17. Gobierno y responsabilidades (RACI)

* **R** (Responsible): Equipo Backend Productos.
* **A** (Accountable): Líder Técnico / Arquitecto.
* **C** (Consulted): Equipo Frontend PGO, QA.
* **I** (Informed): Soporte/Operaciones.

## 18. Entregables

* Código fuente: Controller, Service, Repository, Mapper, DTOs.
* Definición OpenAPI (YAML/JSON).
* Tests unitarios e integración (H2).
* Pipeline CI/CD y dashboards básicos de observabilidad.
* Script DDL (si fuera necesario aplicarlo).

## 19. Glosario

* **DTO**: Data Transfer Object.
* **Mapper**: Componente que transforma Entity↔DTO.
* **NFR**: Requisitos no funcionales.

## 20. Anexos

* **DDL** base (tabla `product`, índices y trigger `updated_at`).
* Datos semilla sugeridos: `SKU-001` como mínimo.

