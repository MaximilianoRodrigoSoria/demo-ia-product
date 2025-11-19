# Best Practices – Spring Boot & Java 17

> Guía curada para proyectos **Spring Boot 3.x** (Java 17 LTS) con enfoque en APIs REST, arquitectura limpia, observabilidad y seguridad. Aplica a servicios monolíticos modulares o microservicios.

---

## 1) Configuración del proyecto

### 1.1 Java 17 + Spring Boot
- **Compatibilidad:** Spring Boot 3.x soporta Java 17. Use siempre la versión de Boot 3.x que su equipo haya validado.
- **Encoding & Locale:** UTF‑8; evitar dependencias de locale del host.
- **Módulos (opcional):** `module-info.java` para límites claros si el equipo lo adopta.

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
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
  implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.0"))
}
```

### 1.2 Dependencias recomendadas
- `spring-boot-starter-web` (o `spring-boot-starter-webflux` si es **reactivo**).
- `spring-boot-starter-validation`, `spring-boot-starter-data-jpa` (si aplica), `spring-boot-starter-security`.
- Observabilidad: `spring-boot-starter-actuator`, Micrometer, exportadores (Prometheus/OTel).
- Documentación: `org.springdoc:springdoc-openapi-starter-webmvc-ui`.
- Mapeo: `org.mapstruct:mapstruct` + processor.
- Tests: JUnit 5, Mockito, Testcontainers.

### 1.3 Estilo y calidad
- **Formateo** automático (Spotless/Google Java Format).
- **Análisis estático** (Checkstyle/PMD/SpotBugs) y **Sonar**.
- **Coverage** razonable; priorizar pruebas significativas sobre %.

---

## 2) Arquitectura y organización

### 2.1 Capas y responsabilidades
- **Controller**: delgado; orquesta entrada/salida HTTP, sin lógica de negocio.
- **Service**: reglas de negocio, transacciones, orquestación.
- **Repository**: persistencia (Spring Data/JPA). Evitar lógica de negocio aquí.
- **DTOs**: entrada/salida de API; **no** exponer entidades JPA.
- **Mappers**: MapStruct; no mapear en los controladores.
- **Config**: `@Configuration` + `@ConfigurationProperties` para propiedades agrupadas.

Estructura sugerida (ejemplo):
```
com.example.app
 ├─ api (controllers, models request/response)
 ├─ application (services, use cases)
 ├─ domain (entities, value objects, rules, ports)
 ├─ infrastructure (adapters: db, messaging, http)
 ├─ config (configuración, security, beans)
 └─ common (exceptions, error model, utils)
```

### 2.2 ADRs y decisiones
- Registrar decisiones con **ADRs** (motivación, alternativas, impacto).
- Mantener diagramas C4 y contratos OpenAPI versionados.

---

## 3) Lenguaje – Java 17

### 3.1 Features clave
- **Records** para DTOs y valores inmutables (introducidos en Java 16 y estables en 17).
- **Pattern matching para instanceof** (presentado y usable en Java 16+).
- **Sealed classes** estándar en Java 17 para jerarquías cerradas.
- **Project Loom (Virtual Threads)**: caracter experimental/externo a Java 17 — si lo requiere, evaluarlo en su propia sección y realizar pruebas de rendimiento y compatibilidad.
- **Sequenced Collections** y otras mejoras recientes en el JDK; revisar compatibilidad de librerías.

**Ejemplo – record DTO:**
```java
public record CreateUserRequest(
  @NotBlank String email,
  @NotBlank String fullName
) {}
```

**Ejemplo usando pattern matching y checks seguros:**
```java
static String label(Object o) {
  if (o == null) return "null";
  if (o instanceof Integer i) return "int:" + i;
  if (o instanceof String s && s.isBlank()) return "empty";
  if (o instanceof String s) return s;
  return "other";
}
```

### 3.2 Virtual Threads / Project Loom (nota)
- Project Loom (virtual threads) no forma parte del JDK estándar en Java 17; es una iniciativa independiente que puede utilizarse de forma experimental. Antes de adoptarlo en producción, validar compatibilidad con drivers/blocking libs y realizar pruebas de carga.

**Configuración (Executor):**
```java
@Configuration
class VirtualThreadConfig {
  @Bean
  Executor taskExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }
}
```

**Buenas prácticas:**
- Mantener operaciones **CPU-bound** fuera de VT intensivo; considerar pools dedicados.
- Revisar libs bloqueantes; evitar bloqueos de larga duración (locks) innecesarios.

---

## 4) Controladores REST

### 4.1 Diseño de API
- Paths **kebab-case**, recursos sustantivos, sub-recursos: `/users/{id}/addresses`.
- Verbos HTTP correctos; **idempotencia** en `PUT` y `DELETE`. Considerar **idempotency keys** en `POST` con reintentos.
- Versionado por URI `/api/v1`. Definir política de deprecación clara.

### 4.2 Validación y DTOs
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {
  private final UserService service;

  @PostMapping
  ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
    var created = service.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
```

### 4.3 Manejo de errores – RFC 7807 (problem+json)
**Modelo de error:**
```java
public record ApiError(
  Instant timestamp,
  int status,
  String errorCode,
  String message,
  String path,
  String correlationId,
  List<FieldError> details
) {
  public record FieldError(String field, String code, String message) {}
}
```

**Control centralizado:**
```java
@RestControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> onValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var details = ex.getBindingResult().getFieldErrors().stream()
      .map(f -> new ApiError.FieldError(f.getField(), f.getCode(), f.getDefaultMessage()))
      .toList();
    var error = new ApiError(Instant.now(), 400, "VALIDATION_ERROR",
      "Input validation failed", req.getRequestURI(),
      MDC.get("correlationId"), details);
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ApiError> onNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    var error = new ApiError(Instant.now(), 404, "RESOURCE_NOT_FOUND",
      ex.getMessage(), req.getRequestURI(), MDC.get("correlationId"), List.of());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }
}
```

---

## 5) Servicios, transacciones y dominio

### 5.1 Reglas de negocio en el Service
- **@Transactional** en métodos de servicio (límite de consistencia).
- Declarar **invariantes** y efectos secundarios (eventos, integraciones).

```java
@Service
@RequiredArgsConstructor
class UserService {
  private final UserRepository repo;
  private final UserMapper mapper;

  @Transactional
  public UserResponse create(CreateUserRequest req) {
    if (repo.existsByEmail(req.email())) {
      throw new BusinessRuleViolationException("Email already in use");
    }
    var entity = mapper.toEntity(req);
    return mapper.toResponse(repo.save(entity));
  }
}
```

### 5.2 Repositorios JPA
- Paginación por defecto en listados, **límites** de tamaño.
- Evitar `EAGER`; preferir **`LAZY`** + `JOIN FETCH` donde haga falta.
- Índices y consultas nativas documentadas cuando sea necesario.

---

## 6) Configuración y propiedades

### 6.1 `@ConfigurationProperties`
- Agrupar propiedades con prefijo; evitar dispersar `@Value`.
- Documentar propósito y rangos válidos.

```java
@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(String sender, Duration timeout) {}
```

**Registro del bean:**
```java
@Configuration
@EnableConfigurationProperties(MailProperties.class)
class MailConfig { }
```

### 6.2 Perfiles y secretos
- Perfiles: `application.yml`, `application-dev.yml`, etc.
- Secretos **fuera** del repo (Vault/SM). Nunca loguear PII/credenciales.

---

## 7) Seguridad

### 7.1 OAuth2 Resource Server (JWT)
```java
@Configuration
class SecurityConfig {

  @Bean
  SecurityFilterChain http(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
        .anyRequest().authenticated())
      .oauth2ResourceServer(rs -> rs.jwt());
    return http.build();
  }
}
```

- Autorizar con **@PreAuthorize("hasAuthority('SCOPE_users:read')")**.
- Cabeceras y políticas endurecidas (HSTS, no‑frame, no‑sniff).

---

## 8) Observabilidad y logging

### 8.1 Actuator y Micrometer
- Habilitar `/actuator/health`, `/metrics`, `/prometheus`. Proteger endpoints.
- Métricas de negocio (contadores/timers) en puntos clave.

### 8.2 Correlación y formato
- Usar **MDC** con `X-Correlation-ID`. Log JSON estructurado.
- Niveles: `INFO` para eventos relevantes; `DEBUG` diagnósticos; `WARN/ERROR` con contexto.

**Filtro de correlación (simplificado):**
```java
@Component
class CorrelationFilter implements Filter {
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

- `springdoc-openapi` para generar `/v3/api-docs` y **Swagger UI**.
- Completar descripciones, ejemplos y modelos de error.
- Publicar contrato y **versionarlo** con la API.

```java
@Operation(summary = "Create user", description = "Registers a new user.")
@ApiResponses({
  @ApiResponse(responseCode = "201", description = "Created"),
  @ApiResponse(responseCode = "400", description = "Validation error",
               content = @Content(schema = @Schema(implementation = ApiError.class)))
})
```

---

## 10) Pruebas (JUnit 5)

### 10.1 Unidades y slices
- Controladores con **MockMvc** y JSON assertions.
- Servicios con Mockito (mocks de repositorios/adapters).
- Validaciones con `Validator` de Jakarta.

### 10.2 Integración
- **Testcontainers** para DB/MQ reales.
- Cargar **dataset mínimo**; tests idempotentes.

```java
@SpringBootTest
@Testcontainers
class UserRepositoryIT {
  static PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:16-alpine");
  @BeforeAll static void start() { db.start(); }

  @Test void should_persist_user() { /* ... */ }
}
```

### 10.3 Naming & reporting
- `@DisplayName` y estilo BDD: `should_Create_When_RequestIsValid`.
- Medir cobertura de **ramas** críticas; evitar tests frágiles.

---

## 11) Rendimiento y resiliencia

- **Virtual Threads** para I/O masivo; **Reactive** si se requiere back‑pressure extremo y streaming.
- **Timeouts/reintentos** con Resilience4j (retry, circuit‑breaker, bulkhead).
- **Caching** con Spring Cache (TTL y claves explícitas).
- Serialización: Jackson con `@JsonInclude(NON_NULL)`, evitar ciclos; records simplifican.
- **GC**: G1 por defecto funciona bien; validar límites de contenedor (`-XX:+UseContainerSupport`). Considerar ZGC para latencia.

---

## 12) Mensajería e integración

- Publicar eventos **de dominio** (outbox + CDC) si se requiere confiabilidad inter-servicios.
- Estándares de **idempotencia** al consumir (dedupe key).
- Contratos claros (Avro/JSON Schema) y versionados.

---

## 13) Seguridad adicional

- Validar entrada **siempre** (Bean Validation + validaciones de negocio).
- Sanitizar salidas donde aplique (HTML/SQL). JPA parametrizado evita SQLi.
- Rotación de secretos, mínima superficie de permisos (principio de menor privilegio).
- Revisiones periódicas (OWASP ASVS/Top10).

---

## 14) Entorno, despliegue y CI/CD

- **12‑Factor App**: configuración por entorno, logs a stdout, stateless.
- Imagenes **distroless** o slim, `jlink` opcional para runtime minimal.
- **Health/Readiness/Liveness** para orquestadores (K8s).
- **Pipeline** con build reproducible, escaneo, tests, reportes, despliegue progresivo.
- Etiquetado semántico, `CHANGELOG` y notas de versión.

---

## 15) Guías de código (resumen)

- Inyección por **constructor**; evitar `@Autowired` en campos.
- No capturar/arrojar **`Exception`** genérica; excepciones específicas.
- Métodos **pequeños** y con nombres expresivos.
- Evitar lógica compleja en controladores; mover a servicios.
- **MapStruct** para mapeos; **records** para DTOs.
- Documentar **públicos** con JavaDoc; omitir comentarios triviales en producción.
- Mantener **inmutabilidad** donde sea posible; evitar estado compartido.
- Registrar **ADRs** para decisiones relevantes.

---

## 16) Checklist rápido (para PRs)

- [ ] Endpoints cumplen convención REST y versionado.
- [ ] DTOs validados; entidades no expuestas.
- [ ] Errores devuelven **problem+json** consistente.
- [ ] Transacciones en Service; repos sin lógica de negocio.
- [ ] Logging estructurado con **correlationId**.
- [ ] OpenAPI actualizado y útil.
- [ ] Tests unitarios + integración (Testcontainers si aplica).
- [ ] Límite de página y tiempos configurados.
- [ ] Sin secretos en logs o código.
- [ ] ADR creado/actualizado si hubo decisión mayor.

---

## 17) Recursos útiles

- Spring Boot Reference, Spring Data, Spring Security.
- Micrometer & Actuator; OpenTelemetry.
- Springdoc OpenAPI Starter.
- Resilience4j (fault tolerance).
- Testcontainers (integración realista).

---

### 18) No borrar pruebas existosas

Si ves que la prueba funciona correctamente, no la borres para evitar perder evidencia de su éxito.

> **Nota:** adapte estas prácticas al contexto, evitando sobre‑ingeniería. Priorice claridad, consistencia y métricas de negocio.
