package com.originacion.contratos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.originacion.contratos.model.Pagare;

import java.util.List;
import java.util.Optional;

public interface PagareRepository extends JpaRepository<Pagare, Long> {

    List<Pagare> findByIdSolicitud(Long idSolicitud);
    
    List<Pagare> findByIdSolicitudOrderByNumeroCuota(Long idSolicitud);

    Optional<Pagare> findByIdSolicitudAndNumeroCuota(Long idSolicitud, Integer numeroCuota);
            
    boolean existsByIdSolicitud(Long idSolicitud);
        
    void deleteByIdSolicitud(Long idSolicitud);
} 