package com.originacion.contratos.contratos.enums;

public enum EstadoContrato {
    DRAFT("Draft"),
    FIRMADO("Firmado"),
    CANCELADO("Cancelado");

    private final String valor;

    EstadoContrato(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
} 