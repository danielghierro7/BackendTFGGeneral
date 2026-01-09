package org.example.backendtfggeneral.entidades;
import jakarta.persistence.*;

@Entity
@Table(name = "parada")
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double latitud;
    private Double longitud;

    @ManyToOne
    @JoinColumn(name = "id_ciudad")
    private Ciudad ciudad;
}

