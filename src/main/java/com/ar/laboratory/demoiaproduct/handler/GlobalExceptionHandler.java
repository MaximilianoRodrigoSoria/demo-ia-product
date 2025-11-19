package com.ar.laboratory.demoiaproduct.handler;

import com.ar.laboratory.demoiaproduct.dto.ApiErrorResponse;
import com.ar.laboratory.demoiaproduct.exception.BusinessRuleViolationException;
import com.ar.laboratory.demoiaproduct.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

/**
 * Manejo centralizado de excepciones para normalizar respuestas y logging.
 * Implementa las mejores prácticas de Spring Boot 3.x con ErrorResponse (RFC 7807).
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onValidation(MethodArgumentNotValidException ex,
                                                      HttpServletRequest req) {

        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiErrorResponse.FieldError(
                        f.getField(),
                        Objects.nonNull(f.getCode()) ? f.getCode() : "INVALID",
                        Objects.nonNull(f.getDefaultMessage()) ? f.getDefaultMessage() : "Validation failed"
                ))
                .toList();

        var correlationId = MDC.get("correlationId");
        var error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Input validation failed",
                req.getRequestURI(),
                correlationId,
                details
        );

        log.warn("Validation error on path {}: {}", req.getRequestURI(), details, ex);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja recursos no encontrados.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> onNotFound(ResourceNotFoundException ex,
                                                    HttpServletRequest req) {

        var correlationId = MDC.get("correlationId");
        var error = new ApiErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getErrorCode(),
                ex.getMessage(),
                req.getRequestURI(),
                correlationId,
                List.of()
        );

        log.info("Resource not found on path {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja violaciones de reglas de negocio.
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> onBusiness(BusinessRuleViolationException ex,
                                                    HttpServletRequest req) {

        var correlationId = MDC.get("correlationId");
        var error = new ApiErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getErrorCode(),
                ex.getMessage(),
                req.getRequestURI(),
                correlationId,
                List.of()
        );

        log.warn("Business rule violation on path {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Manejo catch-all para errores inesperados.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onUnexpected(Exception ex,
                                                      HttpServletRequest req) {

        var correlationId = MDC.get("correlationId");
        var error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Unexpected error occurred",
                req.getRequestURI(),
                correlationId,
                List.of()
        );

        log.error("Unexpected error on path {}: {}", req.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
