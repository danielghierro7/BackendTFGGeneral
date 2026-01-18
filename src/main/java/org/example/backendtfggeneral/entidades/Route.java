package org.example.backendtfggeneral.entidades;
import jakarta.persistence.*;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    public LineaBus getLineaBus() {
        return lineaBus;
    }

    public void setLineaBus(LineaBus lineaBus) {
        this.lineaBus = lineaBus;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "linea_bus_id")
    private LineaBus lineaBus;
}
