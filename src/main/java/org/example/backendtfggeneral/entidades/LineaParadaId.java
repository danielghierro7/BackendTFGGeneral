package org.example.backendtfggeneral.entidades;



import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable // Indica que esta clase se "embutirá" dentro de otra entidad
public class LineaParadaId implements Serializable {

    // Deben llamarse igual que los @MapsId de la entidad LineaParada
    private Long idLinea;
    private Long idParada;

    // 1. Constructor vacío obligatorio para JPA
    public LineaParadaId() {}

    // 2. Constructor para facilitarte la vida al crearla
    public LineaParadaId(Long idLinea, Long idParada) {
        this.idLinea = idLinea;
        this.idParada = idParada;
    }

    // Getters y Setters
    public Long getIdLinea() { return idLinea; }
    public void setIdLinea(Long idLinea) { this.idLinea = idLinea; }

    public Long getIdParada() { return idParada; }
    public void setIdParada(Long idParada) { this.idParada = idParada; }

    // 3. EQUALS y HASHCODE obligatorios
    // JPA los usa para comparar si dos filas son la misma en memoria
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineaParadaId that = (LineaParadaId) o;
        return Objects.equals(idLinea, that.idLinea) &&
                Objects.equals(idParada, that.idParada);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLinea, idParada);
    }
}