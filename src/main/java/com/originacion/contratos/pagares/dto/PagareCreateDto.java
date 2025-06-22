package com.originacion.contratos.pagares.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagareCreateDto {

    @NotNull(message = "El ID de solicitud no puede ser nulo")
    private Long idSolicitud;

    @NotNull(message = "El número de cuota no puede ser nulo")
    @Min(value = 1, message = "El número de cuota debe ser al menos 1")
    private Integer numeroCuota;

    @NotBlank(message = "La ruta del archivo no puede estar vacía")
    private String rutaArchivo;
}
