package com.originacion.contratos.contratos.mapper;

import com.originacion.contratos.contratos.dto.ContratoDTO;
import com.originacion.contratos.contratos.enums.EstadoContrato;
import com.originacion.contratos.contratos.model.Contrato;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContratoMapper {

    public ContratoDTO toDTO(Contrato contrato) {
        if (contrato == null) {
            return null;
        }

        ContratoDTO dto = new ContratoDTO();
        dto.setIdContrato(contrato.getId());
        dto.setIdSolicitud(contrato.getIdSolicitud());
        dto.setRutaArchivo(contrato.getRutaArchivo());
        dto.setFechaGenerado(contrato.getFechaGenerado());
        dto.setFechaFirma(contrato.getFechaFirma());
        dto.setEstado(contrato.getEstado() != null ? contrato.getEstado().name() : null);
        dto.setCondicionEspecial(contrato.getCondicionEspecial());
        dto.setVersion(contrato.getVersion());

        return dto;
    }

    public Contrato toModel(ContratoDTO dto) {
        if (dto == null) {
            return null;
        }

        Contrato contrato = new Contrato();
        contrato.setId(dto.getIdContrato());
        contrato.setIdSolicitud(dto.getIdSolicitud());
        contrato.setRutaArchivo(dto.getRutaArchivo());
        contrato.setFechaGenerado(dto.getFechaGenerado());
        contrato.setFechaFirma(dto.getFechaFirma());
        contrato.setEstado(dto.getEstado() != null ? EstadoContrato.valueOf(dto.getEstado()) : null);
        contrato.setCondicionEspecial(dto.getCondicionEspecial());
        contrato.setVersion(dto.getVersion());

        return contrato;
    }

    public List<ContratoDTO> toDTOList(List<Contrato> contratos) {
        if (contratos == null) {
            return null;
        }

        List<ContratoDTO> dtos = new ArrayList<>(contratos.size());
        for (Contrato contrato : contratos) {
            dtos.add(toDTO(contrato));
        }
        return dtos;
    }

    public List<Contrato> toModelList(List<ContratoDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        List<Contrato> contratos = new ArrayList<>(dtos.size());
        for (ContratoDTO dto : dtos) {
            contratos.add(toModel(dto));
        }
        return contratos;
    }
} 