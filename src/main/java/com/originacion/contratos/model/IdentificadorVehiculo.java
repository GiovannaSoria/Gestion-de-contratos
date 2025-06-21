package com.originacion.contratos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "IdentificadoresVehiculos")
public class IdentificadorVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdIdentificadorVehiculo", nullable = false)
    private Long id;

    @Column(name = "VIN", length = 17, nullable = false, unique = true)
    private String vin;

    @Column(name = "NumeroMotor", length = 20, nullable = false, unique = true)
    private String numeroMotor;

    @Column(name = "Placa", length = 7, nullable = false, unique = true)
    private String placa;

    @Version
    private Long version;

    public IdentificadorVehiculo() {
    }

    public IdentificadorVehiculo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getNumeroMotor() {
        return numeroMotor;
    }

    public void setNumeroMotor(String numeroMotor) {
        this.numeroMotor = numeroMotor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
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
        IdentificadorVehiculo other = (IdentificadorVehiculo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IdentificadorVehiculo [id=" + id + ", vin=" + vin + ", numeroMotor=" + numeroMotor + ", placa=" + placa
                + "]";
    }

}
