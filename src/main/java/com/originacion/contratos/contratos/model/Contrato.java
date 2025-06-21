package com.originacion.contratos.contratos.model;

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
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdContrato", nullable = false)
    private Long id;

    @Column(name = "IdSolicitud", nullable = false)
    private Long idSolicitud;

    @Column(name = "RutaArchivo", nullable = false, length = 150)
    private String rutaArchivo;

    @Column(name = "FechaGenerado", nullable = false)
    private LocalDateTime fechaGenerado;

    @Column(name = "FechaFirma", nullable = false)
    private LocalDateTime fechaFirma;

    @Column(name = "Estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "CondicionEspecial", length = 120)
    private String condicionEspecial;

    @Version
    @Column(name = "Version", nullable = false)
    private Long version;

    @ManyToOne
    @JoinColumn(name = "IdSolicitud", referencedColumnName = "IdSolicitud", insertable = false, updatable = false)
    private SolicitudCredito solicitudCredito;

    public Contrato() {
    }

    public Contrato(Long id) {
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

    public LocalDateTime getFechaFirma() {
        return fechaFirma;
    }

    public void setFechaFirma(LocalDateTime fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCondicionEspecial() {
        return condicionEspecial;
    }

    public void setCondicionEspecial(String condicionEspecial) {
        this.condicionEspecial = condicionEspecial;
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
        Contrato other = (Contrato) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Contrato [id=" + id + ", idSolicitud=" + idSolicitud + ", rutaArchivo=" + rutaArchivo
                + ", fechaGenerado=" + fechaGenerado + ", fechaFirma=" + fechaFirma + ", estado=" + estado
                + ", condicionEspecial=" + condicionEspecial + ", version=" + version + ", solicitudCredito="
                + solicitudCredito + "]";
    }
} 