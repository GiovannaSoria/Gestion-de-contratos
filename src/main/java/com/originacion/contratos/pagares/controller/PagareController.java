package com.originacion.contratos.pagares.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.originacion.contratos.pagares.dto.PagareCreateDto;
import com.originacion.contratos.pagares.dto.PagareDto;
import com.originacion.contratos.pagares.dto.PagareUpdateDto;
import com.originacion.contratos.pagares.service.PagareService;

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

        PagareDto dto = service.getPagareById(id);
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

        PagareDto updated = service.updatePagare(id, updateDto);
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

        service.logicalDeletePagare(id);
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

        List<PagareDto> dtos = service.generarPagaresDesdeCuotasFallback(idSolicitud, monto, tasa, plazo);
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

        return ResponseEntity.ok(service.existenPagaresPorSolicitud(idSolicitud));
    }

    @Operation(summary = "Elimina todos los pagarés de una solicitud (cancelación)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pagarés eliminados")
    })
    @DeleteMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<Void> deleteBySolicitud(
        @Parameter(description = "ID de la solicitud", required = true)
        @PathVariable Long idSolicitud) {

        service.eliminarPagaresPorSolicitud(idSolicitud);
        return ResponseEntity.noContent().build();
    }
}
