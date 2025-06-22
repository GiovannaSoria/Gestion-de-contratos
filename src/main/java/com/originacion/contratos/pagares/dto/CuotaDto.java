package com.originacion.contratos.pagares.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CuotaDto{
    private Integer numeroCuota;
    private BigDecimal monto;
    private BigDecimal interes;
    private BigDecimal saldoPendiente;
    private LocalDate fechaVencimiento;
}