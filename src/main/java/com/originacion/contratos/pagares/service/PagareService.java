package com.originacion.contratos.pagares.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.originacion.contratos.pagares.exception.PagareGenerationException;
import com.originacion.contratos.contratos.model.Contrato;
import com.originacion.contratos.pagares.model.Pagare;
import com.originacion.contratos.pagares.repository.PagareRepository;
import com.banquito.sistema.originacion.model.SolicitudCredito;

@Service
public class PagareService {

    private final PagareRepository pagareRepository;

    public PagareService(PagareRepository pagareRepository) {
        this.pagareRepository = pagareRepository;
    }

    public Pagare findById(Long id) {
        return this.pagareRepository.findById(id)
                .orElseThrow(() -> new PagareGenerationException("Pagaré con ID: " + id + " no encontrado"));
    }

    public List<Pagare> findBySolicitudId(Long idSolicitud) {
        return this.pagareRepository.findByIdSolicitudOrderByNumeroCuota(idSolicitud);
    }

    /**
     * Genera automáticamente todos los pagarés para un contrato basado en amortización francesa
     * Regla de negocio: Un pagaré por cada cuota mensual del préstamo
     */
    @Transactional
    public List<Pagare> generarPagaresAutomaticos(Contrato contrato, SolicitudCredito solicitud) {
        // Validar que no existan pagarés para esta solicitud
        if (this.pagareRepository.existsByIdSolicitud(solicitud.getId())) {
            throw new PagareGenerationException("Ya existen pagarés para la solicitud ID: " + solicitud.getId());
        }

        List<Pagare> pagares = new ArrayList<>();
        
        // Obtener datos del préstamo
        BigDecimal montoSolicitado = solicitud.getMontoSolicitado();
        BigDecimal tasaAnual = solicitud.getTasaAnual();
        Short plazoMeses = solicitud.getPlazoMeses();
        
        // Calcular cuota mensual usando fórmula de amortización francesa
        BigDecimal cuotaMensual = calcularCuotaMensual(montoSolicitado, tasaAnual, plazoMeses);
        
        // Generar pagarés para cada cuota
        for (int numeroCuota = 1; numeroCuota <= plazoMeses; numeroCuota++) {
            Pagare pagare = new Pagare();
            pagare.setIdSolicitud(solicitud.getId());
            pagare.setNumeroCuota(numeroCuota);
            pagare.setRutaArchivo(generarRutaPagare(solicitud, numeroCuota));
            pagare.setFechaGenerado(LocalDateTime.now());
            
            Pagare pagareGuardado = this.pagareRepository.save(pagare);
            pagares.add(pagareGuardado);
        }

        return pagares;
    }

    /**
     * Calcula la cuota mensual usando la fórmula de amortización francesa
     * Fórmula: C = P * [r(1+r)^n] / [(1+r)^n - 1]
     * Donde: C = Cuota, P = Principal, r = Tasa mensual, n = Número de pagos
     */
    private BigDecimal calcularCuotaMensual(BigDecimal principal, BigDecimal tasaAnual, Short plazoMeses) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagareGenerationException("El monto del préstamo debe ser mayor a cero");
        }
        
        if (tasaAnual == null || tasaAnual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagareGenerationException("La tasa de interés debe ser mayor a cero");
        }
        
        if (plazoMeses == null || plazoMeses <= 0) {
            throw new PagareGenerationException("El plazo debe ser mayor a cero");
        }

        // Convertir tasa anual a mensual
        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                                          .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        // Calcular (1 + r)^n
        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal factor = unoPlusTasa.pow(plazoMeses.intValue());

        // Calcular numerador: P * r * (1+r)^n
        BigDecimal numerador = principal.multiply(tasaMensual).multiply(factor);

        // Calcular denominador: (1+r)^n - 1
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);

        // Calcular cuota mensual
        BigDecimal cuotaMensual = numerador.divide(denominador, 2, RoundingMode.HALF_UP);

        return cuotaMensual;
    }

    /**
     * Genera la tabla de amortización completa (útil para reportes)
     */
    public List<CuotaAmortizacion> generarTablaAmortizacion(SolicitudCredito solicitud) {
        List<CuotaAmortizacion> tabla = new ArrayList<>();
        
        BigDecimal montoSolicitado = solicitud.getMontoSolicitado();
        BigDecimal tasaAnual = solicitud.getTasaAnual();
        Short plazoMeses = solicitud.getPlazoMeses();
        
        BigDecimal cuotaMensual = calcularCuotaMensual(montoSolicitado, tasaAnual, plazoMeses);
        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
                                          .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        
        BigDecimal saldoPendiente = montoSolicitado;
        
        for (int numeroCuota = 1; numeroCuota <= plazoMeses; numeroCuota++) {
            // Calcular interés de la cuota
            BigDecimal interesCuota = saldoPendiente.multiply(tasaMensual)
                                                   .setScale(2, RoundingMode.HALF_UP);
            
            // Calcular capital de la cuota
            BigDecimal capitalCuota = cuotaMensual.subtract(interesCuota);
            
            // Actualizar saldo pendiente
            saldoPendiente = saldoPendiente.subtract(capitalCuota);
            
            // Asegurar que la última cuota liquide completamente el préstamo
            if (numeroCuota == plazoMeses && saldoPendiente.compareTo(BigDecimal.ZERO) != 0) {
                capitalCuota = capitalCuota.add(saldoPendiente);
                cuotaMensual = interesCuota.add(capitalCuota);
                saldoPendiente = BigDecimal.ZERO;
            }
            
            CuotaAmortizacion cuota = new CuotaAmortizacion(
                numeroCuota,
                cuotaMensual,
                capitalCuota,
                interesCuota,
                saldoPendiente
            );
            
            tabla.add(cuota);
        }
        
        return tabla;
    }

    private String generarRutaPagare(SolicitudCredito solicitud, int numeroCuota) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "/pagares/" + solicitud.getId() + "/pagare_" + numeroCuota + "_" + timestamp + ".pdf";
    }

    /**
     * Clase interna para representar una cuota de amortización
     */
    public static class CuotaAmortizacion {
        private final Integer numeroCuota;
        private final BigDecimal cuotaMensual;
        private final BigDecimal capital;
        private final BigDecimal interes;
        private final BigDecimal saldoPendiente;

        public CuotaAmortizacion(Integer numeroCuota, BigDecimal cuotaMensual, 
                                BigDecimal capital, BigDecimal interes, BigDecimal saldoPendiente) {
            this.numeroCuota = numeroCuota;
            this.cuotaMensual = cuotaMensual;
            this.capital = capital;
            this.interes = interes;
            this.saldoPendiente = saldoPendiente;
        }

        // Getters
        public Integer getNumeroCuota() { return numeroCuota; }
        public BigDecimal getCuotaMensual() { return cuotaMensual; }
        public BigDecimal getCapital() { return capital; }
        public BigDecimal getInteres() { return interes; }
        public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    }

    /**
     * Método para consumo desde módulo de originación
     */
    public boolean existenPagaresPorSolicitud(Long idSolicitud) {
        return this.pagareRepository.existsByIdSolicitud(idSolicitud);
    }

    /**
     * Elimina pagarés de una solicitud (en caso de cancelación)
     */
    @Transactional
    public void eliminarPagaresPorSolicitud(Long idSolicitud) {
        this.pagareRepository.deleteByIdSolicitud(idSolicitud);
    }
} 