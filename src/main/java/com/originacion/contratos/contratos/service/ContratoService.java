package com.originacion.contratos.contratos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.originacion.contratos.contratos.exception.ContratoNotFoundException;
import com.originacion.contratos.contratos.model.Contrato;
import com.originacion.contratos.contratos.repository.ContratoRepository;
import com.originacion.contratos.model.SolicitudCredito;
import com.originacion.contratos.pagares.service.PagareService;
import com.originacion.contratos.pagares.exception.PagareGenerationException;

@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final PagareService pagareService;

    public ContratoService(ContratoRepository contratoRepository, PagareService pagareService) {
        this.contratoRepository = contratoRepository;
        this.pagareService = pagareService;
    }

    public Contrato findById(Long id) {
        return this.contratoRepository.findById(id)
                .orElseThrow(() -> new ContratoNotFoundException("Contrato con ID: " + id + " no encontrado"));
    }

    public List<Contrato> findAll() {
        return this.contratoRepository.findAll();
    }

    public List<Contrato> findBySolicitudId(Long idSolicitud) {
        return this.contratoRepository.findByIdSolicitud(idSolicitud);
    }

    public List<Contrato> findByEstado(String estado) {
        return this.contratoRepository.findByEstado(estado);
    }

    /**
     * Genera automáticamente un contrato cuando una solicitud es aprobada
     * Regla de negocio: Solo se puede generar un contrato para solicitudes APROBADAS
     */
    @Transactional
    public Contrato generarContratoAutomatico(SolicitudCredito solicitudAprobada) {
        // Validar que la solicitud esté aprobada
        if (!"Aprobada".equals(solicitudAprobada.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden generar contratos para solicitudes aprobadas. Estado actual: " + solicitudAprobada.getEstado());
        }

        // Validar que no exista ya un contrato para esta solicitud
        if (this.contratoRepository.existsByIdSolicitud(solicitudAprobada.getId())) {
            throw new IllegalArgumentException("Ya existe un contrato para la solicitud ID: " + solicitudAprobada.getId());
        }

        // Crear el contrato
        Contrato contrato = new Contrato();
        contrato.setIdSolicitud(solicitudAprobada.getId());
        contrato.setRutaArchivo(generarRutaArchivo(solicitudAprobada));
        contrato.setFechaGenerado(LocalDateTime.now());
        contrato.setFechaFirma(LocalDateTime.now());
        contrato.setEstado("Generado");
        contrato.setCondicionEspecial(generarCondicionesEspeciales(solicitudAprobada));

        // Guardar el contrato
        Contrato contratoGuardado = this.contratoRepository.save(contrato);

        // Generar automáticamente los pagarés
        //this.pagareService.generarPagaresAutomaticos(contratoGuardado, solicitudAprobada);

        return contratoGuardado;
    }

    /**
     * Registra la firma del contrato
     * Regla de negocio: Solo se pueden firmar contratos en estado "Generado"
     */
    @Transactional
    public Contrato firmarContrato(Long contratoId) {
        Contrato contrato = findById(contratoId);

        if (!"Generado".equals(contrato.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden firmar contratos en estado 'Generado'. Estado actual: " + contrato.getEstado());
        }

        contrato.setFechaFirma(LocalDateTime.now());
        contrato.setEstado("Firmado");

        return this.contratoRepository.save(contrato);
    }

    /**
     * Anula un contrato
     * Regla de negocio: Solo se pueden anular contratos que no han sido firmados
     */
    @Transactional
    public Contrato anularContrato(Long contratoId, String motivo) {
        Contrato contrato = findById(contratoId);

        if ("Firmado".equals(contrato.getEstado())) {
            throw new IllegalArgumentException("No se pueden anular contratos que ya han sido firmados");
        }

        contrato.setEstado("Anulado");
        contrato.setCondicionEspecial("ANULADO - Motivo: " + motivo);

        return this.contratoRepository.save(contrato);
    }

    /**
     * Valida si todos los documentos requeridos están adjuntos
     */
    public boolean validarDocumentosCompletos(Long idSolicitud) {
        // Esta lógica se implementaría con DocumentoAdjuntoService
        // Por ahora devolvemos true para permitir el flujo
        return true;
    }

    private String generarRutaArchivo(SolicitudCredito solicitud) {
        // Generar ruta única basada en ID y timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "/contratos/" + solicitud.getId() + "/contrato_" + timestamp + ".pdf";
    }

    private String generarCondicionesEspeciales(SolicitudCredito solicitud) {
        StringBuilder condiciones = new StringBuilder();
        
        // Agregar condiciones basadas en el monto y plazo
        if (solicitud.getMontoSolicitado().doubleValue() > 50000) {
            condiciones.append("Préstamo de alto monto - Requiere garantías adicionales. ");
        }
        
        if (solicitud.getPlazoMeses() > 60) {
            condiciones.append("Plazo extendido - Aplican condiciones especiales de seguimiento. ");
        }

        // Condiciones basadas en score crediticio
        if (solicitud.getScoreInterno() != null && solicitud.getScoreInterno().doubleValue() < 650) {
            condiciones.append("Score crediticio medio - Monitoreo trimestral requerido. ");
        }

        return condiciones.toString().trim();
    }

    /**
     * Consulta específica para módulo de originación
     */
    public Contrato findContratoActivoPorSolicitud(Long idSolicitud) {
        return this.contratoRepository.findByIdSolicitudAndEstado(idSolicitud, "Firmado")
                .orElse(null);
    }

    /**
     * Método para integración con sistema bancario central
     */
    @Transactional
    public void notificarContratoAlBanco(Long contratoId) {
        Contrato contrato = findById(contratoId);
        
        if (!"Firmado".equals(contrato.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden notificar al banco contratos firmados");
        }

        // Aquí se implementaría la integración con el sistema bancario central
        // Por ejemplo, enviar datos del contrato a un servicio REST del banco
        
        // Simular procesamiento
        try {
            Thread.sleep(100); // Simular latencia de red
            // En un entorno real, aquí habría una llamada HTTP al banco
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al notificar al banco", e);
        }
    }
} 