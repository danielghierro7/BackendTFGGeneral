package org.example.backendtfggeneral.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "linea_parada")
public class LineaParada {

    public LineaParadaId getId() {
        return id;
    }

    public void setId(LineaParadaId id) {
        this.id = id;
    }

    public LineaBus getLinea() {
        return linea;
    }

    public void setLinea(LineaBus linea) {
        this.linea = linea;
    }

    public Parada getParada() {
        return parada;
    }

    public void setParada(Parada parada) {
        this.parada = parada;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getTiempoSiguienteMin() {
        return tiempoSiguienteMin;
    }

    public void setTiempoSiguienteMin(Integer tiempoSiguienteMin) {
        this.tiempoSiguienteMin = tiempoSiguienteMin;
    }

    public Double getDistanciaSiguienteKm() {
        return distanciaSiguienteKm;
    }

    public void setDistanciaSiguienteKm(Double distanciaSiguienteKm) {
        this.distanciaSiguienteKm = distanciaSiguienteKm;
    }

    @EmbeddedId
    private LineaParadaId id; // Necesitas una clase para la clave compuesta (id_linea + id_parada)

    @ManyToOne
    @MapsId("idLinea")
    @JoinColumn(name = "id_linea")
    private LineaBus linea;

    @ManyToOne
    @MapsId("idParada")
    @JoinColumn(name = "id_parada")
    private Parada parada;

    private Integer orden;

    @Column(name = "tiempo_siguiente_min")
    private Integer tiempoSiguienteMin;

    @Column(name = "distancia_siguiente_km")
    private Double distanciaSiguienteKm;

    // Getters y Setters...
}