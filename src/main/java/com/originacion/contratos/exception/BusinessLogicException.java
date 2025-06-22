package com.originacion.contratos.exception;

public class BusinessLogicException extends RuntimeException {

    private final String operation;
    private final String reason;

    public BusinessLogicException(String operation, String reason) {
        super();
        this.operation = operation;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "Error en la operación: " + this.operation + ". Razón: " + this.reason;
    }

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }
} 