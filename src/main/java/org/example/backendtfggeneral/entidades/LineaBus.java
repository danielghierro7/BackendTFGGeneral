package org.example.backendtfggeneral.entidades;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "linea_bus")
public class LineaBus {

    @Id
    private Long id;

    private String nombreLinea;

    @ManyToOne
    @JoinColumn(name = "ciudad_origen")
    private Ciudad ciudadOrigen;

    @ManyToOne
    @JoinColumn(name = "ciudad_destino")
    private Ciudad ciudadDestino;

    // NUEVO: Relación con las paradas
    @ManyToMany
    @JoinTable(
            name = "linea_parada", // El nombre de tu tabla intermedia
            joinColumns = @JoinColumn(name = "id_linea"),
            inverseJoinColumns = @JoinColumn(name = "id_parada")
    )
    @OrderBy("id ASC") // Ojo: Si quieres usar la columna 'orden', lo mejor es una consulta personalizada
    private List<Parada> paradas;

    // Getters y Setters para las paradas...
    public List<Parada> getParadas() {
        return paradas;
    }

    public void setParadas(List<Parada> paradas) {
        this.paradas = paradas;
    }

    // ... resto de getters y setters
}