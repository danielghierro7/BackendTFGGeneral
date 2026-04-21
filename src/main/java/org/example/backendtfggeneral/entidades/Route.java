package org.example.backendtfggeneral.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Añadimos este campo para guardar la ruta real de OpenRouteService
    // Usamos @Column(columnDefinition = "CLOB") porque el JSON de una ruta larga
    // puede superar los 4000 caracteres de un VARCHAR2 normal en Oracle.
    @Lob
    @Column(name = "geojson_data")
    private String geojsonData;

    @ManyToOne
    @JoinColumn(name = "linea_bus_id")
    private LineaBus lineaBus;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getGeojsonData() { return geojsonData; }
    public void setGeojsonData(String geojsonData) { this.geojsonData = geojsonData; }

    public LineaBus getLineaBus() { return lineaBus; }
    public void setLineaBus(LineaBus lineaBus) { this.lineaBus = lineaBus; }
}