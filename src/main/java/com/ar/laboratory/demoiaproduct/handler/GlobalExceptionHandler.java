package com.ar.laboratory.demoiaproduct.handler;

import com.ar.laboratory.demoiaproduct.dto.ErrorResponse;
import com.ar.laboratory.demoiaproduct.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo centralizado de excepciones para normalizar respuestas y logging.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Maneja colecciones vacías o recursos inexistentes. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        var traceId = MDC.get("traceId");
        log.warn("NotFound: {} traceId={}", ex.getMessage(), traceId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.notFound(ex.getMessage(), traceId));
    }

    /** Manejo catch-all para errores inesperados. */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Throwable ex) {
        var traceId = MDC.get("traceId");
        log.error("Unexpected error traceId={}", traceId, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.internal("Se produjo un error inesperado", traceId));
    }
}

