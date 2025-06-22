package com.originacion.contratos.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.originacion.contratos.dto.CuotaDto;
import com.originacion.contratos.dto.PagareDto;
import com.originacion.contratos.dto.PagareUpdateDto;
import com.originacion.contratos.exception.PagareGenerationException;
import com.originacion.contratos.mapper.PagareMapper;
import com.originacion.contratos.model.Pagare;
import com.originacion.contratos.repository.PagareRepository;

@Service
public class PagareService {

    private final PagareRepository pagareRepository;
    private final PagareMapper pagareMapper;

    public PagareService(PagareRepository pagareRepository,
                         PagareMapper pagareMapper) {
        this.pagareRepository = pagareRepository;
        this.pagareMapper    = pagareMapper;
    }

     //Obtiene un Pagaré por su ID.
    @Transactional
    public PagareDto getPagareById(Long id) {
        try {
            Pagare pagare = pagareRepository.findById(id)
                .orElseThrow(() -> new PagareGenerationException("Pagaré no encontrado: " + id));
            return pagareMapper.toDto(pagare);
        } catch (PagareGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PagareGenerationException("Error al obtener el Pagaré: " + id);
        }
    }

    //Crea un nuevo Pagaré, “quemando” el FK en el mapper.
    // @Transactional
    // public PagareDto createPagare(PagareCreateDto dto) {
    //     // 1) Mapear CreateDto → entidad (MapStruct inyecta idSolicitud y ignora los campos que definimos)
    //     Pagare pagare = pagareMapper.toEntity(dto);
    //     // 2) Asignar fecha de generación si no lo hace la DB
    //     pagare.setFechaGenerado(LocalDateTime.now());
    //     // 3) Persistir
    //     Pagare saved = pagareRepository.save(pagare);
    //     // 4) Devolver DTO de respuesta
    //     return pagareMapper.toDto(saved);
    // }

    //Obtiene todos los pagarés de una solicitud, ordenados por número de cuota.
    @Transactional
    public List<PagareDto> getPagaresBySolicitud(Long idSolicitud) {
        try {
            var pagares = pagareRepository.findByIdSolicitudOrderByNumeroCuota(idSolicitud);
            return pagareMapper.toDtoList(pagares);
        } catch (Exception e) {
            throw new PagareGenerationException("Error al obtener el cronograma de pagarés");
        }
    }

     //Obtiene un Pagaré concreto de una solicitud según su número de cuota.
    @Transactional
    public PagareDto getPagareBySolicitudAndCuota(Long idSolicitud, Integer numeroCuota) {
        try {
            return pagareRepository
                .findByIdSolicitudAndNumeroCuota(idSolicitud, numeroCuota)
                .map(pagareMapper::toDto)
                .orElseThrow(() -> 
                    new PagareGenerationException(
                        "No se encontró el pagaré para solicitud " 
                        + idSolicitud + " y cuota " + numeroCuota
                    )
                );
        } catch (PagareGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PagareGenerationException("Error al obtener el pagaré específico");
        }
    }

    //Actualiza un Pagaré existente por el ID
    @Transactional
    public PagareDto updatePagare(Long id, PagareUpdateDto dto) {
        try {
            if (!id.equals(dto.getId())) {
                throw new PagareGenerationException("El ID del path no coincide con el del body");
            }

            Pagare existing = pagareRepository.findById(id)
                .orElseThrow(() -> new PagareGenerationException("Pagaré no encontrado: " + id));

            pagareMapper.updateEntity(existing, dto);
            Pagare updated = pagareRepository.save(existing);
            return pagareMapper.toDto(updated);
        } catch (PagareGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PagareGenerationException("Error al actualizar el pagaré: " + id);
        }
    }

    //Eliminación lógica: marca activo = false y retorna el DTO actualizado.
     @Transactional
    public PagareDto logicalDeletePagare(Long id) {
        try {
            Pagare existing = pagareRepository.findById(id)
                .orElseThrow(() -> new PagareGenerationException("Pagaré no encontrado: " + id));

            if (!Boolean.TRUE.equals(existing.getActivo())) {
                throw new PagareGenerationException("El pagaré ya está inactivo: " + id);
            }

            existing.setActivo(false);
            Pagare saved = pagareRepository.save(existing);
            return pagareMapper.toDto(saved);
        } catch (PagareGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PagareGenerationException("Error al eliminar lógicamente el pagaré: " + id);
        }
    }

    /**
     * Stub temporal: genera pagarés a partir de cuotas calculadas localmente.
     * TODO: reemplazar por llamada a microservicio Originación cuando esté disponible.
     */
    @Transactional
    public List<PagareDto> generarPagaresDesdeCuotasFallback(
            Long idSolicitud,
            BigDecimal montoSolicitado,
            BigDecimal tasaAnual,
            Short plazoMeses) {
        try {
            if (pagareRepository.existsByIdSolicitud(idSolicitud)) {
                throw new PagareGenerationException("Ya existen pagarés para solicitud " + idSolicitud);
            }

            List<CuotaDto> tabla = generarTablaDesdeParams(montoSolicitado, tasaAnual, plazoMeses);

            List<Pagare> pagares = new ArrayList<>();
            for (CuotaDto cuota : tabla) {
                Pagare p = new Pagare();
                p.setIdSolicitud(idSolicitud);
                p.setNumeroCuota(cuota.getNumeroCuota());
                p.setRutaArchivo(generarRutaPagare(idSolicitud, cuota.getNumeroCuota()));
                p.setFechaGenerado(LocalDateTime.now());
                pagares.add(pagareRepository.save(p));
            }
            return pagareMapper.toDtoList(pagares);
        } catch (PagareGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new PagareGenerationException("Error al generar cronograma de pagarés");
        }
    }

    // === Helpers privados ===

    private List<CuotaDto> generarTablaDesdeParams(
            BigDecimal principal,
            BigDecimal tasaAnual,
            Short plazoMeses) {

        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagareGenerationException("El monto debe ser mayor a cero");
        }
        if (tasaAnual == null || tasaAnual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagareGenerationException("La tasa debe ser mayor a cero");
        }
        if (plazoMeses == null || plazoMeses <= 0) {
            throw new PagareGenerationException("El plazo debe ser mayor a cero");
        }

        BigDecimal tasaMensual = tasaAnual
            .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        BigDecimal factor = BigDecimal.ONE.add(tasaMensual).pow(plazoMeses);
        BigDecimal numerador = principal.multiply(tasaMensual).multiply(factor);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);
        BigDecimal cuotaMensual = numerador.divide(denominador, 2, RoundingMode.HALF_UP);

        List<CuotaDto> tabla = new ArrayList<>();
        BigDecimal saldo = principal;
        LocalDate hoy = LocalDate.now();
        for (int i = 1; i <= plazoMeses; i++) {
            BigDecimal interes = saldo.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = cuotaMensual.subtract(interes);
            saldo = saldo.subtract(capital);
            if (i == plazoMeses && saldo.compareTo(BigDecimal.ZERO) != 0) {
                capital = capital.add(saldo);
                cuotaMensual = interes.add(capital);
                saldo = BigDecimal.ZERO;
            }
            tabla.add(CuotaDto.builder()
                .numeroCuota(i)
                .monto(cuotaMensual)
                .interes(interes)
                .saldoPendiente(saldo)
                .fechaVencimiento(hoy.plusMonths(i))
                .build()
            );
        }
        return tabla;
    }

    private String generarRutaPagare(Long idSolicitud, int numeroCuota) {
        String ts = String.valueOf(System.currentTimeMillis());
        return "/pagares/" + idSolicitud + "/pagare_" + numeroCuota + "_" + ts + ".pdf";
    }

    public boolean existenPagaresPorSolicitud(Long idSolicitud) {
        try {
            return pagareRepository.existsByIdSolicitud(idSolicitud);
        } catch (Exception e) {
            throw new PagareGenerationException("Error al verificar existencia de pagarés");
        }
    }

    @Transactional
    public void eliminarPagaresPorSolicitud(Long idSolicitud) {
        try {
            pagareRepository.deleteByIdSolicitud(idSolicitud);
        } catch (Exception e) {
            throw new PagareGenerationException("Error al eliminar pagarés de la solicitud");
        }
    }
} 