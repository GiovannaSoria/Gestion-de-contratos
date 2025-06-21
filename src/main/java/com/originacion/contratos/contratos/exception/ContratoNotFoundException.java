package com.originacion.contratos.contratos.exception;

public class ContratoNotFoundException extends RuntimeException {

    private final Integer errorCode;

    public ContratoNotFoundException(String message) {
        super(message);
        this.errorCode = 1001;
    }

    @Override
    public String getMessage() {
        return "Error code: " + this.errorCode + ", message: " + super.getMessage();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
} 