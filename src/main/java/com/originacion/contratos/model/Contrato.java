package com.originacion.contratos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.originacion.contratos.enums.EstadoContrato;

@Entity
@Table(name = "contratos", schema = "gestion_contratos")
@Getter
@Setter
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontrato", nullable = false)
    private Integer id;

    @Column(name = "idsolicitud", nullable = false, unique = true)
    private Integer idSolicitud;

    @Column(name = "rutaarchivo", nullable = false, length = 150)
    private String rutaArchivo;

    @Column(name = "fechagenerado", nullable = false)
    private LocalDateTime fechaGenerado;

    @Column(name = "fechafirma", nullable = true)
    private LocalDateTime fechaFirma;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoContrato estado;

    @Column(name = "condicionespecial", length = 120)
    private String condicionEspecial;

    @Column(name = "version", nullable = false)
    private Long version;

    public Contrato() {
    }

    public Contrato(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contrato contrato)) return false;
        return id != null && id.equals(contrato.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Contrato{" +
                "id=" + id +
                ", idSolicitud=" + idSolicitud +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                ", fechaGenerado=" + fechaGenerado +
                ", fechaFirma=" + fechaFirma +
                ", estado=" + estado +
                ", condicionEspecial='" + condicionEspecial + '\'' +
                ", version=" + version +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        if (fechaGenerado == null) {
            fechaGenerado = LocalDateTime.now();
        }
        if (version == null) {
            version = 1L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (version != null) {
            version = version + 1L;
        }
    }
} 