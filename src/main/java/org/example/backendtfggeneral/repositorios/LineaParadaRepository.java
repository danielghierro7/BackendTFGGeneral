package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineaParadaRepository extends JpaRepository<LineaParada, LineaParadaId> {

    List<LineaParada> findLineaParadaById(LineaParadaId id);

    List<LineaParada> findById_IdLineaOrderByOrdenAsc(Long idLinea);
    @Query(value = "SELECT p.nombre FROM parada p " + // <--- Cambiado "paradas" por "parada"
            "JOIN linea_parada lp ON p.id = lp.id_parada " +
            "WHERE lp.id_linea = :idLinea " +
            "AND lp.orden = (SELECT MAX(orden) FROM linea_parada WHERE id_linea = :idLinea)",
            nativeQuery = true)
    Optional<String> obtenerNombreDestinoFinal(@Param("idLinea") Long idLinea);
    // NUEVO: Método para obtener el nombre de la parada encontrada por Spatial
    @Query("SELECT lp.parada.nombre FROM LineaParada lp WHERE lp.id.idParada = :idParada AND lp.id.idLinea = :idLinea")
    Optional<String> encontrarNombreParada(@Param("idParada") Long idParada, @Param("idLinea") Long idLinea);

    // Tu consulta de Oracle Spatial (Perfecta)
    @Query(value = "SELECT id_parada FROM ( " +
            "  SELECT lp.id_parada " +
            "  FROM linea_parada lp " +
            "  JOIN parada p ON lp.id_parada = p.id " +
            "  JOIN route r ON lp.id_linea = r.linea_bus_id " +
            "  WHERE lp.id_linea = :idLinea " +
            "  AND SDO_LRS.GET_MEASURE(SDO_LRS.PROJECT_PT(r.geom_lrs, p.geom)) > " +
            "      SDO_LRS.GET_MEASURE(SDO_LRS.PROJECT_PT(r.geom_lrs, " +
            "      SDO_GEOMETRY(2001, 8307, SDO_POINT_TYPE(:lon, :lat, NULL), NULL, NULL))) " +
            "  ORDER BY lp.orden ASC " +
            ") WHERE ROWNUM = 1", nativeQuery = true)
    Long encontrarSiguienteParadaId(@Param("idLinea") Long idLinea, @Param("lat") Double lat, @Param("lon") Double lon);
}