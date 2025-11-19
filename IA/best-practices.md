Perfecto, con esas restricciones voy a reescribir y enriquecer la guía completa.
A continuación tenés la **versión mejorada**, incorporando:

* Excepciones custom.
* Uso de `ErrorResponse`.
* Modelado consistente en OpenAPI.
* Manejo de excepciones con logging en los `catch`.
* Uso de `@RequiredArgsConstructor` (sin `@Autowired`).
* Versionado `/api/v1/`.
* Uso de programación funcional donde tenga sentido.
* Uso de `var` y `Objects.isNull / Objects.nonNull`.
* Lineamientos para manejo de hilos.
* Sección de README base.

---

# Best Practices – Spring Boot 3.x & Java 17

> Guía curada para proyectos **Spring Boot 3.x** (Java 17 LTS) con enfoque en APIs REST, arquitectura limpia, observabilidad, seguridad y manejo robusto de errores. Aplica a servicios monolíticos modulares o microservicios.

---

## 1) Configuración del proyecto

### 1.1 Java 17 + Spring Boot

* **Compatibilidad:** Spring Boot 3.x requiere Java 17+; usar siempre la versión de Boot 3.x homologada por el equipo.
* **Encoding & Locale:** Forzar UTF-8; evitar depender del locale del host.
* **Módulos (opcional):** `module-info.java` para delimitar módulos si el equipo adopta JPMS.

**Maven (fragmento):**

```xml
<properties>
  <java.version>17</java.version>
  <spring-boot.version>3.3.0</spring-boot.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring-boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**Gradle (Kotlin DSL):**

```kotlin
java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

dependencies {
  implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
}
```

### 1.2 Dependencias recomendadas

* Core:

    * `spring-boot-starter-web` (o `spring-boot-starter-webflux` si el servicio es **reactivo**).
    * `spring-boot-starter-validation`.
    * `spring-boot-starter-data-jpa` (si aplica).
    * `spring-boot-starter-security`.
* Observabilidad:

    * `spring-boot-starter-actuator`.
    * Micrometer + exportador (Prometheus, OpenTelemetry, etc.).
* Documentación:

    * `org.springdoc:springdoc-openapi-starter-webmvc-ui`.
* Mapeo:

    * `org.mapstruct:mapstruct` + `mapstruct-processor`.
* Testing:

    * JUnit 5, Mockito, AssertJ.
    * Testcontainers para DB/MQ.

### 1.3 Estilo y calidad

* **Formateo** automático (Spotless, Google Java Format).
* **Análisis estático** (Checkstyle, PMD, SpotBugs) + Sonar.
* **Cobertura**: priorizar pruebas significativas sobre % numérico.
* Uso consistente de:

    * `var` para variables locales donde el tipo sea **obvio**.
    * `Objects.isNull(obj)` / `Objects.nonNull(obj)` en lugar de `obj == null` / `obj != null`.

---

## 2) Arquitectura y organización

### 2.1 Capas y responsabilidades

* **Controller**:

    * Delgado.
    * Orquesta entrada/salida HTTP.
    * Sin lógica de negocio.
* **Service**:

    * Reglas de negocio.
    * Límites transaccionales (`@Transactional`).
    * Orquestación de repositorios y adaptadores.
* **Repository**:

    * Persistencia (Spring Data/JPA).
    * Sin lógica de negocio.
* **DTOs**:

    * Entrada/salida de API.
    * **Nunca** exponer entidades JPA directamente.
* **Mappers**:

    * MapStruct u otro mapper dedicado.
    * No mapear en controladores.
* **Config**:

    * Clases `@Configuration`.
    * `@ConfigurationProperties` para agrupar propiedades.

Estructura sugerida:

```text
com.example.app
 ├─ api
 │   ├─ controller
 │   ├─ model (request/response DTOs, ErrorResponse)
 │   └─ handler (GlobalExceptionHandler)
 ├─ application (services, use cases)
 ├─ domain (entities, value objects, reglas, puertos)
 ├─ infrastructure (db, messaging, http clients, adapters)
 ├─ config (security, beans, properties)
 └─ common (exceptions custom, error codes, utils)
```

### 2.2 ADRs y decisiones

* Utilizar **ADRs** para registrar decisiones técnicas relevantes:

    * Contexto, alternativas, decisión, impacto.
* Mantener:

    * Diagramas C4 actualizados.
    * Contratos OpenAPI versionados (v1, v2, etc.).

---

## 3) Lenguaje – Java 17

### 3.1 Features clave

* **Records** para DTOs y modelos inmutables.
* **Pattern matching para `instanceof`**.
* **Sealed classes** para jerarquías cerradas (ej. tipos de error, estados).
* `var` para variables locales donde el tipo sea evidente y mejore la legibilidad.
* Uso explícito de:

    * `Objects.isNull(obj)` y `Objects.nonNull(obj)`.
    * `Optional` para expresar ausencia, no para todo.

**Ejemplo – record DTO con validación:**

```java
public record CreateUserRequest(
  @NotBlank String email,
  @NotBlank String fullName
) {}
```

**Ejemplo – pattern matching + Objects:**

```java
static String label(Object o) {
  if (Objects.isNull(o)) return "null";

  if (o instanceof Integer i) {
    return "int:" + i;
  }

  if (o instanceof String s && s.isBlank()) {
    return "empty";
  }

  if (o instanceof String s) {
    return s;
  }

  return "other";
}
```

### 3.2 Programación funcional (cuando aplique)

* Usar Streams/API funcional para:

    * Filtrar, mapear, reducir colecciones.
    * Transformar DTOs y listas.
* Evitar:

    * Pipelines excesivamente complejos.
    * Side-effects no evidentes dentro de lambdas.

**Ejemplo:**

```java
public List<UserResponse> findAllActive() {
  return repo.findAllActive().stream()
    .map(mapper::toResponse)
    .toList();
}
```

### 3.3 Hilos y concurrencia (visión general)

* Evitar crear hilos manualmente con `new Thread(...)`.
* Utilizar:

    * `ExecutorService` / `Executor` inyectado como bean.
    * Anotación `@Async` (Spring) para tareas asíncronas controladas.
    * Virtual Threads (Project Loom) si el contexto lo permite y se ha validado.
* Manejar correctamente:

    * `InterruptedException` (si se usa programación con hilos clásicos).
    * Timeouts en operaciones bloqueantes.
* Liberar recursos (shutdown de pools) adecuadamente.

---

## 4) Controladores REST

### 4.1 Diseño de API

* Prefijo **obligatorio**: `/api/v1/` para todas las APIs.

    * Ejemplo: `/api/v1/users`, `/api/v1/users/{id}/addresses`.
* Paths en **kebab-case**.
* Buen uso de verbos HTTP:

    * `GET`, `POST`, `PUT`, `PATCH`, `DELETE`.
* Idempotencia:

    * `PUT` y `DELETE` idempotentes.
    * Considerar **idempotency keys** en `POST` para operaciones sensibles.
* Versionado:

    * Nuevas versiones como `/api/v2/...` cuando haya cambios breaking.

### 4.2 Validación y DTOs

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {

  private final UserService service;

  @PostMapping
  @Operation(
    summary = "Create user",
    description = "Registers a new user."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Created",
      content = @Content(schema = @Schema(implementation = UserResponse.class))),
    @ApiResponse(responseCode = "400", description = "Validation error",
      content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
    var created = service.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
```

> Notas:
>
> * No usar `@Autowired` en campos; preferir **inyección por constructor** + `@RequiredArgsConstructor`.
> * Modelar siempre Request/Response en OpenAPI mediante anotaciones (`@Operation`, `@ApiResponse`, `@Schema`, ejemplos, etc.).

### 4.3 Manejo de errores – `ErrorResponse` (RFC 7807 compatible)

Se recomienda una respuesta basada en `problem+json` usando el `ErrorResponse` de Spring (`org.springframework.web.ErrorResponse`) o una implementación propia.

**Modelo de respuesta de error (record) implementando `ErrorResponse`:**

```java
public record ApiErrorResponse(
  HttpStatus status,
  String errorCode,
  String message,
  String path,
  String correlationId,
  List<FieldError> details
) implements ErrorResponse {

  public record FieldError(String field, String code, String message) {}

  @Override
  public HttpStatus getStatusCode() {
    return status;
  }

  @Override
  public ProblemDetail getBody() {
    var detail = ProblemDetail.forStatusAndDetail(status, message);
    detail.setTitle(errorCode);
    detail.setProperty("correlationId", correlationId);
    if (Objects.nonNull(path)) {
      detail.setProperty("path", path);
    }
    if (Objects.nonNull(details) && !details.isEmpty()) {
      detail.setProperty("details", details);
    }
    return detail;
  }
}
```

**Excepciones custom alineadas al modelo de error:**

```java
public class BusinessRuleViolationException extends RuntimeException {
  private final String errorCode;

  public BusinessRuleViolationException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}

public class ResourceNotFoundException extends RuntimeException {
  private final String errorCode;

  public ResourceNotFoundException(String message) {
    super(message);
    this.errorCode = "RESOURCE_NOT_FOUND";
  }

  public String getErrorCode() {
    return errorCode;
  }
}
```

**Handler global con logging en los `catch` implícitos (métodos `@ExceptionHandler`):**

```java
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorResponse> onValidation(MethodArgumentNotValidException ex,
                                             HttpServletRequest req) {

    var details = ex.getBindingResult().getFieldErrors().stream()
      .map(f -> new ApiErrorResponse.FieldError(
        f.getField(),
        f.getCode(),
        f.getDefaultMessage()
      ))
      .toList();

    var error = new ApiErrorResponse(
      HttpStatus.BAD_REQUEST,
      "VALIDATION_ERROR",
      "Input validation failed",
      req.getRequestURI(),
      MDC.get("correlationId"),
      details
    );

    log.warn("Validation error on path {}: {}", req.getRequestURI(), details, ex);

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ErrorResponse> onNotFound(ResourceNotFoundException ex,
                                           HttpServletRequest req) {

    var error = new ApiErrorResponse(
      HttpStatus.NOT_FOUND,
      ex.getErrorCode(),
      ex.getMessage(),
      req.getRequestURI(),
      MDC.get("correlationId"),
      List.of()
    );

    log.info("Resource not found on path {}: {}", req.getRequestURI(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(BusinessRuleViolationException.class)
  ResponseEntity<ErrorResponse> onBusiness(BusinessRuleViolationException ex,
                                           HttpServletRequest req) {

    var error = new ApiErrorResponse(
      HttpStatus.UNPROCESSABLE_ENTITY,
      ex.getErrorCode(),
      ex.getMessage(),
      req.getRequestURI(),
      MDC.get("correlationId"),
      List.of()
    );

    log.warn("Business rule violation on path {}: {}", req.getRequestURI(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponse> onUnexpected(Exception ex, HttpServletRequest req) {

    var error = new ApiErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "INTERNAL_ERROR",
      "Unexpected error",
      req.getRequestURI(),
      MDC.get("correlationId"),
      List.of()
    );

    log.error("Unexpected error on path {}: {}", req.getRequestURI(), ex.getMessage(), ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
```

> Reglas:
>
> * **Siempre loguear en los manejadores** de excepción (`@ExceptionHandler`) con contexto.
> * En código de negocio, usar `try/catch` sólo cuando sea necesario y **loggear** dentro del `catch` antes de transformar o propagar.

---

## 5) Servicios, transacciones y dominio

### 5.1 Reglas de negocio en el Service

```java
@Service
@RequiredArgsConstructor
@Slf4j
class UserService {

  private final UserRepository repo;
  private final UserMapper mapper;

  @Transactional
  public UserResponse create(CreateUserRequest req) {

    if (repo.existsByEmail(req.email())) {
      log.info("Attempt to create user with existing email: {}", req.email());
      throw new BusinessRuleViolationException("Email already in use", "EMAIL_IN_USE");
    }

    var entity = mapper.toEntity(req);

    var saved = repo.save(entity);
    log.debug("User saved with id={}", saved.getId());

    return mapper.toResponse(saved);
  }
}
```

> Reglas:
>
> * `@Transactional` en métodos de servicio (no en controladores).
> * Validar invariantes de negocio y lanzar **excepciones custom**.
> * Usar programación funcional donde sea legible (p.ej. `Optional.ofNullable(...).orElseThrow(...)`).

### 5.2 Repositorios JPA

* Paginación y límite de tamaño por defecto.
* Evitar `EAGER`; preferir `LAZY` + `JOIN FETCH` cuando se necesite.
* Documentar:

    * Consultas nativas.
    * Índices requeridos.

---

## 6) Configuración y propiedades

### 6.1 `@ConfigurationProperties`

```java
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(
  String sender,
  Duration timeout
) {}
```

**Registro del bean:**

```java
@Configuration
@EnableConfigurationProperties(MailProperties.class)
class MailConfig { }
```

### 6.2 Perfiles y secretos

* Perfiles:

    * `application.yml`, `application-dev.yml`, `application-prod.yml`, etc.
* Secretos **fuera** del repo:

    * Vault, Secret Manager, variables de entorno, etc.
* Nunca loguear:

    * Passwords, tokens, PII.

---

## 7) Seguridad

### 7.1 OAuth2 Resource Server (JWT)

```java
@Configuration
class SecurityConfig {

  @Bean
  SecurityFilterChain http(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/actuator/health",
          "/v3/api-docs/**",
          "/swagger-ui/**"
        ).permitAll()
        .anyRequest().authenticated())
      .oauth2ResourceServer(rs -> rs.jwt());
    return http.build();
  }
}
```

* `@PreAuthorize("hasAuthority('SCOPE_users:read')")` en métodos de servicio o controlador.
* Cabeceras endurecidas:

    * HSTS, X-Frame-Options, X-Content-Type-Options, etc.

---

## 8) Observabilidad y logging

### 8.1 Actuator y Micrometer

* Exponer y proteger:

    * `/actuator/health`
    * `/actuator/metrics`
    * `/actuator/prometheus`
* Métricas de negocio:

    * Contadores de operaciones.
    * Timers para endpoints críticos.

### 8.2 Correlación y formato

* Usar **MDC** con `X-Correlation-ID`.
* Logging JSON estructurado.
* Niveles:

    * `INFO` para flujo normal.
    * `DEBUG` para diagnóstico.
    * `WARN`/`ERROR` para problemas reales.

```java
@Component
class CorrelationFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    try {
      var http = (HttpServletRequest) req;
      var cid = Optional.ofNullable(http.getHeader("X-Correlation-ID"))
                        .orElse(UUID.randomUUID().toString());

      MDC.put("correlationId", cid);
      chain.doFilter(req, res);

    } finally {
      MDC.remove("correlationId");
    }
  }
}
```

---

## 9) Documentación OpenAPI

* Utilizar `springdoc-openapi` para generar:

    * `/v3/api-docs`
    * Swagger UI.
* Modelar:

    * Todos los endpoints.
    * DTOs de entrada y salida.
    * Modelos de error `ApiErrorResponse`.
* Mantener la documentación:

    * Versionada por API: `v1`, `v2`, etc.
    * Alineada con los cambios del código.

Ejemplo de documentación en controlador ya visto en 4.2.

---

## 10) Pruebas (JUnit 5)

### 10.1 Unit tests y slices

* Controladores:

    * `@WebMvcTest` + `MockMvc`.
* Servicios:

    * Tests unitarios con Mockito.
* Validaciones:

    * Usar `Validator` de Jakarta o tests específicos de constraints.

### 10.2 Integración

* Testcontainers para BD real:

```java
@SpringBootTest
@Testcontainers
class UserRepositoryIT {

  static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine");

  @BeforeAll
  static void start() {
    db.start();
  }

  @Test
  void should_persist_user() {
    // ...
  }
}
```

### 10.3 Naming & reporting

* Nombres descriptivos (`should_Create_When_RequestIsValid`).
* `@DisplayName` opcional para legibilidad en reportes.

---

## 11) Rendimiento, resiliencia y hilos

* Para operaciones de **I/O intensivo**:

    * Considerar **Virtual Threads** (Project Loom) en entornos compatibles y testeados.
    * Alternativamente, usar programación **reactiva** (WebFlux) si se requiere backpressure.
* Resiliencia:

    * Resilience4j:

        * Circuit breaker, retry, bulkhead, rate limiter.
* Caching:

    * Spring Cache con TTL razonable y claves claras.
* Manejo de hilos:

    * Definir `Executor` como bean:

      ```java
      @Configuration
      class ExecutorConfig {
  
        @Bean
        Executor taskExecutor() {
          return Executors.newFixedThreadPool(10);
        }
      }
      ```
    * Para tareas asíncronas controladas:

      ```java
      @Service
      @RequiredArgsConstructor
      class ReportService {
  
        @Async
        public CompletableFuture<Report> generateReportAsync(String id) {
          // lógica de generación
          return CompletableFuture.completedFuture(new Report(id));
        }
      }
      ```
    * Evitar crear hilos directos (`new Thread`) sin supervisión ni gestión.

---

## 12) Mensajería e integración

* Usar eventos **de dominio** y patrón **Outbox** cuando sea necesaria consistencia entre DB y colas.
* Idempotencia en consumidores:

    * Claves de deduplicación.
* Contratos bien definidos:

    * Avro / JSON Schema.
    * Versionado de esquemas.

---

## 13) Seguridad adicional

* Validar entrada (Bean Validation + reglas de negocio).
* Sanitizar salidas cuando aplique (HTML, etc.).
* Permisos mínimos (principio de menor privilegio).
* Revisiones periódicas:

    * OWASP Top 10, ASVS.

---

## 14) Entorno, despliegue y CI/CD

* 12-Factor App:

    * Configuración por entorno.
    * Logs en stdout.
    * Stateless.
* Imágenes:

    * Distroless o slim.
    * Opcionalmente `jlink` para runtime reducido.
* Health checks:

    * `/actuator/health` para liveness/readiness.
* Pipeline:

    * Build reproducible.
    * Escaneo de vulnerabilidades.
    * Tests automáticos.
    * Deploy progresivo / canary.

---

## 15) Guías de código (resumen)

* Inyección por **constructor** (`@RequiredArgsConstructor`), nunca `@Autowired` en campos.
* No capturar/arrojar `Exception` genérica salvo en puntos muy controlados (y loggeados).
* Métodos pequeños y expresivos.
* Lógica compleja fuera de los controladores.
* Uso de MapStruct para mapeos.
* Records para DTOs.
* `var` para variables locales donde el tipo sea claro.
* `Objects.isNull` / `Objects.nonNull` en lugar de comparaciones directas con `null`.
* Documentar APIs públicas con JavaDoc donde agreguen valor.
* Inmutabilidad donde sea posible.

---

## 16) Checklist rápido (para PRs)

* [ ] Endpoints en `/api/v1/...` con convención REST.
* [ ] DTOs de request/response modelados; entidades JPA no expuestas.
* [ ] Errores devuelven `problem+json` con `ApiErrorResponse` (`ErrorResponse`).
* [ ] Excepciones custom creadas para casos de negocio y not found.
* [ ] `GlobalExceptionHandler` con logs en cada `@ExceptionHandler`.
* [ ] Transacciones declaradas en Services, repos sin lógica de negocio.
* [ ] Logging estructurado con `correlationId`.
* [ ] OpenAPI actualizado (request, response, errores).
* [ ] Tests unitarios + de integración (Testcontainers si aplica).
* [ ] Límites de paginación y timeouts configurados.
* [ ] Sin secretos ni PII en logs/código.
* [ ] ADR actualizado si hubo decisiones relevantes sobre arquitectura.

---

## 17) README base recomendado

Cada proyecto debe contar con un `README.md` con al menos:

1. **Descripción general**

    * Propósito del servicio.
    * Dominios de negocio involucrados.
2. **Stack técnico**

    * Spring Boot versión.
    * Java versión.
    * DB, colas, otras dependencias externas.
3. **Arquitectura**

    * Diagrama simple (C4 nivel 1/2).
    * Principales módulos/paquetes.
4. **APIs**

    * Link a Swagger UI.
    * Link a `/v3/api-docs`.
    * Descripción general de los endpoints clave.
5. **Configuración**

    * Variables de entorno importantes.
    * Perfiles disponibles.
    * Ejemplos de `application-*.yml`.
6. **Ejecución local**

    * Requisitos previos (Docker, JDK, etc.).
    * Comandos para levantar DB / colas (docker-compose).
    * Comando para levantar la app.
7. **Testing**

    * Cómo ejecutar tests.
    * Notas sobre Testcontainers (si aplica).
8. **Observabilidad**

    * Endpoints de Actuator habilitados.
    * Cómo consultar métricas y logs.
9. **Decisiones de diseño**

    * Link a carpeta de ADRs.
10. **Convenciones**

* Links a guías internas de estilo, commits, branching, etc.

---

## 18) No borrar pruebas exitosas

Si una prueba está funcionando correctamente y tiene valor, **no la borres**: es evidencia de que un comportamiento crítico está cubierto.

---

> **Nota final:** ajustar estas prácticas al contexto, evitando sobre-ingeniería. Priorizar claridad, consistencia, observabilidad y métricas de negocio, manteniendo un manejo de errores robusto (excepciones custom + `ErrorResponse`) y un modelo de APIs bien definido en OpenAPI.


