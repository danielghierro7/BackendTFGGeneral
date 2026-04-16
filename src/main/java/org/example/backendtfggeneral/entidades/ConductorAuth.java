package org.example.backendtfggeneral.entidades;


import jakarta.persistence.*;


@Entity
@Table(name = "conductor_auth")
public class ConductorAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_id", unique = true)
    private String busId;

    @Column(name = "password")
    private String password;

    private String nombreConductor;

    public Long getIdLineaAsignada() {
        return idLineaAsignada;
    }

    public void setIdLineaAsignada(Long idLineaAsignada) {
        this.idLineaAsignada = idLineaAsignada;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long idLineaAsignada;

    // Getters y Setters...
}