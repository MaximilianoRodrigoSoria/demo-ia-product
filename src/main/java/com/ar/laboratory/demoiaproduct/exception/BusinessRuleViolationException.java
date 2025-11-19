package com.ar.laboratory.demoiaproduct.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 */
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
