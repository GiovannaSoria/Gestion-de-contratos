package com.originacion.contratos.exception;

public class DocumentoAdjuntoNotFoundException extends RuntimeException {

    private final Integer errorCode;

    public DocumentoAdjuntoNotFoundException(String message) {
        super(message);
        this.errorCode = 1002;
    }

    @Override
    public String getMessage() {
        return "Error code: " + this.errorCode + ", message: " + super.getMessage();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
} 