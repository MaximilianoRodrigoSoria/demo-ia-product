package com.ar.laboratory.demoiaproduct.exception;

/**
 * Se lanza cuando una colección o recurso solicitado no existe en el sistema.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String errorCode;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
