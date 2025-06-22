package com.originacion.contratos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(description = "DTO para representar un contrato de préstamo automotriz")
public class ContratoDTO {

    @Schema(description = "Identificador único del contrato", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idContrato;

    @NotNull(message = "El ID de solicitud es requerido")
    @Min(value = 1, message = "El ID de solicitud debe ser mayor a 0")
    @Schema(description = "Identificador de la solicitud de crédito asociada", example = "1", required = true)
    private Integer idSolicitud;

    @Size(max = 150, message = "La ruta del archivo no puede exceder 150 caracteres")
    @Schema(description = "Ruta donde se almacena el archivo del contrato", example = "/contratos/2024/contrato_001.pdf", accessMode = Schema.AccessMode.READ_ONLY)
    private String rutaArchivo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Fecha y hora de generación del contrato", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaGenerado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Fecha y hora de firma del contrato", example = "2024-01-16T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaFirma;

    @Pattern(regexp = "DRAFT|FIRMADO|CANCELADO", message = "El estado debe ser DRAFT, FIRMADO o CANCELADO")
    @Schema(description = "Estado actual del contrato", example = "DRAFT", allowableValues = {"DRAFT", "FIRMADO", "CANCELADO"}, accessMode = Schema.AccessMode.READ_ONLY)
    private String estado;

    @Size(max = 120, message = "La condición especial no puede exceder 120 caracteres")
    @Schema(description = "Condiciones especiales del contrato", example = "Seguro todo riesgo incluido")
    private String condicionEspecial;

    @Schema(description = "Versión del registro para control de concurrencia", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long version;
} 