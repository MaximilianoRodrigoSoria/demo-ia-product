package com.ar.laboratory.demoiaproduct.exception;

/**
 * Se lanza cuando una colección o recurso solicitado no existe en el sistema.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String message, Throwable cause) { super(message, cause); }
}

