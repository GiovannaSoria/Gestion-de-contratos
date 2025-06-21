package com.originacion.contratos.contratos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.originacion.contratos.contratos.model.Contrato;
import com.originacion.contratos.contratos.service.ContratoService;
import com.originacion.contratos.model.SolicitudCredito;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    /**
     * Obtiene todos los contratos
     */
    @GetMapping
    public ResponseEntity<List<Contrato>> findAll() {
        List<Contrato> contratos = this.contratoService.findAll();
        return ResponseEntity.ok(contratos);
    }

    /**
     * Obtiene un contrato por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contrato> findById(@PathVariable Long id) {
        Contrato contrato = this.contratoService.findById(id);
        return ResponseEntity.ok(contrato);
    }

    /**
     * Obtiene contratos por ID de solicitud
     */
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<Contrato>> findBySolicitudId(@PathVariable Long idSolicitud) {
        List<Contrato> contratos = this.contratoService.findBySolicitudId(idSolicitud);
        return ResponseEntity.ok(contratos);
    }

    /**
     * Obtiene contratos por estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Contrato>> findByEstado(@PathVariable String estado) {
        List<Contrato> contratos = this.contratoService.findByEstado(estado);
        return ResponseEntity.ok(contratos);
    }

    /**
     * Genera automáticamente un contrato para una solicitud aprobada
     */
    @PostMapping("/generar")
    public ResponseEntity<Contrato> generarContratoAutomatico(@RequestBody SolicitudCredito solicitudAprobada) {
        try {
            Contrato contrato = this.contratoService.generarContratoAutomatico(solicitudAprobada);
            return ResponseEntity.status(HttpStatus.CREATED).body(contrato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Firma un contrato
     */
    @PutMapping("/{id}/firmar")
    public ResponseEntity<Contrato> firmarContrato(@PathVariable Long id) {
        try {
            Contrato contrato = this.contratoService.firmarContrato(id);
            return ResponseEntity.ok(contrato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Anula un contrato
     */
    @PutMapping("/{id}/anular")
    public ResponseEntity<Contrato> anularContrato(@PathVariable Long id, 
                                                  @RequestParam String motivo) {
        try {
            Contrato contrato = this.contratoService.anularContrato(id, motivo);
            return ResponseEntity.ok(contrato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene el contrato activo de una solicitud
     */
    @GetMapping("/activo/solicitud/{idSolicitud}")
    public ResponseEntity<Contrato> findContratoActivoPorSolicitud(@PathVariable Long idSolicitud) {
        Contrato contrato = this.contratoService.findContratoActivoPorSolicitud(idSolicitud);
        if (contrato != null) {
            return ResponseEntity.ok(contrato);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Notifica el contrato al banco
     */
    @PostMapping("/{id}/notificar-banco")
    public ResponseEntity<Void> notificarContratoAlBanco(@PathVariable Long id) {
        try {
            this.contratoService.notificarContratoAlBanco(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Valida si todos los documentos requeridos están completos
     */
    @GetMapping("/validar-documentos/{idSolicitud}")
    public ResponseEntity<Boolean> validarDocumentosCompletos(@PathVariable Long idSolicitud) {
        boolean completos = this.contratoService.validarDocumentosCompletos(idSolicitud);
        return ResponseEntity.ok(completos);
    }
} 