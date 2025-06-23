package com.originacion.contratos.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.originacion.contratos.dto.PagareDto;
import com.originacion.contratos.dto.PagareUpdateDto;
import com.originacion.contratos.service.PagareService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/pagares", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Pagarés", description = "API para gestionar Pagarés")
@Validated
public class PagareController {

    private static final Logger log = LoggerFactory.getLogger(PagareController.class);
    private final PagareService service;

    public PagareController(PagareService service) {
        this.service = service;
    }

    @Operation(summary = "Obtiene un Pagaré por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagaré encontrado",
                     content = @Content(schema = @Schema(implementation = PagareDto.class))),
        @ApiResponse(responseCode = "404", description = "Pagaré no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagareDto> getById(
        @Parameter(description = "ID del pagaré", required = true)
        @PathVariable Long id) {

        log.debug("Solicitud recibida → Obtener Pagaré con ID={}", id);
        PagareDto dto = service.getPagareById(id);
        log.info("Pagaré con ID={} recuperado correctamente: númeroCuota={}, rutaArchivo='{}'",
                 id, dto.getNumeroCuota(), dto.getRutaArchivo());
        return ResponseEntity.ok(dto);
    }

    // @Operation(summary = "Crea un nuevo Pagaré")
    // @ApiResponses({
    //     @ApiResponse(responseCode = "201", description = "Pagaré creado",
    //                  content = @Content(schema = @Schema(implementation = PagareDto.class))),
    //     @ApiResponse(responseCode = "400", description = "Datos inválidos")
    // })
    // @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<PagareDto> create(
    //     @io.swagger.v3.oas.annotations.parameters.RequestBody(
    //         description = "Payload para crear el Pagaré",
    //         required = true,
    //         content = @Content(schema = @Schema(implementation = PagareCreateDto.class))
    //     )
    //     @Valid @RequestBody PagareCreateDto createDto) {

    //     PagareDto created = service.createPagare(createDto);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(created);
    // }

    @Operation(summary = "Lista todos los pagarés de una solicitud ordenados por cuota")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de pagarés",
                     content = @Content(schema = @Schema(implementation = PagareDto.class))),
        @ApiResponse(responseCode = "404", description = "No existen pagarés para esa solicitud")
    })
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<PagareDto>> getBySolicitud(
        @Parameter(description = "ID de la solicitud", required = true)
        @PathVariable Long idSolicitud) {

        log.debug("ENTER GET /api/pagares/solicitud/{} → listar pagarés", idSolicitud);
        List<PagareDto> dtos = service.getPagaresBySolicitud(idSolicitud);
        if (dtos.isEmpty()) {
            log.warn("No se encontraron pagarés para la solicitud {}", idSolicitud);
            return ResponseEntity.notFound().build();
        }
        log.info("{} pagarés listados para solicitud {}", dtos.size(), idSolicitud);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtiene un Pagaré de una solicitud por número de cuota")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagaré encontrado",
                     content = @Content(schema = @Schema(implementation = PagareDto.class))),
        @ApiResponse(responseCode = "404", description = "Pagaré no encontrado")
    })
    @GetMapping("/solicitud/{idSolicitud}/cuota/{numeroCuota}")
    public ResponseEntity<PagareDto> getBySolicitudAndCuota(
        @Parameter(description = "ID de la solicitud", required = true)
        @PathVariable Long idSolicitud,
        @Parameter(description = "Número de cuota", required = true)
        @PathVariable Integer numeroCuota
    ) {
        log.debug("ENTER GET /api/pagares/solicitud/{}/cuota/{} → obtener pagaré", idSolicitud, numeroCuota);
        PagareDto dto = service.getPagareBySolicitudAndCuota(idSolicitud, numeroCuota);
        log.info("Pagaré encontrado para solicitud {} cuota {}: ruta='{}'",
                 idSolicitud, numeroCuota, dto.getRutaArchivo());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Actualiza un Pagaré existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagaré actualizado",
                     content = @Content(schema = @Schema(implementation = PagareDto.class))),
        @ApiResponse(responseCode = "400", description = "ID path/body no coinciden o datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Pagaré no encontrado")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagareDto> update(
        @Parameter(description = "ID del pagaré a actualizar", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload para actualizar el Pagaré",
            required = true,
            content = @Content(schema = @Schema(implementation = PagareUpdateDto.class))
        )
        @Valid @RequestBody PagareUpdateDto updateDto) {

        log.debug("Solicitud recibida → Actualizar Pagaré ID={} con nueva rutaArchivo='{}'",
                  id, updateDto.getRutaArchivo());
        PagareDto updated = service.updatePagare(id, updateDto);
        log.info("Pagaré ID={} actualizado correctamente: ahora númeroCuota={}, rutaArchivo='{}'",
                 id, updated.getNumeroCuota(), updated.getRutaArchivo());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Elimina lógicamente un Pagaré")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pagaré marcado como inactivo"),
        @ApiResponse(responseCode = "404", description = "Pagaré no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID del pagaré a eliminar", required = true)
        @PathVariable Long id) {

        log.debug("Solicitud recibida → Eliminación lógica de Pagaré ID={}", id);
        service.logicalDeletePagare(id);
        log.warn("Pagaré ID={} marcado como inactivo (activo=false)", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Genera pagarés desde cuotas (stub temporal)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagarés generados",
                     content = @Content(schema = @Schema(implementation = PagareDto.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida o ya existen pagarés")
    })
    @PostMapping(path = "/automaticos/fallback")
    public ResponseEntity<List<PagareDto>> generateFromCuotasFallback(
        @Parameter(description = "ID de la solicitud", required = true) @RequestParam Long idSolicitud,
        @Parameter(description = "Monto solicitado",  required = true) @RequestParam BigDecimal monto,
        @Parameter(description = "Tasa anual (%)",    required = true) @RequestParam BigDecimal tasa,
        @Parameter(description = "Plazo en meses",     required = true) @RequestParam Short plazo) {

        log.debug("Solicitud recibida → Generar {} Pagarés de fallback para solicitud {} (monto={}, tasa={}, plazo={})",
                  plazo, idSolicitud, monto, tasa, plazo);
        List<PagareDto> dtos = service.generarPagaresDesdeCuotasFallback(idSolicitud, monto, tasa, plazo);
        log.info("{} Pagarés generados para solicitud {}", dtos.size(), idSolicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtos);
    }

    @Operation(summary = "Verifica si existen pagarés para una solicitud")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Indicador de existencia")
    })
    @GetMapping("/existe/solicitud/{idSolicitud}")
    public ResponseEntity<Boolean> existsBySolicitud(
        @Parameter(description = "ID de la solicitud", required = true)
        @PathVariable Long idSolicitud) {
        
        log.debug("ENTER GET /existe/solicitud/{}", idSolicitud);
        boolean existen = service.existenPagaresPorSolicitud(idSolicitud);
        log.info("Existencia de pagarés para solicitud {}: {}", idSolicitud, existen);
        return ResponseEntity.ok(existen);
    }

    @Operation(summary = "Elimina todos los pagarés de una solicitud (cancelación)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pagarés eliminados")
    })
    @DeleteMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<Void> deleteBySolicitud(
        @Parameter(description = "ID de la solicitud", required = true)
        @PathVariable Long idSolicitud) {

        log.debug("ENTER DELETE /solicitud/{} → eliminación física", idSolicitud);
        service.eliminarPagaresPorSolicitud(idSolicitud);
        log.warn("Pagarés eliminados físicamente para solicitud {}", idSolicitud);
        return ResponseEntity.noContent().build();
    }
}
