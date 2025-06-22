package com.originacion.contratos.exception;

public class PagareGenerationException extends RuntimeException {

    private final Integer errorCode;

    public PagareGenerationException(String message) {
        super(message);
        this.errorCode = 1003;
    }

    @Override
    public String getMessage() {
        return "Error code: " + this.errorCode + ", message: " + super.getMessage();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
} 