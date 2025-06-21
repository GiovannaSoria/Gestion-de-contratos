package com.originacion.contratos.contratos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.originacion.contratos.contratos.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByIdSolicitud(Long idSolicitud);
    
    List<Contrato> findByEstado(String estado);
    
    Optional<Contrato> findByIdSolicitudAndEstado(Long idSolicitud, String estado);
    
    List<Contrato> findByEstadoOrderByFechaGeneradoDesc(String estado);
    
    boolean existsByIdSolicitud(Long idSolicitud);
} 