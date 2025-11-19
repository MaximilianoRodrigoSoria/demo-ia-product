package com.ar.laboratory.demoiaproduct.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para health check usando Java 17 records.
 */
public record HealthResponse(
        String status,
        String message,
        String version,
        LocalDateTime timestamp
) {
}
