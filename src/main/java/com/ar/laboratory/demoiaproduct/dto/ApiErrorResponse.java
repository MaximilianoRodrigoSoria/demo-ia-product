package com.ar.laboratory.demoiaproduct.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.util.List;
import java.util.Objects;

/**
 * Respuesta de error compatible con RFC 7807 (problem+json).
 * Implementa ErrorResponse de Spring para integración con el framework.
 */
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
