package com.originacion.contratos.contratos.service;

import com.originacion.contratos.contratos.enums.EstadoContrato;
import com.originacion.contratos.contratos.exception.BusinessLogicException;
import com.originacion.contratos.contratos.exception.NotFoundException;
import com.originacion.contratos.contratos.model.Contrato;
import com.originacion.contratos.contratos.repository.ContratoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ContratoService {

    private final ContratoRepository contratoRepository;

    public ContratoService(ContratoRepository contratoRepository) {
        this.contratoRepository = contratoRepository;
    }

    @Transactional(readOnly = true)
    public Page<Contrato> findAll(Pageable pageable) {
        log.debug("Buscando todos los contratos con paginación: {}", pageable);
        return contratoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Contrato> findByEstado(EstadoContrato estado, Pageable pageable) {
        log.debug("Buscando contratos por estado: {} con paginación: {}", estado, pageable);
        return contratoRepository.findByEstado(estado, pageable);
    }

    @Transactional(readOnly = true)
    public Contrato findById(Integer id) {
        log.debug("Buscando contrato por ID: {}", id);
        return contratoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), "Contrato"));
    }

    @Transactional(readOnly = true)
    public Contrato findByIdSolicitud(Integer idSolicitud) {
        log.debug("Buscando contrato por ID de solicitud: {}", idSolicitud);
        return contratoRepository.findByIdSolicitud(idSolicitud)
                .orElseThrow(() -> new NotFoundException(idSolicitud.toString(), "Contrato por solicitud"));
    }

    public Contrato generarContrato(Integer idSolicitud, LocalDateTime fechaFirma, String condicionEspecial) {
        log.info("Generando contrato para solicitud: {}", idSolicitud);
        
        // Verificar si ya existe un contrato para esta solicitud
        if (contratoRepository.existsByIdSolicitud(idSolicitud)) {
            throw new BusinessLogicException("CREAR_CONTRATO", "Ya existe un contrato para la solicitud: " + idSolicitud);
        }

        Contrato contrato = new Contrato();
        contrato.setIdSolicitud(idSolicitud);
        contrato.setRutaArchivo("/contratos/generados/contrato_" + idSolicitud + ".pdf");
        contrato.setFechaGenerado(LocalDateTime.now());
        // fechaFirma se queda como null hasta que el contrato sea firmado
        contrato.setEstado(EstadoContrato.DRAFT);
        contrato.setCondicionEspecial(condicionEspecial);
        contrato.setVersion(1L);

        return contratoRepository.save(contrato);
    }

    public Contrato firmarContrato(Integer id) {
        log.info("Firmando contrato ID: {}", id);
        
        Contrato contrato = findById(id);
        
        if (contrato.getEstado() != EstadoContrato.DRAFT) {
            throw new BusinessLogicException("FIRMAR_CONTRATO", 
                "El contrato debe estar en estado DRAFT para ser firmado. Estado actual: " + contrato.getEstado());
        }

        contrato.setEstado(EstadoContrato.FIRMADO);
        contrato.setFechaFirma(LocalDateTime.now());
        
        return contratoRepository.save(contrato);
    }

    public Contrato cancelarContrato(Integer id, String motivo) {
        log.info("Cancelando contrato ID: {} por motivo: {}", id, motivo);
        
        Contrato contrato = findById(id);
        
        if (contrato.getEstado() == EstadoContrato.CANCELADO) {
            throw new BusinessLogicException("CANCELAR_CONTRATO", "El contrato ya está cancelado");
        }

        contrato.setEstado(EstadoContrato.CANCELADO);
        contrato.setCondicionEspecial("CANCELADO: " + motivo);
        
        return contratoRepository.save(contrato);
    }

    public Contrato actualizarCondicionEspecial(Integer id, String condicion) {
        log.info("Actualizando condición especial del contrato ID: {}", id);
        
        Contrato contrato = findById(id);
        
        if (contrato.getEstado() != EstadoContrato.DRAFT) {
            throw new BusinessLogicException("ACTUALIZAR_CONDICION", 
                "Solo se pueden modificar contratos en estado DRAFT. Estado actual: " + contrato.getEstado());
        }

        contrato.setCondicionEspecial(condicion);
        
        return contratoRepository.save(contrato);
    }

    // PUT - Actualizar contrato completo
    public Contrato actualizarContrato(Integer id, Integer idSolicitud, String condicionEspecial, LocalDateTime fechaFirma, EstadoContrato estado) {
        log.info("Actualizando contrato completo ID: {}", id);
        
        Contrato contrato = findById(id);
        
        // Solo permitir actualización si está en DRAFT
        if (contrato.getEstado() != EstadoContrato.DRAFT) {
            throw new BusinessLogicException("ACTUALIZAR_CONTRATO", 
                "Solo se pueden actualizar contratos en estado DRAFT. Estado actual: " + contrato.getEstado());
        }

        // Validar que no se cambie a una solicitud que ya tenga contrato
        if (!contrato.getIdSolicitud().equals(idSolicitud)) {
            if (contratoRepository.existsByIdSolicitud(idSolicitud)) {
                throw new BusinessLogicException("ACTUALIZAR_CONTRATO", 
                    "Ya existe un contrato para la solicitud: " + idSolicitud);
            }
            contrato.setIdSolicitud(idSolicitud);
        }

        // Actualizar campos
        if (condicionEspecial != null) {
            contrato.setCondicionEspecial(condicionEspecial);
        }
        
        if (fechaFirma != null) {
            contrato.setFechaFirma(fechaFirma);
        }
        
        if (estado != null && estado != contrato.getEstado()) {
            // Solo permitir cambios de estado válidos
            if (estado == EstadoContrato.FIRMADO && contrato.getEstado() == EstadoContrato.DRAFT) {
                contrato.setEstado(estado);
                if (contrato.getFechaFirma() == null) {
                    contrato.setFechaFirma(LocalDateTime.now());
                }
            } else if (estado == EstadoContrato.CANCELADO) {
                contrato.setEstado(estado);
            } else {
                throw new BusinessLogicException("ACTUALIZAR_CONTRATO", 
                    "Cambio de estado no válido de " + contrato.getEstado() + " a " + estado);
            }
        }

        return contratoRepository.save(contrato);
    }

    @Transactional(readOnly = true)
    public Long contarPorEstado(EstadoContrato estado) {
        log.debug("Contando contratos por estado: {}", estado);
        return contratoRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticasPorEstado() {
        log.debug("Obteniendo estadísticas por estado");
        
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("DRAFT", contarPorEstado(EstadoContrato.DRAFT));
        estadisticas.put("FIRMADO", contarPorEstado(EstadoContrato.FIRMADO));
        estadisticas.put("CANCELADO", contarPorEstado(EstadoContrato.CANCELADO));
        
        return estadisticas;
    }

    // DELETE LÓGICO - Cancela el contrato por ID (cambia estado a CANCELADO)
    public void eliminarLogicamente(Integer id, String motivo) {
        log.info("Eliminación lógica del contrato ID: {} por motivo: {}", id, motivo);
        
        Contrato contrato = findById(id);
        
        if (contrato.getEstado() == EstadoContrato.CANCELADO) {
            throw new BusinessLogicException("ELIMINAR_CONTRATO", "El contrato ya está cancelado");
        }

        contrato.setEstado(EstadoContrato.CANCELADO);
        contrato.setCondicionEspecial("ELIMINADO: " + motivo);
        
        contratoRepository.save(contrato);
        log.info("Contrato ID: {} eliminado lógicamente", id);
    }

    // DELETE FÍSICO - Elimina físicamente el contrato por ID de solicitud
    public void eliminarFisicamente(Integer idSolicitud) {
        log.info("Eliminación física del contrato por ID de solicitud: {}", idSolicitud);
        
        Contrato contrato = contratoRepository.findByIdSolicitud(idSolicitud)
                .orElseThrow(() -> new NotFoundException(idSolicitud.toString(), "Contrato por solicitud"));
        
        contratoRepository.delete(contrato);
        log.info("Contrato de solicitud: {} eliminado físicamente de la base de datos", idSolicitud);
    }
} 