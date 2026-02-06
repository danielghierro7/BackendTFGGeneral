package org.example.backendtfggeneral.repositorios;

import org.example.backendtfggeneral.entidades.Ciudad;
import org.example.backendtfggeneral.entidades.Parada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParadaRepository extends JpaRepository<Parada, Long> {
    List<Parada> findParadaByCiudadId(Long ciudadId);
}
