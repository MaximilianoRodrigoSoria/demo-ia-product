package com.ar.laboratory.demoiaproduct.dto;

import java.time.OffsetDateTime;

/**
 * Respuesta estándar de error para la API.
 */
public record ErrorResponse(
        String code,
        String message,
        String detail,
        String traceId,
        OffsetDateTime timestamp
) {
    public static ErrorResponse notFound(String detail, String traceId) {
        return new ErrorResponse("APP-002", "No se encontraron productos", detail, traceId, OffsetDateTime.now());
    }
    public static ErrorResponse internal(String detail, String traceId) {
        return new ErrorResponse("APP-005", "Error interno", detail, traceId, OffsetDateTime.now());
    }
}

