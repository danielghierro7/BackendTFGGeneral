package org.example.backendtfggeneral.entidades;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.example.backendtfggeneral.beans.Ubicacion;
import org.example.backendtfggeneral.entidades.LineaBus;
import java.util.List;

@Entity
@Table(name = "parada")
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //sus atributos no tienen tabla propia, mejor meterlo directamente como columnas de esta tabla de la entidad
    @Embedded
    private Ubicacion ubicacion;

    @ManyToOne
    @JoinColumn(name = "id_ciudad")
    private Ciudad ciudad;


    @ManyToMany(mappedBy = "paradas") // "paradas" es el nombre del campo en org.example.backendtfggeneral.entidades.LineaBus
    @JsonIgnore // Evita que al mostrar una parada se intenten cargar sus líneas y viceversa
    private List<LineaBus> lineas;


    private String nombre;

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    }

