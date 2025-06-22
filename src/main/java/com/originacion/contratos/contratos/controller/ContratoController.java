package com.originacion.contratos.contratos.controller;

import com.originacion.contratos.contratos.dto.ContratoDTO;
import com.originacion.contratos.contratos.mapper.ContratoMapper;
import com.originacion.contratos.contratos.enums.EstadoContrato;
import com.originacion.contratos.contratos.exception.BusinessLogicException;
import com.originacion.contratos.contratos.exception.NotFoundException;
import com.originacion.contratos.contratos.model.Contrato;
import com.originacion.contratos.contratos.service.ContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contratos")
@Tag(name = "Contratos", description = "API para gestión de contratos de préstamos automotrices")
@Slf4j
public class ContratoController {

    private final ContratoService contratoService;
    private final ContratoMapper contratoMapper;

    public ContratoController(ContratoService contratoService, ContratoMapper contratoMapper) {
        this.contratoService = contratoService;
        this.contratoMapper = contratoMapper;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los contratos", description = "Obtiene una lista paginada de todos los contratos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de contratos obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class)))
    })
    public ResponseEntity<Page<ContratoDTO>> getAllContratos(
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "idContrato") String sortBy,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Filtro por estado") @RequestParam(required = false) String estado) {
        
        log.info("Solicitando contratos - Página: {}, Tamaño: {}, Orden: {} {}, Estado: {}", 
                page, size, sortBy, sortDir, estado);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Contrato> contratos;
        if (estado != null && !estado.isEmpty()) {
            try {
                EstadoContrato estadoEnum = EstadoContrato.valueOf(estado.toUpperCase());
                contratos = contratoService.findByEstado(estadoEnum, pageable);
            } catch (IllegalArgumentException e) {
                log.error("Estado inválido proporcionado: {}", estado);
                return ResponseEntity.badRequest().build();
            }
        } else {
            contratos = contratoService.findAll(pageable);
        }

        Page<ContratoDTO> dtos = contratos.map(contratoMapper::toDTO);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener contrato por ID", description = "Obtiene un contrato específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contrato encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    public ResponseEntity<ContratoDTO> getContratoById(
            @Parameter(description = "ID del contrato") @PathVariable Integer id) {
        
        log.info("Solicitando contrato por ID: {}", id);
        
        try {
            Contrato contrato = contratoService.findById(id);
            return ResponseEntity.ok(contratoMapper.toDTO(contrato));
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/solicitud/{idSolicitud}")
    @Operation(summary = "Obtener contrato por ID de solicitud", description = "Obtiene un contrato por el ID de solicitud asociada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contrato encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    public ResponseEntity<ContratoDTO> getContratoByIdSolicitud(
            @Parameter(description = "ID de la solicitud") @PathVariable Integer idSolicitud) {
        
        log.info("Solicitando contrato por ID de solicitud: {}", idSolicitud);
        
        try {
            Contrato contrato = contratoService.findByIdSolicitud(idSolicitud);
            return ResponseEntity.ok(contratoMapper.toDTO(contrato));
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado para solicitud: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Generar nuevo contrato", description = "Genera un nuevo contrato para una solicitud de crédito")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contrato generado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un contrato para esta solicitud")
    })
    public ResponseEntity<ContratoDTO> generarContrato(
            @Valid @RequestBody ContratoDTO contratoDTO,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Generando nuevo contrato para solicitud: {} - RequestId: {}", contratoDTO.getIdSolicitud(), requestId);
        
        try {
            Contrato contrato = contratoService.generarContrato(
                    contratoDTO.getIdSolicitud(),
                    contratoDTO.getFechaFirma(),
                    contratoDTO.getCondicionEspecial()
            );
            
            ContratoDTO responseDTO = contratoMapper.toDTO(contrato);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            
        } catch (BusinessLogicException e) {
            log.error("Error de negocio al generar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}/firmar")
    @Operation(summary = "Firmar contrato", description = "Cambia el estado del contrato a FIRMADO y genera pagarés automáticamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contrato firmado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado"),
        @ApiResponse(responseCode = "409", description = "El contrato no está en estado válido para firmar")
    })
    public ResponseEntity<ContratoDTO> firmarContrato(
            @Parameter(description = "ID del contrato") @PathVariable Integer id,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Firmando contrato ID: {} - RequestId: {}", id, requestId);
        
        try {
            Contrato contrato = contratoService.firmarContrato(id);
            return ResponseEntity.ok(contratoMapper.toDTO(contrato));
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BusinessLogicException e) {
            log.error("Error de negocio al firmar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar contrato", description = "Cambia el estado del contrato a CANCELADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contrato cancelado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado"),
        @ApiResponse(responseCode = "409", description = "El contrato ya está cancelado")
    })
    public ResponseEntity<ContratoDTO> cancelarContrato(
            @Parameter(description = "ID del contrato") @PathVariable Integer id,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Cancelando contrato ID: {} por motivo: {} - RequestId: {}", id, motivo, requestId);
        
        try {
            Contrato contrato = contratoService.cancelarContrato(id, motivo);
            return ResponseEntity.ok(contratoMapper.toDTO(contrato));
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BusinessLogicException e) {
            log.error("Error de negocio al cancelar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar contrato completo", description = "Actualiza un contrato completo por ID (solo en estado DRAFT)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contrato actualizado exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado"),
        @ApiResponse(responseCode = "409", description = "El contrato no está en estado válido para modificar o conflicto de datos")
    })
    public ResponseEntity<ContratoDTO> actualizarContrato(
            @Parameter(description = "ID del contrato a actualizar") @PathVariable Integer id,
            @Valid @RequestBody ContratoDTO contratoDTO,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Actualizando contrato ID: {} - RequestId: {}", id, requestId);
        
        // Validar que el ID del path coincida con el del body (si está presente)
        if (contratoDTO.getIdContrato() != null && !contratoDTO.getIdContrato().equals(id)) {
            log.error("ID del path ({}) no coincide con ID del body ({})", id, contratoDTO.getIdContrato());
            return ResponseEntity.badRequest().build();
        }
        
        try {
            EstadoContrato estado = null;
            if (contratoDTO.getEstado() != null) {
                estado = EstadoContrato.valueOf(contratoDTO.getEstado());
            }
            
            Contrato contrato = contratoService.actualizarContrato(
                    id,
                    contratoDTO.getIdSolicitud(),
                    contratoDTO.getCondicionEspecial(),
                    contratoDTO.getFechaFirma(),
                    estado
            );
            
            return ResponseEntity.ok(contratoMapper.toDTO(contrato));
            
        } catch (IllegalArgumentException e) {
            log.error("Estado inválido proporcionado: {}", contratoDTO.getEstado());
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BusinessLogicException e) {
            log.error("Error de negocio al actualizar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}/condicion")
    @Operation(summary = "Actualizar condición especial", description = "Actualiza la condición especial del contrato (solo en estado DRAFT)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Condición actualizada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContratoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado"),
        @ApiResponse(responseCode = "409", description = "El contrato no está en estado válido para modificar")
    })
    public ResponseEntity<ContratoDTO> actualizarCondicionEspecial(
            @Parameter(description = "ID del contrato") @PathVariable Integer id,
            @Parameter(description = "Nueva condición especial") @RequestParam String condicion,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Actualizando condición especial del contrato ID: {} - RequestId: {}", id, requestId);
        
        try {
            Contrato contrato = contratoService.actualizarCondicionEspecial(id, condicion);
            return ResponseEntity.ok(contratoMapper.toDTO(contrato));
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BusinessLogicException e) {
            log.error("Error de negocio al actualizar condición: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/estadisticas/estado")
    @Operation(summary = "Obtener estadísticas por estado", description = "Obtiene el conteo de contratos agrupados por estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    public ResponseEntity<Object> getEstadisticasPorEstado() {
        log.info("Solicitando estadísticas de contratos por estado");
        
        return ResponseEntity.ok(java.util.Map.of(
            "DRAFT", contratoService.contarPorEstado(EstadoContrato.DRAFT),
            "FIRMADO", contratoService.contarPorEstado(EstadoContrato.FIRMADO),
            "CANCELADO", contratoService.contarPorEstado(EstadoContrato.CANCELADO)
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar contrato lógicamente", description = "Elimina lógicamente un contrato por ID (cambia estado a CANCELADO)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contrato eliminado lógicamente (cancelado)"),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado"),
        @ApiResponse(responseCode = "409", description = "El contrato ya está cancelado")
    })
    public ResponseEntity<Void> eliminarContratoLogicamente(
            @Parameter(description = "ID del contrato a eliminar lógicamente") @PathVariable Integer id,
            @Parameter(description = "Motivo de eliminación") @RequestParam(defaultValue = "Eliminado por usuario") String motivo,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Eliminación lógica del contrato ID: {} por motivo: {} - RequestId: {}", id, motivo, requestId);
        
        try {
            contratoService.eliminarLogicamente(id, motivo);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BusinessLogicException e) {
            log.error("Error de negocio al eliminar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/solicitud/{idSolicitud}")
    @Operation(summary = "Eliminar contrato físicamente", description = "Elimina físicamente un contrato de la base de datos por ID de solicitud")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Contrato eliminado físicamente de la base de datos"),
        @ApiResponse(responseCode = "404", description = "Contrato no encontrado")
    })
    public ResponseEntity<Void> eliminarContratoFisicamente(
            @Parameter(description = "ID de la solicitud del contrato a eliminar físicamente") @PathVariable Integer idSolicitud,
            @Parameter(description = "ID de request para idempotencia") @RequestHeader(required = false) String requestId) {
        
        log.info("Eliminación física del contrato por ID de solicitud: {} - RequestId: {}", idSolicitud, requestId);
        
        try {
            contratoService.eliminarFisicamente(idSolicitud);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("Contrato no encontrado para solicitud: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFound(NotFoundException e) {
        log.error("Recurso no encontrado: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({BusinessLogicException.class})
    public ResponseEntity<Object> handleBusinessLogic(BusinessLogicException e) {
        log.error("Error de lógica de negocio: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(java.util.Map.of("error", e.getMessage()));
    }
} 