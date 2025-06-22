package com.originacion.contratos.pagares.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import com.originacion.contratos.model.SolicitudCredito;

@Entity
@Table(name = "pagares")
public class Pagare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPagare", nullable = false)
    private Long id;

    @Column(name = "IdSolicitud", nullable = false)
    private Long idSolicitud;

    @Column(name = "NumeroCuota", nullable = false, precision = 3)
    private Integer numeroCuota;

    @Column(name = "RutaArchivo", nullable = false, length = 150)
    private String rutaArchivo;

    @Column(name = "FechaGenerado", nullable = false)
    private LocalDateTime fechaGenerado;

    @Column(name="activo", nullable=false)
    private Boolean activo = true;

    @Version
    @Column(name = "Version", nullable = false)
    private Long version;

    @ManyToOne
    @JoinColumn(name = "IdSolicitud", referencedColumnName = "IdSolicitud", insertable = false, updatable = false)
    private SolicitudCredito solicitudCredito;

    public Pagare() {
    }

    public Pagare(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public LocalDateTime getFechaGenerado() {
        return fechaGenerado;
    }

    public void setFechaGenerado(LocalDateTime fechaGenerado) {
        this.fechaGenerado = fechaGenerado;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public SolicitudCredito getSolicitudCredito() {
        return solicitudCredito;
    }

    public void setSolicitudCredito(SolicitudCredito solicitudCredito) {
        this.solicitudCredito = solicitudCredito;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pagare other = (Pagare) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Pagare [id=" + id + ", idSolicitud=" + idSolicitud + ", numeroCuota=" + numeroCuota
                + ", rutaArchivo=" + rutaArchivo + ", fechaGenerado=" + fechaGenerado + ", version=" + version
                + ", solicitudCredito=" + solicitudCredito + "]";
    }

} 