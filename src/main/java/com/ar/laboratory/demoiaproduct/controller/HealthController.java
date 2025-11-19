package com.ar.laboratory.demoiaproduct.controller;

import com.ar.laboratory.demoiaproduct.dto.HealthInfo;
import com.ar.laboratory.demoiaproduct.dto.HealthResponse;
import com.ar.laboratory.demoiaproduct.mapper.HealthMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador de Health Check con documentación OpenAPI
 * Usa Lombok @RequiredArgsConstructor para inyección de dependencias
 * Usa Lombok @Slf4j para logging
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Endpoints para verificar el estado de la aplicación")
public class HealthController {

    private final HealthMapper healthMapper;

    @Value("${spring.application.name}")
    private String applicationName;

    @Operation(
            summary = "Verificar estado de la aplicación",
            description = "Endpoint personalizado para verificar que la aplicación está funcionando correctamente. " +
                    "Demuestra el uso de Lombok, MapStruct y documentación OpenAPI."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aplicación funcionando correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HealthResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<HealthResponse> checkHealth() {
        log.info("Verificando estado de salud de la aplicación: {}", applicationName);

        // Crear HealthInfo usando record
        var healthInfo = new HealthInfo(
                "UP",
                "Aplicación funcionando correctamente",
                "1.0.0",
                LocalDateTime.now()
        );

        // Usar MapStruct para convertir a HealthResponse
        var response = healthMapper.toHealthResponse(healthInfo);

        log.info("Health check completado: {}", response.status());

        return ResponseEntity.ok(response);
    }
}
