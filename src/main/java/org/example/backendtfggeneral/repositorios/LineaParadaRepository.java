package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.LineaParada;
import org.example.backendtfggeneral.entidades.LineaParadaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineaParadaRepository extends JpaRepository<LineaParada, Long> {

    List<LineaParada> findLineaParadaById(LineaParadaId id);
    List<LineaParada> findByParada_Nombre(String nombre);
    int getLineaParadaById(LineaParadaId id);
    // Spring parsea este nombre: busca en el ID el campo idLinea y ordena por 'orden'
    List<LineaParada> findById_IdLineaOrderByOrdenAsc(Long idLinea);

    List<LineaParada> findById_IdParada(Long idParada);
}
