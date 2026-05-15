package org.example.backendtfggeneral.beans;

public class ProximaParadaDto {
    private Double latitud;
    private Double longitud;
    private String proximaParada;
    private String busId;

    public ProximaParadaDto(Double latitud, Double longitud, String busId, String proximaParada) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.busId = busId;
        this.proximaParada = proximaParada;
    }

    // Getters necesarios para la serialización JSON
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
    public String getProximaParada() { return proximaParada; }
    public String getBusId() { return busId; }
}