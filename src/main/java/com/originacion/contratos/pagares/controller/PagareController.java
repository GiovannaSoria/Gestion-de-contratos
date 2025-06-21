package com.originacion.contratos.pagares.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.originacion.contratos.contratos.model.Contrato;
import com.originacion.contratos.pagares.model.Pagare;
import com.originacion.contratos.pagares.service.PagareService;
import com.originacion.contratos.pagares.service.PagareService.CuotaAmortizacion;
import com.banquito.sistema.originacion.model.SolicitudCredito;

@RestController
@RequestMapping("/api/pagares")
public class PagareController {

    private final PagareService pagareService;

    public PagareController(PagareService pagareService) {
        this.pagareService = pagareService;
    }

    /**
     * Obtiene un pagaré por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pagare> findById(@PathVariable Long id) {
        Pagare pagare = this.pagareService.findById(id);
        return ResponseEntity.ok(pagare);
    }

    /**
     * Obtiene todos los pagarés de una solicitud
     */
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<Pagare>> findBySolicitudId(@PathVariable Long idSolicitud) {
        List<Pagare> pagares = this.pagareService.findBySolicitudId(idSolicitud);
        return ResponseEntity.ok(pagares);
    }

    /**
     * DTO para la generación de pagarés
     */
    public static class GenerarPagaresRequest {
        private Contrato contrato;
        private SolicitudCredito solicitud;

        // Getters y setters
        public Contrato getContrato() {
            return contrato;
        }

        public void setContrato(Contrato contrato) {
            this.contrato = contrato;
        }

        public SolicitudCredito getSolicitud() {
            return solicitud;
        }

        public void setSolicitud(SolicitudCredito solicitud) {
            this.solicitud = solicitud;
        }
    }

    /**
     * Genera automáticamente todos los pagarés para un contrato
     */
    @PostMapping("/generar")
    public ResponseEntity<List<Pagare>> generarPagaresAutomaticos(@RequestBody GenerarPagaresRequest request) {
        try {
            List<Pagare> pagares = this.pagareService.generarPagaresAutomaticos(
                request.getContrato(), 
                request.getSolicitud()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(pagares);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Genera la tabla de amortización para una solicitud
     */
    @PostMapping("/tabla-amortizacion")
    public ResponseEntity<List<CuotaAmortizacion>> generarTablaAmortizacion(@RequestBody SolicitudCredito solicitud) {
        try {
            List<CuotaAmortizacion> tabla = this.pagareService.generarTablaAmortizacion(solicitud);
            return ResponseEntity.ok(tabla);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Verifica si existen pagarés para una solicitud
     */
    @GetMapping("/existe/solicitud/{idSolicitud}")
    public ResponseEntity<Boolean> existenPagaresPorSolicitud(@PathVariable Long idSolicitud) {
        boolean existen = this.pagareService.existenPagaresPorSolicitud(idSolicitud);
        return ResponseEntity.ok(existen);
    }

    /**
     * Elimina pagarés de una solicitud (en caso de cancelación)
     */
    @DeleteMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<Void> eliminarPagaresPorSolicitud(@PathVariable Long idSolicitud) {
        try {
            this.pagareService.eliminarPagaresPorSolicitud(idSolicitud);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 