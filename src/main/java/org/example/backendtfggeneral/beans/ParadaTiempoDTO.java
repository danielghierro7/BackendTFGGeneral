package org.example.backendtfggeneral.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar el tiempo de llegada de un bus a una parada específica.
 * Utilizado tanto para la vista de línea completa como para la IA.
 */
public class ParadaTiempoDTO {

    @JsonProperty("nombreParada") // Clarifica el nombre en el JSON
    private String nombre;

    private Integer minutos;
    private Integer orden;

    public ParadaTiempoDTO(String nombre, Integer minutos, Integer orden) {
        this.nombre = nombre;
        this.minutos = minutos;
        this.orden = orden;
    }

    // Getters
    public String getNombreParada() { return nombre; }
    public Integer getMinutosFaltantes() { return minutos; }
    public Integer getOrden() { return orden; }

    // Setters (Si los necesitas para frameworks de mapeo)
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setMinutos(Integer minutos) { this.minutos = minutos; }
    public void setOrden(Integer orden) { this.orden = orden; }
}