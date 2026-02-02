package org.example.backendtfggeneral.beans;


//Dto principalmente hecho para Elegir una linea de bus y ver cuanto le queda a cada parada
public class ParadaTiempoDTO {
    private String nombre;
    private Integer minutos;
    private Integer orden; // Añadir el orden ayuda mucho al frontend

    public ParadaTiempoDTO(String nombre, Integer minutos, Integer orden) {
        this.nombre = nombre;
        this.minutos = minutos;
        this.orden = orden;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public Integer getMinutos() { return minutos; }
    public Integer getOrden() { return orden; }
}