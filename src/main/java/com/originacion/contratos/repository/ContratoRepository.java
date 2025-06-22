package com.originacion.contratos.repository;

import com.originacion.contratos.enums.EstadoContrato;
import com.originacion.contratos.model.Contrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    Optional<Contrato> findByIdSolicitud(Integer idSolicitud);

    List<Contrato> findByEstado(EstadoContrato estado);

    Page<Contrato> findByEstado(EstadoContrato estado, Pageable pageable);

    List<Contrato> findByIdSolicitudIn(List<Integer> idSolicitudes);

    Boolean existsByIdSolicitud(Integer idSolicitud);

    Long countByEstado(EstadoContrato estado);
} 