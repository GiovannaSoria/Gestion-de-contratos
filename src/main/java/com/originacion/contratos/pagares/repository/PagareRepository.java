package com.originacion.contratos.pagares.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.originacion.contratos.pagares.model.Pagare;

public interface PagareRepository extends JpaRepository<Pagare, Long> {

    List<Pagare> findByIdSolicitud(Long idSolicitud);
    
    List<Pagare> findByIdSolicitudOrderByNumeroCuota(Long idSolicitud);

    Optional<Pagare> findByIdSolicitudAndNumeroCuota(Long idSolicitud, Integer numeroCuota);
            
    boolean existsByIdSolicitud(Long idSolicitud);
        
    void deleteByIdSolicitud(Long idSolicitud);
} 