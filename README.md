# Demo IA Product API

API REST para la gestión de productos con inteligencia artificial.

## Tecnologías Configuradas

### 1. **Lombok**
Biblioteca para reducir código boilerplate en Java.

**Anotaciones utilizadas:**
- `@Data`: Genera getters, setters, toString, equals y hashCode
- `@Builder`: Permite construir objetos usando patrón builder
- `@NoArgsConstructor`: Constructor sin argumentos
- `@AllArgsConstructor`: Constructor con todos los argumentos
- `@RequiredArgsConstructor`: Constructor con campos final/required
- `@Slf4j`: Logger automático

**Ejemplo de uso:**
```java
@Data
@Builder
public class HealthResponse {
    private String status;
    private String message;
}

// Uso con Builder
HealthResponse response = HealthResponse.builder()
    .status("UP")
    .message("OK")
    .build();
```

### 2. **MapStruct**
Framework para mapeo de objetos que genera código en tiempo de compilación.

**Características:**
- Mapeo automático de campos con mismo nombre
- Mapeo personalizado con `@Mapping`
- Type-safe y performante
- Integración con Lombok

**Ejemplo:**
```java
@Mapper(componentModel = "SPRING")
public interface HealthMapper {
    @Mapping(source = "applicationStatus", target = "status")
    HealthResponse toHealthResponse(HealthInfo healthInfo);
}
```

### 3. **OpenAPI / Swagger**
Documentación interactiva de la API usando Springdoc.

**URLs importantes:**
- Swagger UI: `http://localhost:8080/demo-ia-product/swagger-ui.html`
- API Docs JSON: `http://localhost:8080/demo-ia-product/api-docs`

**Anotaciones utilizadas:**
- `@Tag`: Agrupa endpoints
- `@Operation`: Describe una operación
- `@ApiResponse`: Documenta respuestas
- `@Schema`: Documenta modelos

### 4. **Spring Boot Actuator**
Endpoints de monitoreo y salud de la aplicación.

**Endpoints disponibles:**
- Health: `http://localhost:8080/demo-ia-product/actuator/health`
- Info: `http://localhost:8080/demo-ia-product/actuator/info`

## Configuración del Proyecto

### Context Path
```properties
server.servlet.context-path=/demo-ia-product
```

Todas las URLs deben incluir el prefijo `/demo-ia-product`.

### Puerto del Servidor
```properties
server.port=8080
```

## Endpoints Disponibles

### Health Check Personalizado

**GET** `/demo-ia-product/api/health`

Retorna el estado de la aplicación con información detallada.

**Respuesta:**
```json
{
  "status": "UP",
  "message": "Aplicación funcionando correctamente",
  "version": "1.0.0",
  "timestamp": "2025-11-11T16:39:00"
}
```

**GET** `/demo-ia-product/api/health/status`

Retorna solo el estado de forma simple.

**Respuesta:**
```
UP
```

### Actuator Health

**GET** `/demo-ia-product/actuator/health`

Endpoint estándar de Spring Boot Actuator.

## Compilación y Ejecución

### Con Docker (Recomendado)

#### Iniciar la base de datos PostgreSQL
```bash
docker-compose up -d
```

Este comando:
- Inicia un contenedor PostgreSQL con la base de datos `productbd`
- Ejecuta automáticamente los scripts de inicialización:
  - `01-product-api-db.sql`: Crea la tabla product con triggers e índices
  - `02-product-data.sql`: Inserta 30 productos de ejemplo
- La base de datos estará disponible en `localhost:5432`

#### Credenciales de la base de datos
- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: productbd
- **Usuario**: user
- **Password**: password

#### Detener y eliminar contenedores
```bash
docker-compose down
```

#### Eliminar volúmenes (limpia la base de datos)
```bash
docker-compose down -v
```

### Sin Docker

#### Compilar el proyecto
```bash
mvn clean compile
```

#### Ejecutar la aplicación
```bash
mvn spring-boot:run
```

#### Empaquetar
```bash
mvn clean package
```

#### Ejecutar el JAR
```bash
java -jar target/demo-ia-product-0.0.1-SNAPSHOT.jar
```

## Verificación

Después de iniciar la aplicación, verifica los siguientes endpoints:

1. **Swagger UI**: http://localhost:8080/demo-ia-product/swagger-ui.html
2. **Health personalizado**: http://localhost:8080/demo-ia-product/api/health
3. **Actuator Health**: http://localhost:8080/demo-ia-product/actuator/health

## Estructura del Proyecto

```
demo-ia-product/
├── src/main/
│   ├── java/com/ar/laboratory/demoiaproduct/
│   │   ├── config/
│   │   │   └── OpenApiConfig.java          # Configuración de OpenAPI/Swagger
│   │   ├── controller/
│   │   │   └── HealthController.java       # Controlador REST con documentación
│   │   ├── dto/
│   │   │   ├── HealthInfo.java            # DTO interno
│   │   │   └── HealthResponse.java        # DTO de respuesta
│   │   ├── mapper/
│   │   │   └── HealthMapper.java          # Mapper MapStruct
│   │   └── DemoIaProductApplication.java  # Clase principal
│   └── resources/
│       ├── sql/
│       │   ├── product-api-db.sql         # Script DDL (crea tabla)
│       │   └── product-data.sql           # Script DML (inserta datos)
│       └── application.properties         # Configuración de la aplicación
├── docker-compose.yml                     # Configuración de Docker
├── pom.xml                               # Dependencias Maven
└── README.md                             # Esta documentación
```

## Dependencias Principales

- Spring Boot 3.5.7
- Java 17
- Lombok (última versión)
- MapStruct 1.6.3
- Springdoc OpenAPI 2.7.0
- Spring Boot Actuator
- PostgreSQL 15.2 (Docker)

## Datos de Prueba

El script `product-data.sql` inserta 30 productos de ejemplo en la base de datos:

- **20 productos activos** con stock disponible (SKU-002 a SKU-021)
  - Laptops, periféricos, accesorios de oficina
  - Precios en USD
  
- **3 productos especiales** (SKU-022 a SKU-024)
  - Productos descontinuados o con bajo stock
  
- **6 productos en pesos argentinos** (SKU-025 a SKU-030)
  - Productos con precios en ARS
  - Variedad de stock

El script se ejecuta automáticamente al iniciar el contenedor Docker por primera vez.

## Notas de Desarrollo

### MapStruct
- Las implementaciones de los mappers se generan en `target/generated-sources/annotations/`
- Es necesario compilar el proyecto para que MapStruct genere el código
- La integración con Lombok requiere `lombok-mapstruct-binding`

### Lombok
- Los annotation processors están configurados en el `maven-compiler-plugin`
- El orden es importante: Lombok primero, luego MapStruct

### OpenAPI
- La documentación se genera automáticamente escaneando los controladores
- Los schemas se generan a partir de los DTOs
- Se puede personalizar la configuración en `OpenApiConfig.java`

## Autor

Laboratory Team - support@laboratory.com
