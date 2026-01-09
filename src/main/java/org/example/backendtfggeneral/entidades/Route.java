package org.example.backendtfggeneral.entidades;
import jakarta.persistence.*;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "linea_bus_id")
    private LineaBus lineaBus;
}
