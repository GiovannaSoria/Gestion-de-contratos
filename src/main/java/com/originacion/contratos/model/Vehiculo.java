package com.banquito.sistema.originacion.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "Vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdVehiculo", nullable = false)
    private Long id;

    @Column(name = "IdIdentificadorVehiculo", nullable = false, unique = true)
    private Long idIdentificadorVehiculo;

    @Column(name = "IdConcesionario", nullable = false)
    private Long idConcesionario;

    @Column(name = "Marca", length = 40, nullable = false)
    private String marca;

    @Column(name = "Modelo", length = 40, nullable = false)
    private String modelo;

    @Column(name = "Anio", length = 4, nullable = false)
    private Integer anio;

    @Column(name = "Valor", length = 10, precision = 2, nullable = false)
    private Double valor;

    @Column(name = "Color", length = 30, nullable = false)
    private String color;

    @Column(name = "Extras", length = 150, nullable = false)
    private String extras;

    @Column(name = "Estado", length = 20, nullable = false)
    private String estado;

    @Version
    private Long version;

    @OneToOne
    @JoinColumn(name = "IdIdentificadorVehiculo", referencedColumnName = "IdIdentificadorVehiculo", insertable = false, updatable = false)
    @JsonIgnoreProperties({
        "vin",
        "numeroMotor",
        "version"
    })
    private IdentificadorVehiculo identificadorVehiculo;

    @ManyToOne
    @JoinColumn(name = "IdConcesionario", referencedColumnName = "IdConcesionario", insertable = false, updatable = false)
    @JsonIgnoreProperties({
        "direccion",
        "telefono",
        "emailContacto",
        "estado",
        "version"
    })
    private Concesionario concesionario;

    public Vehiculo() {
    }

    public Vehiculo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdIdentificadorVehiculo() {
        return idIdentificadorVehiculo;
    }

    public void setIdIdentificadorVehiculo(Long idIdentificadorVehiculo) {
        this.idIdentificadorVehiculo = idIdentificadorVehiculo;
    }

    public Long getIdConcesionario() {
        return idConcesionario;
    }

    public void setIdConcesionario(Long idConcesionario) {
        this.idConcesionario = idConcesionario;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public IdentificadorVehiculo getIdentificadorVehiculo() {
        return identificadorVehiculo;
    }

    public void setIdentificadorVehiculo(IdentificadorVehiculo identificadorVehiculo) {
        this.identificadorVehiculo = identificadorVehiculo;
    }

    public Concesionario getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(Concesionario concesionario) {
        this.concesionario = concesionario;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
        Vehiculo other = (Vehiculo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Vehiculo [id=" + id + ", idIdentificadorVehiculo=" + idIdentificadorVehiculo + ", idConcesionario="
                + idConcesionario + ", marca=" + marca + ", modelo=" + modelo + ", anio=" + anio + ", valor=" + valor
                + ", color=" + color + ", extras=" + extras + ", identificadorVehiculo=" + identificadorVehiculo
                + ", concesionario=" + concesionario + "]";
    }

}
