package com.originacion.contratos.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class PagareDto{
    private Long id;
    private Long idSolicitud;
    private Integer numeroCuota;
    private String rutaArchivo;
    private LocalDateTime fechaGenerado;
    private Boolean activo = true;
    private Long version;

}