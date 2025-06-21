package com.banquito.sistema.originacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "clientesprospectos")
public class ClienteProspecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdClienteProspecto", nullable = false)
    private Long idClienteProspecto;

    @Column(name = "Cedula", length = 10, nullable = false, unique = true)
    private String cedula;

    @Column(name = "Nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "Apellido", length = 50, nullable = false)
    private String apellido;

    @Column(name = "Telefono", length = 20, nullable = false)
    private String telefono;

    @Column(name = "Email", length = 60, nullable = false)
    private String email;

    @Column(name = "Direccion", length = 120, nullable = false)
    private String direccion;

    @Column(name = "Ingresos", precision = 12, scale = 2, nullable = false)
    private BigDecimal ingresos;

    @Column(name = "Egresos", precision = 12, scale = 2, nullable = false)
    private BigDecimal egresos;

    @Column(name = "ActividadEconomica", length = 120, nullable = false)
    private String actividadEconomica;

    @Column(name = "Estado", length = 20, nullable = false)
    private String estado;

    @Version
    @Column(name = "Version", nullable = false)
    private Long version;

    public ClienteProspecto() {
    }

    public ClienteProspecto(Long idClienteProspecto) {
        this.idClienteProspecto = idClienteProspecto;
    }

    public Long getIdClienteProspecto() {
        return idClienteProspecto;
    }

    public void setIdClienteProspecto(Long idClienteProspecto) {
        this.idClienteProspecto = idClienteProspecto;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(String actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idClienteProspecto == null) ? 0 : idClienteProspecto.hashCode());
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
        ClienteProspecto other = (ClienteProspecto) obj;
        if (idClienteProspecto == null) {
            if (other.idClienteProspecto != null)
                return false;
        } else if (!idClienteProspecto.equals(other.idClienteProspecto))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClienteProspecto [idClienteProspecto=" + idClienteProspecto + ", cedula=" + cedula + ", nombre="
                + nombre + ", apellido=" + apellido + ", telefono=" + telefono + ", email=" + email + ", direccion="
                + direccion + ", ingresos=" + ingresos + ", egresos=" + egresos + ", actividadEconomica="
                + actividadEconomica + ", estado=" + estado + ", version=" + version + "]";
    }
} 