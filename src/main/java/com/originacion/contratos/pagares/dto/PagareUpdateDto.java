package com.originacion.contratos.pagares.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagareUpdateDto {

    @NotNull(message = "El ID del pagaré no puede ser nulo")
    private Long id;

    @NotNull(message = "El número de cuota no puede ser nulo")
    @Min(value = 1, message = "El número de cuota debe ser al menos 1")
    private Integer numeroCuota;

    @NotBlank(message = "La ruta del archivo no puede estar vacía")
    @Size(max = 200, message = "La ruta del archivo no puede exceder los 200 caracteres")
    private String rutaArchivo;
}
