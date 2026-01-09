package org.example.backendtfggeneral.entidades;
import jakarta.persistence.*;

@Entity
@Table(name = "linea_bus")
public class LineaBus {

    @Id
    private Long id; // NO auto, lo metes tú (140, 141...)

    private String nombreLinea;

    @ManyToOne
    @JoinColumn(name = "ciudad_origen")
    private Ciudad ciudadOrigen;

    @ManyToOne
    @JoinColumn(name = "ciudad_destino")
    private Ciudad ciudadDestino;
}
