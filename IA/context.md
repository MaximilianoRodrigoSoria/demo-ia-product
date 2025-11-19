# Contexto del Proyecto

## 1. Resumen

**Nombre del repositorio:** `demo-ia-product`  
**Descripción general:** API REST para la gestión de productos con soporte para inteligencia artificial.  
**Versión actual:** `0.0.1-SNAPSHOT`  
**Grupo:** `com.ar.laboratory`  
**Artifact ID:** `demo-ia-product`  
**Estado:** Proyecto en desarrollo (phase MVP - demostración de tecnologías y patrones base).

El proyecto demuestra prácticas de arquitectura limpia, inyección de dependencias por constructor y documentación automática con OpenAPI/Swagger. Proporciona una base sólida para desarrollos asistidos por IA respetando estándares de codificación, testing y observabilidad.

---

## 2. Arquitectura y Módulos

### 2.1 Arquitectura General

- **Estilo:** REST/HTTP  
- **Capas:**
  - **Controller** (`com.ar.laboratory.demoiaproduct.controller`): Entrada HTTP, validación y mapeo de requests/responses.
  - **Mapper** (`com.ar.laboratory.demoiaproduct.mapper`): Transformación de DTOs ↔ Entidades (MapStruct).
  - **DTO** (`com.ar.laboratory.demoiaproduct.dto`): Contratos de request/response.
  - **Entity** (`com.ar.laboratory.demoiaproduct.entity`): Modelos de dominio (pendiente).
  - **Config** (`com.ar.laboratory.demoiaproduct.config`): Configuración de beans (OpenAPI, etc.).

### 2.2 Módulos Actuales

| Módulo | Ubicación | Responsabilidad | Estado |
|--------|-----------|-----------------|--------|
| **Health Check** | `controller/HealthController.java` | Verificar estado de la aplicación | ✅ Implementado |
| **Mapper Health** | `mapper/HealthMapper.java` | Mapeo HealthInfo ↔ HealthResponse | ✅ Implementado |
| **OpenAPI Config** | `config/OpenApiConfig.java` | Documentación Swagger/OpenAPI | ✅ Implementado |
| **Product** | Pendiente | CRUD de productos | 🔲 Pendiente |
| **Service Layer** | Pendiente | Lógica de negocio | 🔲 Pendiente |
| **Repository Layer** | Pendiente | Acceso a datos (JPA/Hibernate) | 🔲 Pendiente |
| **Exception Handling** | Pendiente | Manejo centralizado de errores | 🔲 Pendiente |

---

## 3. Tecnologías y Versiones

### 3.1 Stack Tecnológico

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Java** | `17` | Lenguaje base con características modernas (records, sealed classes, pattern matching). |
| **Spring Boot** | `3.5.7` | Framework para aplicaciones web/REST. |
| **Spring Web** | `3.5.7` | Soporte para controladores REST y MVC. |
| **Lombok** | Latest (via parent) | Reducción de boilerplate (getters, setters, logging). |
| **MapStruct** | Pendiente (añadir) | Mapeo de objetos en tiempo de compilación. |
| **PostgreSQL** | `15.2` | Base de datos relacional principal. |
| **Spring Data JPA** | Pendiente (añadir) | ORM con Hibernate. |
| **Jakarta Bean Validation** | `3.x` (via Spring Boot) | Validación de inputs. |
| **OpenAPI/Swagger** | `2.x` (springdoc-openapi) | Documentación automática de APIs. |
| **Maven** | `3.x` | Gestor de dependencias y compilación. |

### 3.2 Dependencias Actuales (pom.xml)

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 3.3 Dependencias Pendientes (Recomendadas)

- `spring-boot-starter-data-jpa` (acceso a datos)
- `spring-boot-starter-validation` (Jakarta Bean Validation)
- `org.mapstruct:mapstruct` y `org.mapstruct:mapstruct-processor` (mapeo de DTOs)
- `springdoc-openapi-starter-webmvc-ui` (OpenAPI/Swagger UI)
- `spring-boot-starter-actuator` (health endpoints y métricas)
- `postgresql` (driver JDBC)
- `junit-jupiter` (JUnit 5 - ya incluido en spring-boot-starter-test)
- `mockito` (mocking en tests)
- `testcontainers` (integración con BD en tests)
- `org.slf4j:slf4j-api` (ya incluido)

---

## 4. Endpoints y Contratos Existentes

### 4.1 Health Check Endpoints

#### GET `/api/health`
**Descripción:** Verificar estado completo de la aplicación con información detallada.

**Request:** Sin parámetros  
**Response:**
```json
{
  "status": "UP",
  "message": "Aplicación funcionando correctamente",
  "version": "1.0.0",
  "timestamp": "2025-11-11T14:30:45.123456"
}
```

**Códigos HTTP:**
- `200 OK`: Aplicación funcionando correctamente.
- `500 Internal Server Error`: (potencial) Error interno.

---

#### GET `/api/health/status`
**Descripción:** Estado simplificado (solo estado).

**Request:** Sin parámetros  
**Response:**
```plain
UP
```

**Códigos HTTP:**
- `200 OK`: Aplicación disponible.

---

### 4.2 Endpoints Pendientes

| Método | Ruta | Descripción | Estado |
|--------|------|-------------|--------|
| `POST` | `/api/v1/products` | Crear producto | 🔲 Pendiente |
| `GET` | `/api/v1/products` | Listar productos (con filtros) | 🔲 Pendiente |
| `GET` | `/api/v1/products/{id}` | Obtener producto por ID | 🔲 Pendiente |
| `PUT` | `/api/v1/products/{id}` | Actualizar producto | 🔲 Pendiente |
| `DELETE` | `/api/v1/products/{id}` | Eliminar producto | 🔲 Pendiente |

---

### 4.3 Documentación Automática

- **Swagger UI:** `http://localhost:8080/demo-ia-product/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/demo-ia-product/api-docs`
- **Herramienta:** springdoc-openapi (requiere añadir dependencia)

---

## 5. Integraciones (APIs, Colas, BDs)

### 5.1 Base de Datos

**Motor:** PostgreSQL 15.2  
**Nombre BD:** `productbd`  
**Usuario:** `user`  
**Contraseña:** `password`  
**Host:** `localhost:5433` (docker-compose)  
**Inicialización:** Scripts automáticos (`product-api-db.sql`, `product-data.sql`)

**Tabla actual:**
- `product`: Tabla con campos SKU, nombre, descripción, precio, currency, stock, timestamps y control de versión.

---

### 5.2 APIs Externas

| Nombre | Propósito | Status | Detalles |
|--------|-----------|--------|---------|
| Pendiente | Pendiente | 🔲 No aplicable | Especificar cuando se agreguen integraciones. |

---

### 5.3 Colas de Mensajes

| Sistema | Propósito | Status | Detalles |
|---------|-----------|--------|---------|
| Pendiente | Pendiente | 🔲 No aplicable | Evaluar si se requiere async processing. |

---

## 6. Modelo de Datos (Alto Nivel)

### 6.1 Entidades

#### **Product**
```
id (BIGSERIAL, PK)
├─ sku (VARCHAR 50, UNIQUE, NOT NULL)
├─ name (VARCHAR 255, NOT NULL)
├─ description (TEXT, nullable)
├─ price (NUMERIC 14,2, NOT NULL, CHECK >= 0)
├─ currency (CHAR 3, DEFAULT 'USD')
├─ stock (INTEGER, DEFAULT 0, CHECK >= 0)
├─ active (BOOLEAN, DEFAULT true)
├─ created_at (TIMESTAMP WITH TIME ZONE, DEFAULT now())
├─ updated_at (TIMESTAMP WITH TIME ZONE, DEFAULT now(), actualizada por trigger)
└─ version (BIGINT, DEFAULT 0, para control de concurrencia optimista)
```

**Índices:**
- `idx_product_name` (name)
- `idx_product_created_at` (created_at)

**Trigger:** `trg_product_updated_at` (PostgreSQL) - Actualiza `updated_at` automáticamente en UPDATE.

---

### 6.2 DTOs

#### **HealthInfo** (interno)
```
├─ applicationStatus (String)
├─ statusMessage (String)
├─ applicationVersion (String)
└─ checkTime (LocalDateTime)
```

#### **HealthResponse** (respuesta HTTP)
```
├─ status (String)
├─ message (String)
├─ version (String)
└─ timestamp (LocalDateTime)
```

---

### 6.3 Diagramas de Relaciones

**Estado actual:** Solo 1 tabla (`product`). Sin relaciones externas.  
**Próximas mejoras:** Agregar entidades de negocio (órdenes, categorías, inventario, etc.) según requisitos.

---

## 7. Estándares y Convenciones de Código

### 7.1 Convenciones Generales

| Aspecto | Convención | Ubicación/Referencia |
|--------|-----------|------|
| **Idioma de código** | Inglés (clases, métodos, variables). | Toda la base de código. |
| **Idioma de comentarios** | Español (JavaDoc, comentarios en línea). | Según contexto del equipo. |
| **Inyección de dependencias** | Constructor con `@RequiredArgsConstructor` (Lombok). | Controllers, Services, Config. |
| **Anotaciones HTTP** | Verbos correctos (`@GetMapping`, `@PostMapping`, etc.). | Controllers. |
| **Responses** | `ResponseEntity<T>` para flexibilidad. | Controllers. |
| **DTOs** | Separadas de entidades; mapeo con MapStruct. | `dto/` |
| **Excepciones** | Excepciones de negocio que extienden `RuntimeException`. | `exception/` (pendiente crear) |
| **Logging** | SLF4J con `@Slf4j` de Lombok. | Services, Controllers. |

### 7.2 Estructura de Paquetes

```
com.ar.laboratory.demoiaproduct/
├─ DemoIaProductApplication.java
├─ config/              # Configuración de beans
│  └─ OpenApiConfig.java
├─ controller/          # REST Controllers (thin)
│  └─ HealthController.java
├─ service/             # Lógica de negocio (pendiente)
│  └─ ProductService.java (pendiente)
├─ repository/          # Acceso a datos (pendiente)
│  └─ ProductRepository.java (pendiente)
├─ entity/              # Modelos JPA/Dominio (pendiente)
│  └─ Product.java (pendiente)
├─ dto/                 # Contratos de request/response
│  ├─ HealthInfo.java
│  ├─ HealthResponse.java
│  └─ (pendiente: ProductRequest, ProductResponse)
├─ mapper/              # Mapeo de objetos
│  ├─ HealthMapper.java
│  └─ (pendiente: ProductMapper.java)
├─ exception/           # Excepciones de negocio (pendiente)
│  ├─ ResourceNotFoundException.java
│  └─ BusinessRuleViolationException.java
├─ util/                # Utilidades y helpers (pendiente)
│  └─ (pendiente: Constants, DateUtils, etc.)
└─ handler/             # Manejo centralizado de errores (pendiente)
   └─ GlobalExceptionHandler.java
```

### 7.3 Convenciones de Nombres

| Elemento | Patrón | Ejemplo |
|----------|--------|---------|
| **Clases** | `CapitalCase` (PascalCase) | `HealthController`, `ProductService` |
| **Métodos** | `camelCase` | `checkHealth()`, `createProduct()` |
| **Variables** | `camelCase` | `applicationName`, `healthInfo` |
| **Constantes** | `UPPER_SNAKE_CASE` | `API_VERSION`, `DEFAULT_PAGE_SIZE` |
| **DTOs de request** | `{Entidad}Request` | `CreateProductRequest`, `UpdateProductRequest` |
| **DTOs de response** | `{Entidad}Response` | `ProductResponse`, `HealthResponse` |
| **Mappers** | `{Entidad}Mapper` | `ProductMapper`, `HealthMapper` |
| **Services** | `{Dominio}Service` | `ProductService`, `OrderService` |
| **Repositories** | `{Entidad}Repository` | `ProductRepository` |
| **Excepciones** | `{Razon}Exception` | `ResourceNotFoundException`, `BusinessRuleViolationException` |
| **Utilidades** | `{Concepto}Utils` o `{Concepto}Helper` | `DateUtils`, `ValidationHelper` |

### 7.4 JavaDoc y Documentación

**Obligatorio para:**
- Toda clase pública
- Todos los métodos públicos
- Parámetros (`@param`)
- Retornos (`@return`)
- Excepciones lanzadas (`@throws`)

**Nivel de detalle:** Profesional, técnico, conciso. Incluir propósito de negocio cuando aplique.

**Ejemplo:**
```java
/**
 * Procesa el pago de una orden garantizando idempotencia y consistencia transaccional.
 *
 * <p>Reglas de negocio:
 * <ul>
 *   <li>Verifica elegibilidad del cliente y estado de la orden.</li>
 *   <li>Reserva fondos y confirma la liquidación.</li>
 * </ul>
 *
 * @param orderId identificador único de la orden
 * @param request {@code PaymentRequest} con medios y metadatos
 * @return {@code PaymentResult} con estado final y referencias
 * @throws ResourceNotFoundException si la orden no existe
 * @throws BusinessRuleViolationException si la orden no es elegible
 * @see PaymentValidator
 */
```

### 7.5 Validación

- **Controllers:** `@Valid` + anotaciones Jakarta (`@NotNull`, `@NotBlank`, `@Size`, `@Positive`, etc.)
- **Services:** Validaciones complejas de negocio en clases dedicadas (`{Concepto}Validator`).

### 7.6 Logging

- **Herramienta:** SLF4J (vía Lombok `@Slf4j`)
- **Nivel INFO:** Operaciones normales (inicio/fin de operaciones críticas)
- **Nivel DEBUG:** Detalles de ejecución
- **Nivel WARN:** Condiciones anómalas (reglas de negocio incumplidas, retries)
- **Nivel ERROR:** Excepciones y errores inesperados
- **Contexto:** Siempre incluir IDs y claves relevantes
- **Sensibilidad:** Nunca loggear passwords, tokens, datos personales

**Ejemplo:**
```java
log.info("Verificando estado de salud de la aplicación: {}", applicationName);
log.error("Error al procesar producto: orderId={}, error={}", orderId, ex.getMessage(), ex);
```

---

## 8. Testing (Unitario, Integración, Datos de Prueba)

### 8.1 Framework y Herramientas

| Herramienta | Versión | Propósito | Status |
|-----------|---------|-----------|--------|
| **JUnit 5 (Jupiter)** | `5.x` (via Spring Boot) | Testing unitario | ✅ Incluido |
| **Mockito** | Pendiente | Mocking de dependencias | 🔲 Pendiente |
| **MockMvc** | Pendiente | Testing de controllers | 🔲 Pendiente |
| **Testcontainers** | Pendiente | Integración con BD en tests | 🔲 Pendiente |
| **AssertJ** | Pendiente | Assertions fluidas | 🔲 Pendiente |

### 8.2 Estructura de Tests

```
src/test/java/com/ar/laboratory/demoiaproduct/
├─ DemoIaProductApplicationTests.java
├─ controller/
│  └─ HealthControllerTest.java (pendiente)
├─ service/
│  └─ ProductServiceTest.java (pendiente)
├─ repository/
│  └─ ProductRepositoryTest.java (pendiente)
└─ mapper/
   └─ HealthMapperTest.java (pendiente)
```

### 8.3 Convención de Nombres de Tests

**Patrón:** `should_ExpectedBehavior_When_StateUnderTest`

**Ejemplos:**
- `should_ReturnHealthStatus_When_ApplicationIsRunning()`
- `should_CreateProduct_When_RequestIsValid()`
- `should_ThrowResourceNotFoundException_When_ProductDoesNotExist()`

### 8.4 Estructura de Test (Given/When/Then)

```java
@DisplayName("HealthController: checkHealth()")
class HealthControllerTest {

    @Test
    @DisplayName("should_ReturnHealthStatus_When_ApplicationIsRunning")
    void should_ReturnHealthStatus_When_ApplicationIsRunning() {
        // Given: estado inicial de la aplicación
        var healthInfo = HealthInfo.builder()
            .applicationStatus("UP")
            .statusMessage("OK")
            .applicationVersion("1.0.0")
            .checkTime(LocalDateTime.now())
            .build();

        // When: se invoca el endpoint GET /api/health
        var response = controller.checkHealth();

        // Then: verifica respuesta exitosa y contenido
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("UP");
    }
}
```

### 8.5 Datos de Prueba

**Ubicación:** `src/main/resources/sql/`

**Scripts disponibles:**
- `product-api-db.sql`: DDL (tablas, índices, triggers)
- `product-data.sql`: DML (inserciones de datos de ejemplo)

**Productos de ejemplo (30 registros):**
- Electrónica (laptops, mouses, monitores, webcams, etc.)
- Monedas: USD y ARS
- Estados: activos, inactivos, stock bajo
- Rangos de precio: $20 a $1,395

---

## 9. CI/CD y Calidad (Análisis Estático, Cobertura, Quality Gate)

### 9.1 Pipeline CI/CD

| Etapa | Herramienta | Status | Configuración |
|-------|-----------|--------|--------------|
| **Build** | Maven | ✅ Configurado | `mvn clean package` |
| **Unit Tests** | JUnit 5 | ✅ Configurado | Ejecución automática durante build |
| **Integration Tests** | Pendiente | 🔲 Pendiente | Configurar con Testcontainers |
| **Code Analysis** | Pendiente | 🔲 Pendiente | SonarQube (opcional) |
| **Code Coverage** | Pendiente | 🔲 Pendiente | JaCoCo (plugin Maven) |
| **Linting** | Pendiente | 🔲 Pendiente | Checkstyle, Spotbugs |
| **Containerization** | Docker | 🔲 Pendiente | Dockerfile + docker-compose |
| **Deployment** | Pendiente | 🔲 Pendiente | Estrategia según entorno |

### 9.2 Quality Gate (Recomendados)

```
- Cobertura de tests unitarios: >= 70%
- Cobertura de tests de integración: >= 50%
- Deuda técnica: < 10 días
- Bugs críticos: 0
- Vulnerabilidades de seguridad: 0
- Code smells mayores: 0
```

### 9.3 Plugins Maven

**Configurados:**
- `maven-compiler-plugin` (con anotación processors para Lombok)
- `spring-boot-maven-plugin` (empaquetado)

**Recomendados (añadir):**
- `jacoco-maven-plugin` (cobertura)
- `maven-checkstyle-plugin` (estilos)
- `maven-surefire-plugin` (ejecución tests)
- `sonar-maven-plugin` (análisis SonarQube)

---

## 10. Seguridad (Authn/Authz, Secretos)

### 10.1 Autenticación y Autorización

| Aspecto | Status | Detalles |
|--------|--------|---------|
| **Autenticación** | 🔲 Pendiente | Evaluar OAuth2, JWT o API Key según requisitos. |
| **Autorización** | 🔲 Pendiente | Spring Security con roles/permisos. |
| **CORS** | 🔲 Pendiente | Configurar según dominios permitidos. |
| **HTTPS** | 🔲 Pendiente | Certificados en producción. |

### 10.2 Gestión de Secretos

| Secreto | Ubicación Actual | Recomendación | Status |
|--------|-----------------|----------------|--------|
| **BD: Usuario** | `docker-compose.yml` | Usar variables de entorno o vault | ⚠️ A mejorar |
| **BD: Contraseña** | `docker-compose.yml` | Usar variables de entorno o vault | ⚠️ A mejorar |
| **API Keys** | Pendiente | Usar Spring Cloud Config o HashiCorp Vault | 🔲 Pendiente |
| **Tokens JWT** | Pendiente | Generar dinámicamente con clave privada | 🔲 Pendiente |

### 10.3 Mejoras de Seguridad Recomendadas

1. **Externalizar secretos:** Usar variables de entorno o gestores de secretos.
2. **HTTPS obligatorio en producción.**
3. **Validar todos los inputs** en controllers con `@Valid`.
4. **Sanitizar outputs** para prevenir XSS.
5. **Prevenir SQL injection:** Usar parameterized queries (JPA hace esto automáticamente).
6. **Rate limiting:** Implementar para prevenir abuso.
7. **Logging de seguridad:** Registrar intentos fallidos de autenticación.

---

## 11. Observabilidad (Logs, Métricas, Trazas)

### 11.1 Logging

**Herramienta:** SLF4J con Logback (vía Spring Boot)  
**Ubicación:** `src/main/resources/logback-spring.xml` (pendiente crear)

**Niveles configurados:**
- `INFO`: Operaciones normales
- `DEBUG`: Detalles de desarrollo
- `WARN`: Condiciones anómalas
- `ERROR`: Excepciones

**Ejemplo de configuración:**
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
    <logger name="com.ar.laboratory.demoiaproduct" level="DEBUG" />
</configuration>
```

### 11.2 Métricas

| Métrica | Herramienta | Status | Detalles |
|--------|-----------|--------|---------|
| **Health Endpoint** | Actuator | ✅ Básico | `/actuator/health` (requiere añadir dependencia) |
| **Métricas de negocio** | Micrometer | 🔲 Pendiente | Órdenes creadas, productos consultados, etc. |
| **Métricas de rendimiento** | Micrometer | 🔲 Pendiente | Latencia de requests, uso de memoria. |
| **Exportación** | Prometheus/Grafana | 🔲 Pendiente | Evaluación según requerimientos. |

### 11.3 Trazas Distribuidas

| Aspecto | Status | Detalles |
|--------|--------|---------|
| **Tracing** | 🔲 Pendiente | Evaluar Sleuth (Spring Cloud) o Jaeger. |
| **Correlación** | 🔲 Pendiente | Generar correlation-id en requests. |
| **Baggage** | 🔲 Pendiente | Propagar contexto entre servicios. |

---

## 12. Configuración por Entorno

### 12.1 Propiedades de Aplicación

**Archivo base:** `src/main/resources/application.properties`

#### Configuración Actual:
```properties
# Nombre de la aplicación
spring.application.name=demo-ia-product

# Server
server.port=8080
server.servlet.context-path=/demo-ia-product

# OpenAPI/Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.packages-to-scan=com.ar.laboratory.demoiaproduct
springdoc.paths-to-match=/api/**

# API Metadata
springdoc.info.title=Demo IA Product API
springdoc.info.description=API para gestión de productos con IA
springdoc.info.version=1.0.0
springdoc.info.contact.name=Laboratory Team
springdoc.info.contact.email=support@laboratory.com

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

### 12.2 Perfiles de Entorno (Pendientes)

```
src/main/resources/
├─ application.properties (base)
├─ application-dev.properties (desarrollo)
├─ application-test.properties (testing)
└─ application-prod.properties (producción)
```

**Ejemplo - `application-dev.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/productbd
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.root=INFO
logging.level.com.ar.laboratory.demoiaproduct=DEBUG
```

**Ejemplo - `application-prod.properties`:**
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.root=WARN
server.ssl.enabled=true
server.ssl.key-store=${KEYSTORE_PATH}
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
```

### 12.3 Variables de Entorno

| Variable | Propósito | Entorno |
|----------|-----------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil activo | Todos |
| `DB_URL` | URL de BD | Producción |
| `DB_USER` | Usuario de BD | Producción |
| `DB_PASSWORD` | Contraseña de BD | Producción |
| `KEYSTORE_PATH` | Ruta del keystore SSL | Producción |
| `KEYSTORE_PASSWORD` | Password del keystore | Producción |

---

## 13. Estrategia de Ramas y Commits

### 13.1 Modelo de Ramas

**Esquema:** Git Flow (recomendado para proyectos con ciclos de release)

```
main (producción)
 ↑
 ├── release/v1.0.0 (rama de release, preparación para producción)
 │    ↓
develop (integración, próxima versión)
 ↑
 ├── feature/agregar-crud-productos
 ├── feature/implementar-paginación
 ├── bugfix/corregir-mapeo-dto
 ├── hotfix/patch-seguridad (desde main)
 └── chore/actualizar-dependencias
```

### 13.2 Convenciones de Nombres de Ramas

| Tipo | Prefijo | Ejemplo |
|------|---------|---------|
| Feature (nueva funcionalidad) | `feature/` | `feature/agregar-busqueda-productos` |
| Bugfix | `bugfix/` | `bugfix/corregir-validacion-precio` |
| Hotfix (urgente, desde main) | `hotfix/` | `hotfix/parchar-sql-injection` |
| Release | `release/` | `release/v1.0.0` |
| Chore (tareas técnicas) | `chore/` | `chore/actualizar-lombok` |
| Docs (documentación) | `docs/` | `docs/actualizar-readme` |

### 13.3 Convenciones de Commits

**Formato:** Semantic Commit (CommonJS)

```
<tipo>(<scope>): <descripción corta>

<cuerpo (opcional)>

<footer (opcional)>
```

**Tipos:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Cambios de formato (espacios, indentación, semicolons)
- `refactor`: Refactorización sin cambios de comportamiento
- `perf`: Mejoras de rendimiento
- `test`: Adición o actualización de tests
- `chore`: Cambios en dependencies, build, CI/CD
- `ci`: Cambios en configuración de CI/CD

**Ejemplos:**
```
feat(product): agregar endpoint de búsqueda con filtros

- Implementar búsqueda por nombre y categoría
- Añadir paginación con offset/limit
- Documentar parámetros en OpenAPI

Closes #123
```

```
fix(mapper): corregir mapeo de localDateTime en HealthResponse

Fixes #456
```

```
chore(deps): actualizar spring-boot de 3.5.6 a 3.5.7
```

### 13.4 Pull Request (PR)

**Requerimientos:**
- Título descriptivo con tipo y scope.
- Descripción clara de cambios y propósito.
- Referencia a issues (`Closes #123`).
- Mínimo 2 aprobaciones antes de merge.
- Pasar todos los tests y quality gates.
- Squash or rebase antes de merge.

**Plantilla de PR (`.github/pull_request_template.md`):**
```markdown
## Descripción
Breve descripción del cambio.

## Tipo de Cambio
- [ ] Feature (nueva funcionalidad)
- [ ] Bug fix (corrección)
- [ ] Documentation (documentación)
- [ ] Refactor (refactorización)
- [ ] Test (tests)

## Testing Realizado
Describir tests realizados.

## Checklist
- [ ] El código sigue las convenciones del proyecto
- [ ] Se actualizó la documentación si aplica
- [ ] Se añadieron tests relevantes
- [ ] Todos los tests pasan localmente
- [ ] No se introdujeron problemas de seguridad

## Closes
Closes #(numero_issue)
```

---

## 14. Riesgos, Supuestos y Limitaciones

### 14.1 Riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|------------|--------|-----------|
| **Ausencia de autenticación** | Alta | Alto | Implementar OAuth2/JWT antes de pasar a producción. |
| **Credenciales en código** | Media | Alto | Usar variables de entorno y gestores de secretos. |
| **Falta de validación de inputs** | Media | Alto | Implementar Jakarta Bean Validation en todos los controllers. |
| **Escalabilidad limitada** | Media | Medio | Planear sharding/replicación de BD según crecimiento. |
| **Testing incompleto** | Alta | Medio | Establecer cobertura mínima de 70% y ejecutar en CI/CD. |
| **Documentación desactualizada** | Media | Bajo | Automatizar doc con OpenAPI; actualizar en cada PR. |

### 14.2 Supuestos

1. **Base de datos:** PostgreSQL 15.2 en ambiente local; producción TBD.
2. **Escalabilidad:** Inicialmente monolítica; migración a microservicios según demanda.
3. **Usuarios:** Equipo técnico con experiencia en Java 17 y Spring Boot.
4. **Conectividad:** Acceso a BD siempre disponible en desarrollo.
5. **Datos:** No hay requisitos de anonimización GDPR en fase MVP.

### 14.3 Limitaciones

| Limitación | Impacto | Observación |
|-----------|---------|------------|
| **Sin caché implementado** | Rendimiento | Evaluar Redis si latencia > 100ms. |
| **Sin búsqueda full-text** | Experiencia de usuario | Considerar Elasticsearch. |
| **Sin soft delete** | Auditoría | Implementar LogicalDelete con Hibernate Envers. |
| **Sin versionamiento de API** | Compatibilidad | Usar `/v1/`, `/v2/` en ruta base. |
| **Sin multi-tenancy** | Escalabilidad | Evaluar si aplica según requisitos empresariales. |

---

## 15. Glosario

| Término | Definición |
|--------|-----------|
| **DTO** | Data Transfer Object; objeto para transportar datos entre capas. |
| **JPA** | Java Persistence API; estándar para ORM en Java. |
| **MapStruct** | Framework para mapeo automático de objetos en compilación. |
| **OpenAPI** | Especificación estándar para documentar APIs REST. |
| **Swagger UI** | Interfaz web interactiva para explorar y probar APIs. |
| **Lombok** | Biblioteca para reducir boilerplate (getters, setters, logging, builders). |
| **Spring Boot** | Framework para crear aplicaciones stand-alone basadas en Spring. |
| **Spring Web** | Módulo para desarrollo de aplicaciones web y REST. |
| **Spring Data JPA** | Abstracción de Spring para ORM con Hibernate. |
| **PostgreSQL** | SGBD relacional open-source. |
| **Maven** | Herramienta para gestión de dependencias y build. |
| **CI/CD** | Continuous Integration / Continuous Deployment. |
| **SLF4J** | Simple Logging Facade for Java. |
| **Logback** | Implementación de SLF4J. |
| **JUnit 5 (Jupiter)** | Framework moderno para testing unitario en Java. |
| **Testcontainers** | Biblioteca para ejecutar contenedores Docker en tests. |
| **MockMvc** | Herramienta de Spring para testing de controllers sin servidor HTTP. |
| **Mockito** | Framework para crear mocks en tests. |
| **Quality Gate** | Criterios de calidad que debe cumplir el código (cobertura, bugs, etc.). |
| **SonarQube** | Plataforma de análisis estático de código. |
| **JaCoCo** | Plugin para medir cobertura de tests. |
| **Entity** | Clase anotada con `@Entity` que representa una tabla en BD. |
| **Repository** | Abstracción para acceso a datos (patrón DAO). |
| **Service** | Capa de lógica de negocio. |
| **Controller** | Capa de entrada HTTP que maneja requests/responses. |
| **Trigger** | Función que se ejecuta automáticamente ante eventos en BD (INSERT, UPDATE, DELETE). |
| **Index** | Estructura en BD para acelerar búsquedas. |
| **Package-by-feature** | Organización de paquetes por funcionalidad en lugar de por capa. |
| **SRP** | Single Responsibility Principle; principio SOLID. |
| **SOLID** | Conjunto de principios para código limpio y mantenible. |
| **DDD** | Domain-Driven Design; enfoque de diseño centrado en el dominio. |
| **REST** | Representational State Transfer; estilo arquitectónico para APIs. |
| **HTTP** | HyperText Transfer Protocol. |
| **JSON** | JavaScript Object Notation; formato de datos. |
| **SQL** | Structured Query Language. |
| **DDL** | Data Definition Language (CREATE, ALTER, DROP). |
| **DML** | Data Manipulation Language (INSERT, UPDATE, DELETE). |

---

## Notas Finales y Próximos Pasos

1. **Inmediatos:**
   - Añadir dependencias Maven (MapStruct, Spring Data JPA, Validation, OpenAPI UI).
   - Crear entidad `Product` y repositorio.
   - Implementar `ProductService` con validaciones.
   - Desarrollar controlador CRUD completo con documentación OpenAPI.

2. **Corto plazo:**
   - Implementar tests unitarios e integración (cobertura >= 70%).
   - Centralizar manejo de errores en `GlobalExceptionHandler`.
   - Crear perfiles de entorno (dev, test, prod).
   - Externalizar secretos (variables de entorno).

3. **Mediano plazo:**
   - Implementar autenticación/autorización (OAuth2 o JWT).
   - Añadir observabilidad (logging mejorado, métricas, trazas).
   - Evaluar y implementar búsqueda full-text (Elasticsearch).
   - Containerización y CI/CD (GitHub Actions, GitLab CI, etc.).

4. **Largo plazo:**
   - Migración a arquitectura de microservicios si aplica.
   - Implementar caché (Redis).
   - Auditoría completa (Envers).
   - Escalabilidad horizontal (sharding, replicación).

---

**Versión del documento:** 1.0  
**Fecha de creación:** 2025-11-11  
**Última actualización:** 2025-11-11  
**Autor/Propietario:** Arquitecto de Software Senior  
**Estado:** Vigente (revisión anual recomendada)

