package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaParadaRepository extends JpaRepository<LineaParada, LineaParadaId> {

    List<LineaParada> findLineaParadaById(LineaParadaId id);
    List<LineaParada> findByParada_Nombre(String nombre);
    int getLineaParadaById(LineaParadaId id);
    // Spring parsea este nombre: busca en el ID el campo idLinea y ordena por 'orden'
    List<LineaParada> findById_IdLineaOrderByOrdenAsc(Long idLinea);

    List<LineaParada> findById_IdParada(Long idParada);
    @Query(value = "SELECT id_parada FROM (" +
            "  SELECT lp.id_parada " +
            "  FROM linea_parada lp " +
            "  JOIN parada p ON lp.id_parada = p.id " +
            "  JOIN route r ON lp.id_linea = r.linea_bus_id " +
            "  WHERE lp.id_linea = :idLinea " +
            "  AND SDO_LRS.GET_MEASURE(SDO_LRS.PROJECT_PT(r.geom_lrs, p.geom)) > " +
            "      SDO_LRS.GET_MEASURE(SDO_LRS.PROJECT_PT(r.geom_lrs, " +
            "      SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(:lon, :lat, NULL), NULL, NULL))) " +
            "  ORDER BY lp.orden ASC " +
            ") WHERE ROWNUM = 1", nativeQuery = true)
    Long encontrarSiguienteParadaId(@Param("idLinea") Long idLinea,
                                    @Param("lat") double lat,
                                    @Param("lon") double lon);
}
